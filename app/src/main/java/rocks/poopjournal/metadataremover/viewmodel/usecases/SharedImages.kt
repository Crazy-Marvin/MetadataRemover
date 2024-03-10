package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.Context
import javax.inject.Inject

class SharedImages @Inject constructor(
    private val context: Context
) {

    val dir by lazy {
        context.filesDir
            .resolve("images")
            .apply { mkdirs() }
    }
}