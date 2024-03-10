package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import rocks.poopjournal.metadataremover.BuildConfig
import rocks.poopjournal.metadataremover.util.Logger
import java.io.File
import javax.inject.Inject

class GetFileUri @Inject constructor(
    private val context: Context
) {
    fun getUri(file: File) : Uri? {
        return try {
            Logger.d("Sharing '${file.absolutePath}'.")
            FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.files",
                file)
        } catch (e: IllegalArgumentException) {
            Logger.e(e, "The file can't be shared: ${file.name}")
            null
        }
    }
}