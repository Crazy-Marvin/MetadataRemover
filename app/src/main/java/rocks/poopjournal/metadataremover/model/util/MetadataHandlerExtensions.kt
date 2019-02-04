package rocks.poopjournal.metadataremover.model.util

import rocks.poopjournal.metadataremover.metadata.handlers.NopMetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.MetadataReader
import rocks.poopjournal.metadataremover.model.metadata.MetadataWriter

/**
 * Construct a [MetadataHandler] using this [MetadataReader] for reading
 * and a [NopMetadataHandler] for writing.
 */
fun MetadataReader.toMetadataHandler(): MetadataHandler =
        MetadataReaderDelegateHandler(this)

private class MetadataReaderDelegateHandler(val delegate: MetadataReader) :
        MetadataHandler,
        MetadataReader by delegate,
        MetadataWriter by NopMetadataHandler

/**
 * Construct a [MetadataHandler] using this [MetadataWriter] for writing
 * and a [NopMetadataHandler] for reading.
 */
fun MetadataWriter.toMetadataHandler(): MetadataHandler =
        MetadataWriterDelegateHandler(this)

private class MetadataWriterDelegateHandler(val delegate: MetadataWriter) :
        MetadataHandler,
        MetadataWriter by delegate,
        MetadataReader by NopMetadataHandler
