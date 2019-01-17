package rocks.poopjournal.metadataremover.model.util

import rocks.poopjournal.metadataremover.metadata.handlers.NopMetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.MetadataReader
import rocks.poopjournal.metadataremover.model.metadata.MetadataWriter

/**
 * Construct a [MetadataHandler] using this [MetadataReader] for reading
 * and a [NopMetadataHandler] for writing.
 */
fun MetadataReader.toMetadataHandler(): MetadataHandler {
    return object : MetadataHandler,
            MetadataReader by this,
            MetadataWriter by NopMetadataHandler {
    }
}

/**
 * Construct a [MetadataHandler] using this [MetadataWriter] for writing
 * and a [NopMetadataHandler] for reading.
 */
fun MetadataWriter.toMetadataHandler(): MetadataHandler {
    return object : MetadataHandler,
            MetadataWriter by this,
            MetadataReader by NopMetadataHandler {
    }
}