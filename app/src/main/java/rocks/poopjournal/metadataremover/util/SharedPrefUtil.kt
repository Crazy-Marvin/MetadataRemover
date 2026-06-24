package rocks.poopjournal.metadataremover.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
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

    fun isDataWiperEnabled(): Boolean = sharedPreferences.getBoolean("datawiper_enabled", false)
    fun setDataWiperEnabled(enabled: Boolean) = sharedPreferences.edit {
        putBoolean(
            "datawiper_enabled",
            enabled
        )
    }

    fun isDataWiperBootEnabled(): Boolean = sharedPreferences.getBoolean("datawiper_run_on_boot", false)
    fun setDataWiperBootEnabled(enabled: Boolean) = sharedPreferences.edit {
        putBoolean(
            "datawiper_run_on_boot",
            enabled
        )
    }

    fun setDataWiperTreeUri(uri: String) =
        sharedPreferences.edit().putString("datawiper_tree_uri", uri).apply()

    fun getDataWiperTreeUri(): String? =
        sharedPreferences.getString("datawiper_tree_uri", null)


    companion object {
        const val PREF_NAME = "MyPrefs"
        const val KEY_MONITOR_MODE = "MonitorMode"

    }
}