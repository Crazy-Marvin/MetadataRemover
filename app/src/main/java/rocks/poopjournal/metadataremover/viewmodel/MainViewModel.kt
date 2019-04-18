/*
 * MIT License
 *
 * Copyright (c) 2018 Jan Heinrich Reimer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rocks.poopjournal.metadataremover.viewmodel

import android.app.Application
import android.content.Intent
import android.content.res.AssetFileDescriptor
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rocks.poopjournal.metadataremover.BuildConfig
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.metadata.handlers.ApplyAllMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.ExifMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.FirstMatchMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.PngMetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.Resource
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.ui.AboutActivity
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.ActivityResultLauncher
import rocks.poopjournal.metadataremover.util.FileOpener
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.*
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.applicationContext
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.mutableLiveDataOf
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.singleLiveEventOf
import rocks.poopjournal.metadataremover.util.extensions.android.copyFrom
import java.io.Closeable

class MainViewModel(application: Application) :
        AndroidViewModel(application), MetadataViewModel, ActivityLauncherViewModel,
        ActivityResultLauncherViewModel, ActivityResultLauncher {

    private val sharedImagesDirectory by lazy {
        applicationContext.filesDir
                .resolve("images")
                .apply { mkdirs() }
    }

    data class WrongMimeTypeFileSelectedHint(
            val mediaType: MediaType,
            val allowedMediaTypes: Set<MediaType>
    )

    override val metadata = mutableLiveDataOf<Resource<Metadata>>(Resource.Empty())
    val wrongMimeTypeFileSelectedHint = singleLiveEventOf<WrongMimeTypeFileSelectedHint>()
    override val activityLaunchInfo = singleLiveEventOf<ActivityLauncher.LaunchInfo>()
    override val activityResultLaunchInfo = singleLiveEventOf<ActivityResultLauncher.LaunchInfo>()

    private val metadataHandler = FirstMatchMetadataHandler(
            ApplyAllMetadataHandler(
                    ExifMetadataHandler(applicationContext),
//                    DrewMetadataReader.toMetadataHandler(),
                    PngMetadataHandler
            )
//            , NopMetadataHandler // For testing purposes only. TODO Remove after testing.
    )

    // For now we'll restrict file opening to mime types we can read AND write to.
    // Later we might add support for just reading metadata for formats we can't write to.
    private val allowedMimeTypes
        get() = metadataHandler.writableMimeTypes intersect metadataHandler.readableMimeTypes

    private var filePicker = FileOpener(
            applicationContext,
            this,
            ::openFile,
            ::onNoFileChooserInstalled,
            ::onWrongMimeTypeFileSelected)

    private var fileView: FileView? = null

    private fun onNoFileChooserInstalled() {
        // Search the Play store for a file explorer.
        val searchText = applicationContext
                .getString(R.string.search_text_file_explorer)
        val searchUri = "https://play.google.com/store/search"
                .parseUri()
                .buildUpon()
                .appendQueryParameter("c", "apps")
                .appendQueryParameter("q", searchText)
                .build()
        val searchIntent = Intent(Intent.ACTION_VIEW)
                .setData(searchUri)

        startActivity(searchIntent)
    }

    private fun onWrongMimeTypeFileSelected(mediaType: MediaType,
            allowedMediaTypes: Set<MediaType>) {
        GlobalScope.launch(Dispatchers.Main) {
            wrongMimeTypeFileSelectedHint.value =
                    WrongMimeTypeFileSelectedHint(mediaType, allowedMediaTypes)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GlobalScope.launch(Dispatchers.Main) {
            filePicker.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun startActivityForResult(launchInfo: ActivityResultLauncher.LaunchInfo) {
        GlobalScope.launch(Dispatchers.Main) {
            activityResultLaunchInfo.value = launchInfo
        }
    }

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) {
        GlobalScope.launch(Dispatchers.Main) {
            activityLaunchInfo.value = launchInfo
        }
    }

    fun openAboutScreen() {
        val aboutIntent = Intent(getApplication(), AboutActivity::class.java)
        startActivity(aboutIntent)
    }

    fun openFile() {
        GlobalScope.launch(Dispatchers.Main) {
            metadata.value = Resource.Loading()
        }
        filePicker.openFile(allowedMimeTypes)
    }

    private inner class FileView(
            val descriptor: AssetFileDescriptor,
            val name: String,
            val mediaType: MediaType
    ) : Closeable {

        val original = sharedImagesDirectory
                .resolve(name)
                .apply {
                    createNewFile()
                    copyFrom(descriptor)
                }

        val nameWithoutExtension = original.nameWithoutExtension

        val output = sharedImagesDirectory
                .resolve("${nameWithoutExtension}_no_metadata" +
                        if (original.extension.isNotEmpty()) ".${original.extension}" else "")
                .apply {
                    createNewFile()
                }

        val isMetadataRemoved
            get() = output.size != 0L

        override fun close() {
            // Copy file output to descriptor, then close it.
            if ('w' in filePicker.mode) {
                descriptor.copyFrom(output)
            }
            descriptor.close()

            // Delete temp files.
            original.delete()
            output.delete()
        }
    }

    private suspend fun openFile(descriptor: AssetFileDescriptor, name: String,
            mediaType: MediaType) {
        Logger.d("Opening file '$name' to display metadata...")

        fileView = FileView(descriptor, name, mediaType)
        loadMetadata()
    }

    private suspend fun loadMetadata() {
        val fileView = fileView ?: return

        val inputMetadata = metadataHandler.loadMetadata(fileView.mediaType, fileView.original)
        if (inputMetadata == null) {
            Logger.d("Couldn't load metadata for file '${fileView.name}'. " +
                    "Maybe the file extension '${fileView.original.extension}' is not supported.")
            // If the file couldn't be read, immediately close it.
            closeFile()

            GlobalScope.launch(Dispatchers.Main) {
                metadata.value = Resource.Empty() // TODO Show a message that no metadata could be found.
            }

            return
        }

        val fileSystemAttributes = listOfNotNull(
                fileView.original.nameAttribute,
//                fileView.original.lastModifiedAttribute, // TODO Remove this.
                fileView.original.sizeAttribute
        )
        val outputMetadata = inputMetadata.copy(
                title = inputMetadata.title ?: Text(fileView.nameWithoutExtension),
                attributes = inputMetadata.attributes + fileSystemAttributes
        )

        GlobalScope.launch(Dispatchers.Main) {
            metadata.value = Resource.Success(outputMetadata)
        }
    }

    fun closeFile() {
        GlobalScope.launch(Dispatchers.Main) {
            metadata.value = Resource.Empty()
        }

        fileView?.close()
        fileView = null
    }

    fun removeMetadata() {
        val fileView = fileView ?: return

        if (fileView.isMetadataRemoved) {
            shareOutputFile()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            metadataHandler.removeMetadata(
                    fileView.mediaType,
                    fileView.original,
                    fileView.output
            )
            fileView.output.setLastModified(0)
            shareOutputFile()
        }
    }

    // TODO replace by just exiting with positive response.
    private fun shareOutputFile() {
        val fileView = fileView ?: return
        if (!fileView.isMetadataRemoved) return

        val fileUri =
                try {
                    Logger.d("Sharing '${fileView.output.absolutePath}'.")
                    FileProvider.getUriForFile(
                            applicationContext,
                            "${BuildConfig.APPLICATION_ID}.files",
                            fileView.output)
                } catch (e: IllegalArgumentException) {
                    Logger.e(e, "The file can't be shared: ${fileView.output.name}")
                    null
                }

        if (fileUri != null) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            sendIntent.setDataAndType(fileUri, applicationContext.contentResolver.getType(fileUri))

            val chooserIntent = Intent.createChooser(
                    sendIntent,
                    applicationContext.getString(
                            R.string.title_intent_chooser_share_without_metadata,
                            fileView.name))

            startActivity(chooserIntent)
        }
    }
}