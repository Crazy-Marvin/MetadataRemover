package rocks.poopjournal.metadataremover.util.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rocks.poopjournal.metadataremover.util.SharedPrefUtil

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = SharedPrefUtil(context)
            if (prefs.isDataWiperEnabled() && prefs.isDataWiperBootEnabled()) {
                DataWiperService.start(context)
            }
        }
    }
}