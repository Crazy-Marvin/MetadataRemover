package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import rocks.poopjournal.metadataremover.R
import javax.inject.Inject

class SaveImages @Inject constructor(
    private val context: Context
) {

    fun toDevice(filename: String, uri: Uri, callback: (String) -> Unit){
        val contentResolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, contentResolver.getType(uri))
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(
                R.string.app_name))
        }

        val uriToInsert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uriToInsert?.let {newUri ->
            contentResolver.openOutputStream(newUri).use {output ->
                output?.let {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(output)
                    }
                }
            }
        }

        callback(context.getString(R.string.saved_to_device_successfully))
    }
}