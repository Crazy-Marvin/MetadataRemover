package rocks.poopjournal.metadataremover.util.extensions.android

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.CancellationSignal
import java.io.File

fun Uri.openAssetFileDescriptor(
        context: Context,
        mode: String = "r",
        cancellationSignal: CancellationSignal? = null
): AssetFileDescriptor? =
        context.contentResolver.openAssetFileDescriptor(this, mode, cancellationSignal)

fun AssetFileDescriptor.copyTo(file: File) {
    createInputStream().use { input ->
        file.outputStream().use { input.copyTo(it) }
    }
}

fun AssetFileDescriptor.copyFrom(file: File) {
    file.inputStream().use { input ->
        createOutputStream().use { input.copyTo(it) }
    }
}

fun File.copyFrom(fileDescriptor: AssetFileDescriptor) = fileDescriptor.copyTo(this)
