package rocks.poopjournal.metadataremover.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefUtil(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    fun setSentryMonitorEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_MONITOR_MODE, enabled) }
    }

    fun isSentryMonitorEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_MONITOR_MODE, false)
    }


    companion object {
        const val PREF_NAME = "MyPrefs"
        const val KEY_MONITOR_MODE = "MonitorMode"

    }
}