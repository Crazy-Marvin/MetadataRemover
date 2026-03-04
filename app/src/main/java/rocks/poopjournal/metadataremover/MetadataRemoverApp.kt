package rocks.poopjournal.metadataremover

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import rocks.poopjournal.metadataremover.util.SharedPrefUtil

@HiltAndroidApp
class MetadataRemoverApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val pref = SharedPrefUtil(this)

        if (pref.isSentryMonitorEnabled()) {
            enableSentry()
        } else {
            disableSentry()
        }
    }
    private fun enableSentry() {
        SentryAndroid.init(this) { options ->
            // DSN will be read automatically from manifest
            options.isEnableUserInteractionTracing = true
            options.isAttachScreenshot = true
            options.isAttachViewHierarchy = true
        }
    }

    private fun disableSentry() {
        io.sentry.Sentry.close()
    }
}