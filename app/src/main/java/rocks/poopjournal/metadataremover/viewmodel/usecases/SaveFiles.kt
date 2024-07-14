package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import rocks.poopjournal.metadataremover.R
import javax.inject.Inject

class SaveFiles @Inject constructor(
    private val context: Context
) {

    fun toDevice(filename: String, uri: Uri, callback: (String) -> Unit) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, getRelativePath(mimeType))
        }

        val collection = when {
            mimeType?.startsWith("image/") == true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> throw IllegalArgumentException("Unsupported MIME type: $mimeType")
        }

        val uriToInsert = contentResolver.insert(collection, contentValues)

        uriToInsert?.let { newUri ->
            contentResolver.openOutputStream(newUri).use { output ->
                output?.let {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(output)
                    }
                }
            }
        }

        callback(context.getString(R.string.saved_to_device_successfully))
    }

    private fun getRelativePath(mimeType: String?): String {
        val directory = when {
            mimeType?.startsWith("image/") == true -> Environment.DIRECTORY_PICTURES
            mimeType?.startsWith("video/") == true -> Environment.DIRECTORY_MOVIES
            else -> Environment.DIRECTORY_DOWNLOADS
        }
        return "$directory/${context.getString(R.string.app_name)}"
    }
}