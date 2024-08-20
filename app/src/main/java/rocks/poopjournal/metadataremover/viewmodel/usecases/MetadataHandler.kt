package rocks.poopjournal.metadataremover.viewmodel.usecases

import android.content.Context
import rocks.poopjournal.metadataremover.metadata.handlers.ApplyAllMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.DocumentMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.ExifMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.FirstMatchMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.PngMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.AudioVideoMetadataHandler
import javax.inject.Inject

class MetadataHandler @Inject constructor(
    private val context: Context
) {

     val handler = FirstMatchMetadataHandler(
        ApplyAllMetadataHandler(
            ExifMetadataHandler(context),
//          DrewMetadataReader.toMetadataHandler(),
            PngMetadataHandler,
            AudioVideoMetadataHandler(context),
            DocumentMetadataHandler(context)
        )
//      , NopMetadataHandler // For testing purposes only. TODO Remove after testing.
    )
}