package rocks.poopjournal.metadataremover.util.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.*
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.metadata.handlers.*
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.util.SharedPrefUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class DataWiperService : Service() {

    private lateinit var metadataHandler: MetadataHandler
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val processingUris: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val cleanedUris: MutableSet<String> = ConcurrentHashMap.newKeySet()

    private var treeUri: Uri? = null
    private var observerThread: HandlerThread? = null
    private var observerHandler: Handler? = null
    private var mediaObserver: ContentObserver? = null
    private var pendingScan: Runnable? = null

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "datawiper_channel"

        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context, Intent(context, DataWiperService::class.java)
            )
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, DataWiperService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        metadataHandler = ApplyAllMetadataHandler(
            PngMetadataHandler,
            NopMetadataHandler,
            AudioVideoMetadataHandler(this),
            DocumentMetadataHandler(this),
            ExifMetadataHandler(this)
        )
        createNotificationChannel()
        startForegroundCompat(buildNotification("DataWiper is watching..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopSelf()
            return START_NOT_STICKY
        }

        val uriString = SharedPrefUtil(this).getDataWiperTreeUri()
        if (uriString.isNullOrEmpty()) {
            Log.d("DataWiper", "No folder granted via SAF. Stopping.")
            stopSelf()
            return START_NOT_STICKY
        }

        val uri = Uri.parse(uriString)
        treeUri = uri

        // Confirm we still hold the persisted read+write grant
        val hasPerm = contentResolver.persistedUriPermissions.any {
            it.uri == uri && it.isReadPermission && it.isWritePermission
        }
        if (!hasPerm) {
            Log.d("DataWiper", "Lost persisted permission for $uri. Re-pick the folder.")
            stopSelf()
            return START_NOT_STICKY
        }

        serviceScope.launch { scanTree() }   // process existing files
        registerMediaObserver()              // trigger on new files

        Log.d("DataWiper", "Watching tree: $uri")
        return START_STICKY
    }

    private fun registerMediaObserver() {
        if (mediaObserver != null) return
        val thread = HandlerThread("datawiper-observer").also { it.start() }
        observerThread = thread
        val handler = Handler(thread.looper)
        observerHandler = handler

        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                // debounce: rescan ~1.2s after the last change burst
                pendingScan?.let { handler.removeCallbacks(it) }
                val r = Runnable { serviceScope.launch { scanTree() } }
                pendingScan = r
                handler.postDelayed(r, 1200)
            }
        }
        mediaObserver = observer
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer
        )
    }

    private suspend fun scanTree() {
        val uri = treeUri ?: return
        val tree = DocumentFile.fromTreeUri(this, uri)
        if (tree == null || !tree.canRead()) {
            Log.d("DataWiper", "Cannot read tree: $uri")
            return
        }
        val children = tree.listFiles()
        Log.d("DataWiper", "Tree children: ${children.size}")
        val supported = children.filter { isSupported(it) }
        Log.d("DataWiper", "Supported docs: ${supported.size}")
        supported.forEach { processDocument(it) }
        updateNotification("Scan complete. Watching for new files...")
    }

    private suspend fun processDocument(doc: DocumentFile) {
        val key = doc.uri.toString()
        if (key in cleanedUris) return
        if (!processingUris.add(key)) return

        var inputFile: File? = null
        var outputFile: File? = null
        try {
            if (!doc.exists() || doc.length() == 0L) return

            val mediaType = getMediaType(doc.name) ?: run {
                Log.d("DataWiper", "Unsupported: ${doc.name}")
                return
            }
            if (mediaType !in metadataHandler.writableMimeTypes) return

            // 1) copy SAF document -> cache input file
            inputFile = File(cacheDir, "dw_in_${System.nanoTime()}")
            val copied = contentResolver.openInputStream(doc.uri)?.use { input ->
                inputFile!!.outputStream().use { out -> input.copyTo(out) }; true
            } ?: false
            if (!copied) return

            // 2) inspect metadata
            val metadata = metadataHandler.loadMetadata(mediaType, inputFile) ?: return
            val removable = metadata.attributes.filter { it.removable }
            if (removable.isEmpty()) {
                cleanedUris.add(key)
                return
            }
            Log.d("DataWiper", "Found ${removable.size} removable attrs in ${doc.name}")

            // 3) write cleaned copy into a separate cache file
            outputFile = File(cacheDir, "dw_out_${System.nanoTime()}")
            outputFile.createNewFile()
            val ok = metadataHandler.removeMetadata(
                mediaType, inputFile, outputFile, removable.toList()
            )
            if (!ok || outputFile.length() == 0L) {
                Log.d("DataWiper", "Failed to clean ${doc.name}")
                return
            }

            // 4) overwrite the original document in place ("wt" truncates)
            val wrote = contentResolver.openOutputStream(doc.uri, "wt")?.use { out ->
                outputFile!!.inputStream().use { it.copyTo(out) }; true
            } ?: false
            if (!wrote) {
                Log.d("DataWiper", "Could not open output for ${doc.name}")
                return
            }

            cleanedUris.add(key)
            Log.d("DataWiper", "Cleaned ${doc.name}")
            updateNotification("Cleaned: ${doc.name}")
        } catch (e: Exception) {
            Log.e("DataWiper", "Error processing ${doc.name}: ${e.message}")
        } finally {
            inputFile?.delete()
            outputFile?.delete()
            processingUris.remove(key)
        }
    }

    private fun isSupported(doc: DocumentFile): Boolean {
        if (!doc.isFile) return false
        val name = doc.name ?: return false
        if (name.startsWith(".")) return false
        return getMediaType(name) != null
    }

    private fun getMediaType(name: String?): MediaType? {
        if (name.isNullOrEmpty()) return null
        val ext = name.substringAfterLast('.', "").lowercase()
        if (ext.isEmpty()) return null
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: return null
        val mediaType = MediaType.parse(mime) ?: return null
        return if (mediaType in metadataHandler.writableMimeTypes) mediaType else null
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "DataWiper Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors a folder and removes metadata"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        val openIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, DataWiperService::class.java).apply { action = "STOP" },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Metadata DataWiper")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(R.drawable.ic_close, "Stop", stopIntent)
            .build()
    }

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        mediaObserver?.let { contentResolver.unregisterContentObserver(it) }
        observerThread?.quitSafely()
        serviceScope.cancel()
        Log.d("DataWiper", "Service stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}