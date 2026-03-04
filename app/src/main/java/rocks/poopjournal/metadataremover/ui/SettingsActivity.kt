package rocks.poopjournal.metadataremover.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.databinding.ActivitySettingsBinding
import rocks.poopjournal.metadataremover.databinding.DialogErrorMonitorBinding
import rocks.poopjournal.metadataremover.util.SharedPrefUtil
import rocks.poopjournal.metadataremover.util.extensions.android.activity

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefUtils: SharedPrefUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

}
