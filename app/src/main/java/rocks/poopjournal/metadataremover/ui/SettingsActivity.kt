package rocks.poopjournal.metadataremover.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivitySettingsBinding
import rocks.poopjournal.metadataremover.databinding.DialogErrorMonitorBinding
import rocks.poopjournal.metadataremover.util.SharedPrefUtil
import rocks.poopjournal.metadataremover.util.extensions.android.activity
import rocks.poopjournal.metadataremover.util.service.DataWiperService
import java.util.Locale
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefUtils: SharedPrefUtil

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                if (!hasMediaPermission()) mediaPermissionLauncher.launch(mediaPermission())
                else ensureFolderThenStart()
            } else {
                binding.switchDataWiper.isChecked = false
                prefUtils.setDataWiperEnabled(false)
                Toast.makeText(this, getString(R.string.notification_permission_required),
                    Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true
        prefUtils = SharedPrefUtil(this)
        binding.toolbar.setNavigationOnClickListener {
            NavUtils.navigateUpFromSameTask(activity)
        }
        binding.monitorInfoButton.setOnClickListener {
            showMaterialInfoDialog()
        }

        binding.switchSentry.isChecked = prefUtils.isSentryMonitorEnabled()


        binding.switchSentry.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // switch turned ON
                prefUtils.setSentryMonitorEnabled(true)
            } else {
                // switch turned OFF
                prefUtils.setSentryMonitorEnabled(false)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeList = AppCompatDelegate.getApplicationLocales()

            val currentLocaleName = if (!localeList.isEmpty) {
                AppCompatDelegate.getApplicationLocales()[0]?.displayName
            } else {
                Locale.getDefault().displayName
            }

            binding.cardLanguage.root.visibility = View.VISIBLE
            binding.cardLanguage.languajePicker.text = currentLocaleName
            binding.cardLanguage.languajePicker.setOnClickListener {
                val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        }

        // --- DataWiper ---
        binding.switchDataWiper.isChecked = prefUtils.isDataWiperEnabled()
        binding.switchBoot.isChecked = prefUtils.isDataWiperBootEnabled()
        val savedTree = prefUtils.getDataWiperTreeUri()
        binding.tvFolderPath.text =
            if (savedTree != null) displayNameForTree(savedTree.toUri())
            else "No folder selected"
// Enable/disable dependent cards
        val updateDependents = { enabled: Boolean ->
            binding.cardDataWiperBoot.alpha = if (enabled) 1f else 0.5f
            binding.cardDataWiperFolder.alpha = if (enabled) 1f else 0.5f
            binding.switchBoot.isEnabled = enabled
            binding.cardDataWiperFolder.isClickable = enabled
        }
        updateDependents(prefUtils.isDataWiperEnabled())

        binding.switchDataWiper.setOnCheckedChangeListener { _, isChecked ->
            prefUtils.setDataWiperEnabled(isChecked)
            updateDependents(isChecked)
            if (isChecked) proceedEnableFlow() else DataWiperService.stop(this)
        }

        binding.switchBoot.setOnCheckedChangeListener { _, isChecked ->
            prefUtils.setDataWiperBootEnabled(isChecked)
        }

        binding.dataWiperInfoButton.setOnClickListener {
            showDataWiperInfoDialog()
        }

        binding.cardDataWiperFolder.setOnClickListener {
            if (!prefUtils.isDataWiperEnabled()) return@setOnClickListener
            folderPickerLauncher.launch(null)
        }


    }

    private fun showMaterialInfoDialog() {
        val dialogBinding = DialogErrorMonitorBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private val folderPickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri ?: return@registerForActivityResult

            // Persist read + write so it survives reboot (needed for "run on boot")
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flags)

            // Store the URI itself — NOT a filesystem path
            prefUtils.setDataWiperTreeUri(uri.toString())
            binding.tvFolderPath.text = displayNameForTree(uri)

            if (prefUtils.isDataWiperEnabled()) {
                DataWiperService.stop(this)
                DataWiperService.start(this)
            }
        }

    private fun displayNameForTree(uri: Uri): String {
        val docId = DocumentsContract.getTreeDocumentId(uri)
        return docId.substringAfter(':', docId)
    }

    private fun proceedEnableFlow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                Toast.makeText(this,
                    "If no dialog appeared, enable notifications in Settings",
                    Toast.LENGTH_LONG).show()
            }
            return
        }
        if (!hasMediaPermission()) {
            mediaPermissionLauncher.launch(mediaPermission())
            return
        }
        ensureFolderThenStart()
    }


    private fun ensureFolderThenStart() {
        if (hasFolderGranted()) {
            DataWiperService.start(this)
        } else {
            Toast.makeText(this, "Pick a folder to monitor", Toast.LENGTH_SHORT).show()
            folderPickerLauncher.launch(null)
        }
    }

    private fun mediaPermission(): String =
        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    private fun hasMediaPermission(): Boolean =
        Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(this, mediaPermission()) ==
                PackageManager.PERMISSION_GRANTED

    private val mediaPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            ensureFolderThenStart()
        }

    private fun hasFolderGranted(): Boolean {
        val uriString = prefUtils.getDataWiperTreeUri() ?: return false
        val uri = Uri.parse(uriString)
        return contentResolver.persistedUriPermissions.any {
            it.uri == uri && it.isReadPermission && it.isWritePermission
        }
    }
    private fun showDataWiperInfoDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.datawiper_info_title)
            .setMessage(R.string.datawiper_info_message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

}
