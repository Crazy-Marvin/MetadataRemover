package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.getFileName
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.getMediaType
import rocks.poopjournal.metadataremover.util.extensions.android.openAssetFileDescriptor
import javax.inject.Inject

class GetDescriptor @Inject constructor(
    private val context: Context
) {

    fun openFile(uri: Uri, onResult:(file: AssetFileDescriptor, displayName: String, mediaType: MediaType) -> Unit) {

        val mediaType = uri.getMediaType(context)
        if (mediaType == null) {
            Logger.w("Could not guess MIME type from file '${uri.path}'.")
            return
        }

        val fileName = uri.getFileName(context)?.takeIf(String::isNotBlank)

        if (fileName == null) {
            Logger.w("Could not get name from file '$uri'.")
            return
        }

        val descriptor: AssetFileDescriptor = uri.openAssetFileDescriptor(context, "r") ?: return
        onResult(descriptor, fileName, mediaType)
        return
    }
}