package rocks.poopjournal.metadataremover.metadata.handlers

import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.extensions.MimeType
import java.io.File

class DummyMetadataHandler : MetadataHandler {

    override val writableMimeTypes = setOf("image/jpeg", "image/jpg")
    override val readableMimeTypes = setOf("image/jpeg", "image/jpg")

    override suspend fun loadMetadata(mimeType: MimeType, inputFile: File): Metadata? {
        val thumbnail = Image(inputFile)

        val dummyAttribute = Metadata.Attribute(
                Text(R.string.label_attribute_file_size),
                Image(R.drawable.ic_landscape),
                Text("Size: ${inputFile.totalSpace}"),
                Text("Name: ${inputFile.name}"))

        val dummyAttribute2 = Metadata.Attribute(
                Text(R.string.label_attribute_file_last_modification_date_time),
                Image(R.drawable.ic_event),
                Text("LM: ${inputFile.lastModified()}"))

        return Metadata(
                thumbnail = thumbnail,
                attributes = setOf(dummyAttribute, dummyAttribute2)
        )
    }

    override suspend fun removeMetadata(mimeType: MimeType, inputFile: File,
                                        outputFile: File): Boolean {

        // Just copy the original file
        inputFile.copyTo(outputFile, overwrite = true)

        return true
    }
}

