package rocks.poopjournal.metadataremover.util.extensions.pngj

import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.PngWriter
import ar.com.hjg.pngj.chunks.PngChunkTIME
import ar.com.hjg.pngj.chunks.PngChunkTextVar
import ar.com.hjg.pngj.chunks.PngMetadata
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.TimeZones
import rocks.poopjournal.metadataremover.util.extensions.ATTRIBUTE_DATE_FORMAT
import rocks.poopjournal.metadataremover.util.extensions.ATTRIBUTE_TIME_FORMAT
import rocks.poopjournal.metadataremover.util.extensions.toRfc822CalendarOrNull
import java.util.*


fun PngChunkTIME.toCalendar(): Calendar {
    val ymdhms = ymdhms
    return Calendar.getInstance(TimeZones.UTC).also { calendar ->
        calendar[Calendar.YEAR] = ymdhms[0]
        calendar[Calendar.MONTH] = ymdhms[1] - 1
        calendar[Calendar.DAY_OF_MONTH] = ymdhms[2]
        calendar[Calendar.HOUR_OF_DAY] = ymdhms[3]
        calendar[Calendar.MINUTE] = ymdhms[4]
        calendar[Calendar.SECOND] = ymdhms[5]
    }
}

fun PngReader.copyRowsTo(writer: PngWriter) {
    while (hasMoreRows()) {
        writer.writeRow(readRow())
    }
}

val PngMetadata.creationAttribute: Metadata.Attribute?
    get() {
        val creationDate = getTxtForKey(PngChunkTextVar.KEY_Creation_Time)
                ?.takeIf(String::isNotBlank)
                ?.toRfc822CalendarOrNull()
                ?: return null
        return Metadata.Attribute(
                label = Text(R.string.label_attribute_file_creation_date_time),
                icon = Image(R.drawable.ic_event),
                primaryValue = Text(ATTRIBUTE_DATE_FORMAT.format(creationDate)),
                secondaryValue = Text(ATTRIBUTE_TIME_FORMAT.format(creationDate))
        )
    }

val PngMetadata.lastModifiedAttribute: Metadata.Attribute?
    get() {
        val lastModifiedDate = time?.toCalendar() ?: return null
        return Metadata.Attribute(
                label = Text(R.string.label_attribute_file_last_modification_date_time),
                icon = Image(R.drawable.ic_history),
                primaryValue = Text(ATTRIBUTE_DATE_FORMAT.format(lastModifiedDate)),
                secondaryValue = Text(ATTRIBUTE_TIME_FORMAT.format(lastModifiedDate))
        )
    }

val PngMetadata.authorAttribute: Metadata.Attribute?
    get() {
        val author: String = getTxtForKey(PngChunkTextVar.KEY_Author)?.takeIf(String::isNotBlank)
                ?: return null
        val copyright: String? = getTxtForKey(PngChunkTextVar.KEY_Copyright)?.takeIf(String::isNotBlank)
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_author),
                Image(R.drawable.ic_person),
                Text(author),
                copyright?.let { Text(it) }
        )
    }

val PngMetadata.sourceAttribute: Metadata.Attribute?
    get() {
        val source: String? = getTxtForKey(PngChunkTextVar.KEY_Source)?.takeIf(String::isNotBlank)
        val software: String? = getTxtForKey(PngChunkTextVar.KEY_Software)?.takeIf(String::isNotBlank)
        if (source == null && software == null) return null
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_source),
                Image(R.drawable.ic_folder_special),
                Text(source ?: software!!),
                software?.takeIf { source != null }?.let { Text(it) }
        )
    }

val PngMetadata.commentAttribute: Metadata.Attribute?
    get() {
        val description: String? = getTxtForKey(PngChunkTextVar.KEY_Description)?.takeIf(String::isNotBlank)
        val comment: String? = getTxtForKey(PngChunkTextVar.KEY_Comment)?.takeIf(String::isNotBlank)
        if (description == null && comment == null) return null
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_comment),
                Image(R.drawable.ic_comment),
                Text(description ?: comment!!),
                comment?.takeIf { description != null }?.let { Text(it) }
        )
    }

val PngMetadata.warningAttribute: Metadata.Attribute?
    get() {
        val disclaimer: String? = getTxtForKey(PngChunkTextVar.KEY_Disclaimer)?.takeIf(String::isNotBlank)
        val warning: String? = getTxtForKey(PngChunkTextVar.KEY_Warning)?.takeIf(String::isNotBlank)
        if (disclaimer == null && warning == null) return null
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_warning),
                Image(R.drawable.ic_warning),
                Text(disclaimer ?: warning!!),
                warning?.takeIf { disclaimer != null }?.let { Text(it) }
        )
    }
