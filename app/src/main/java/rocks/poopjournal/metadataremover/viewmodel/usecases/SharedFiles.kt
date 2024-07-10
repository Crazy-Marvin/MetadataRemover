package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.Context
import javax.inject.Inject

class SharedFiles @Inject constructor(
    private val context: Context
) {

    val dir by lazy {
        context.filesDir
            .resolve("metadata-remover-files")
            .apply { mkdirs() }
    }
}