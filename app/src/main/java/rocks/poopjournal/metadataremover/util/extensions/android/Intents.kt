/*
 * MIT License
 *
 * Copyright (c) 2017 Jan Heinrich Reimer
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

package rocks.poopjournal.metadataremover.util.extensions.android

import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable

/**
 * Create an [Intent] for the component specified by [ComponentType].
 */
@JvmName("intent")
inline fun <reified ComponentType> Context.intent(): Intent = Intent(this, ComponentType::class.java)

/**
 * Start an [Activity] specified by [ComponentType].
 */
inline fun <reified ComponentType : Activity> Context.startActivity() {
    startActivity<ComponentType> {}
}

/**
 * Start an [Activity] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified ComponentType : Activity> Context.startActivity(
        options: Bundle? = null) {
    startActivity<ComponentType>(options) {}
}

/**
 * Send a [broadcast][BroadcastReceiver] specified by [ComponentType].
 */
inline fun <reified ComponentType : BroadcastReceiver> Context.sendBroadcast(
        receiverPermission: String? = null) {
    sendBroadcast<ComponentType>(receiverPermission) {}
}

/**
 * Start a [Service] specified by [ComponentType].
 */
inline fun <reified ComponentType : Service> Context.startService() {
    startService<ComponentType> {}
}

/**
 * Start a [foreground service][Service] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
inline fun <reified ComponentType : Service> Context.startForegroundService() {
    startForegroundService<ComponentType> {}
}

/**
 * Start an [Activity] specified by [ComponentType].
 */
inline fun <reified ComponentType : Activity> Context.startActivity(
        init: Intent.() -> Unit) {
    val intent = intent<ComponentType>()
    intent.init()
    startActivity(intent)
}

/**
 * Start an [Activity] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified ComponentType : Activity> Context.startActivity(
        options: Bundle? = null,
        init: Intent.() -> Unit) {
    val intent = intent<ComponentType>()
    intent.init()
    startActivity(intent, options)
}

/**
 * Send a [broadcast][BroadcastReceiver] specified by [ComponentType].
 */
inline fun <reified ComponentType : BroadcastReceiver> Context.sendBroadcast(
        receiverPermission: String? = null,
        init: Intent.() -> Unit) {
    val intent = intent<ComponentType>()
    intent.init()
    sendBroadcast(intent, receiverPermission)
}

/**
 * Start a [Service] specified by [ComponentType].
 */
inline fun <reified ComponentType : Service> Context.startService(
        init: Intent.() -> Unit) {
    val intent = intent<ComponentType>()
    intent.init()
    startService(intent)
}

/**
 * Start a [foreground service][Service] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
inline fun <reified ComponentType : Service> Context.startForegroundService(
        init: Intent.() -> Unit) {
    val intent = intent<ComponentType>()
    intent.init()
    startForegroundService(intent)
}

/**
 * Get a [PendingIntent] starting an [Activity].
 */
fun Context.pendingActivityIntent(
        requestCode: Int,
        intent: Intent,
        flags: Int): PendingIntent? =
        PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                flags)

/**
 * Get a [PendingIntent] starting an [Activity].
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun Context.pendingActivityIntent(
        requestCode: Int,
        intent: Intent,
        flags: Int,
        options: Bundle? = null): PendingIntent? =
        PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                flags,
                options)

/**
 * Get a [PendingIntent] starting multiple [Activity]s.
 */
fun Context.pendingActivitiesIntent(
        requestCode: Int,
        intents: Array<out Intent>,
        flags: Int): PendingIntent? =
        PendingIntent.getActivities(
                this,
                requestCode,
                intents,
                flags)

/**
 * Get a [PendingIntent] starting multiple [Activity]s.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun Context.pendingActivitiesIntent(
        requestCode: Int,
        intents: Array<out Intent>,
        flags: Int,
        options: Bundle? = null): PendingIntent? =
        PendingIntent.getActivities(
                this,
                requestCode,
                intents,
                flags,
                options)

/**
 * Get a [PendingIntent] starting a [broadcast][BroadcastReceiver].
 */
fun Context.pendingBroadcastIntent(
        requestCode: Int,
        intent: Intent,
        flags: Int): PendingIntent? =
        PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                flags)

/**
 * Get a [PendingIntent] starting a [Service].
 */
fun Context.pendingServiceIntent(
        requestCode: Int,
        intent: Intent,
        flags: Int): PendingIntent? =
        PendingIntent.getService(
                this,
                requestCode,
                intent,
                flags)

/**
 * Get a [PendingIntent] starting a [foreground service][Service].
 */
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
fun Context.pendingForegroundServiceIntent(
        requestCode: Int,
        intent: Intent,
        flags: Int): PendingIntent? =
        PendingIntent.getForegroundService(
                this,
                requestCode,
                intent,
                flags)

/**
 * Get a [PendingIntent] starting an [Activity] specified by [ComponentType].
 */
inline fun <reified ComponentType : Activity> Context.pendingActivityIntent(
        requestCode: Int,
        flags: Int): PendingIntent? =
        pendingActivityIntent(
                requestCode,
                intent<ComponentType>(),
                flags)

/**
 * Get a [PendingIntent] starting an [Activity] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
inline fun <reified ComponentType : Activity> Context.pendingActivityIntent(
        requestCode: Int,
        flags: Int,
        options: Bundle? = null): PendingIntent? =
        pendingActivityIntent(
                requestCode,
                intent<ComponentType>(),
                flags,
                options)

/**
 * Get a [PendingIntent] starting a [broadcast][BroadcastReceiver] specified by [ComponentType].
 */
inline fun <reified ComponentType : BroadcastReceiver> Context.pendingBroadcastIntent(
        requestCode: Int,
        flags: Int): PendingIntent? =
        pendingBroadcastIntent(
                requestCode,
                intent<ComponentType>(),
                flags)

/**
 * Get a [PendingIntent] starting a [Service] specified by [ComponentType].
 */
inline fun <reified ComponentType : Service> Context.pendingServiceIntent(
        requestCode: Int,
        flags: Int): PendingIntent? =
        pendingServiceIntent(
                requestCode,
                intent<ComponentType>(),
                flags)

/**
 * Get a [PendingIntent] starting a [foreground service][Service] specified by [ComponentType].
 */
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
inline fun <reified ComponentType : Service> Context.pendingForegroundServiceIntent(
        requestCode: Int,
        flags: Int): PendingIntent? =
        pendingForegroundServiceIntent(
                requestCode,
                intent<ComponentType>(),
                flags)

operator fun Intent.set(name: String, value: Boolean): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Byte): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Char): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Short): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Int): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Long): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Float): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Double): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: String): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: CharSequence): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Parcelable): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<Parcelable>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Serializable): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: BooleanArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: ByteArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: ShortArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: CharArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: IntArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: LongArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: FloatArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: DoubleArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<String>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<CharSequence>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Bundle): Intent = putExtra(name, value)
