package rocks.poopjournal.metadataremover.model.metadata

import android.net.Uri

data class ClearedFile (
    val uri: Uri,
    val name: String
)