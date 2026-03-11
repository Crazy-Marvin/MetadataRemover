package rocks.poopjournal.metadataremover.metadata.handlers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import rocks.poopjournal.metadataremover.model.resources.Text
import java.io.File

class AudioVideoMetadataHandler(private val context: Context): MetadataHandler {

    override val readableMimeTypes =    MediaTypes[MediaTypes.AVI_VIDEO]    +
                                        MediaTypes[MediaTypes.MP4_VIDEO]    +
                                        MediaTypes[ MediaTypes.MPEG_VIDEO]  +
                                        MediaTypes[ MediaTypes.OGG_VIDEO]   +
                                        MediaTypes[ MediaTypes.QUICKTIME]   +
                                        MediaTypes[ MediaTypes.WEBM_VIDEO]  +
                                        MediaTypes[ MediaTypes.WMV]         +
                                        MediaTypes[ MediaTypes.MP4_AUDIO]   +
                                        MediaTypes[ MediaTypes.MPEG_AUDIO]  +
                                        MediaTypes[ MediaTypes.OGG_AUDIO]   +
                                        MediaTypes[ MediaTypes.WEBM_AUDIO]  +
                                        MediaTypes[ MediaTypes.WAV_AUDIO]

    override val writableMimeTypes =    MediaTypes[MediaTypes.AVI_VIDEO]    +
                                        MediaTypes[MediaTypes.MP4_VIDEO]    +
                                        MediaTypes[ MediaTypes.MPEG_VIDEO]  +
                                        MediaTypes[ MediaTypes.OGG_VIDEO]   +
                                        MediaTypes[ MediaTypes.QUICKTIME]   +
                                        MediaTypes[ MediaTypes.WEBM_VIDEO]  +
                                        MediaTypes[ MediaTypes.WMV]         +
                                        MediaTypes[ MediaTypes.MP4_AUDIO]   +
                                        MediaTypes[ MediaTypes.MPEG_AUDIO]  +
                                        MediaTypes[ MediaTypes.OGG_AUDIO]   +
                                        MediaTypes[ MediaTypes.WEBM_AUDIO]  +
                                        MediaTypes[ MediaTypes.WAV_AUDIO]

    override suspend fun loadMetadata(mediaType: MediaType, inputFile: File): Metadata? {
        check(mediaType in readableMimeTypes)

        return Metadata(
            thumbnail = Image(inputFile),
            attributes = mediaMetadataRetrieve(inputFile.toUri()).toSet()
        )
    }


    override suspend fun removeMetadata(
        mediaType: MediaType,
        inputFile: File,
        outputFile: File,
        attributes: List<Metadata.Attribute>
    ): Boolean {
        check(mediaType in writableMimeTypes)

        val selectedKeys = attributes
            .filter { it.removable && it.selected }
            .mapNotNull { it.tag }

        // If no attributes selected, remove all
        val removeAll = selectedKeys.isEmpty()

        // Build FFmpeg metadata removal commands
        val metadataCommands = if (removeAll) {
            "-map_metadata -1"
        } else {
            selectedKeys.joinToString(" ") { key -> "-metadata $key=" }
        }

        val command = "-y -i ${inputFile.absolutePath} -map 0 $metadataCommands -c copy ${outputFile.absolutePath}"
        val session = FFmpegKit.execute(command)
        return ReturnCode.isSuccess(session.returnCode)
    }

    private fun mediaMetadataRetrieve(uri: Uri): List<Metadata.Attribute>{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        val metadataList = mutableListOf<Metadata.Attribute>()

        fun addMetadata(key: Int, label: String, iconRes: Int,removable : Boolean , tag : String) {
            val value = retriever.extractMetadata(key)
            if (value != null) {
                metadataList.add(Metadata.Attribute(
                    label = Text(label),
                    icon = Image(iconRes),
                    primaryValue = Text(value),
                    removable = removable,
                    tag = tag
                ))
            }
        }

        // Duration (special case for formatting)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        val durationSec = durationMs?.div(1000)
        val durationValue = Text(durationSec?.let { "$it seconds" } ?: "Duration not available")
        metadataList.add(Metadata.Attribute(
            label = Text("Duration"),
            icon = Image(R.drawable.ic_duration),
            primaryValue = durationValue,
            removable = true,
            tag = "duration"
        ))

        // Add other metadata
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM, "Album", R.drawable.ic_album,removable = true, tag = "album")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "Album Artist", R.drawable.ic_artist,removable = true, tag = "album_artist")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST, "Artist", R.drawable.ic_artist,removable = true, tag = "artist")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR, "Author", R.drawable.ic_author,removable = true, tag = "author")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE, "Bitrate", R.drawable.ic_bitrate,removable = true, tag = "bitrate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE, "Bits Per Sample", R.drawable.ic_audio,removable = true, tag = "bits_per_sample")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE, "Capture Framerate", R.drawable.ic_video,removable = true, tag = "capture_framerate")
        }

        addMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER, "CD Track Number", R.drawable.ic_tracks,removable = true, tag = "cd_track_number")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE, "Color Range", R.drawable.ic_color,removable = true, tag = "color_range")
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_STANDARD, "Color Standard", R.drawable.ic_color, removable = true, tag = "color_standard")
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_TRANSFER, "Color Transfer", R.drawable.ic_color,removable = true, tag = "color_transfer")
        }

        addMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION, "Compilation", R.drawable.ic_compilation,removable = true, tag = "compilation")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER, "Composer", R.drawable.ic_composer,removable = true, tag = "composer")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_DATE, "Date", R.drawable.ic_date,removable = true, tag = "date")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER, "Disc Number", R.drawable.ic_album,removable = true, tag = "disc_number")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE, "Genre", R.drawable.ic_genre,removable = true, tag = "genre")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE, "MIME Type", R.drawable.ic_file,removable = true, tag = "mime_type")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS, "Number of Tracks", R.drawable.ic_tracks,removable = true, tag = "num_tracks")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE, "Title", R.drawable.ic_title,removable = true, tag = "title")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR, "Year", R.drawable.ic_year,removable = true, tag = "year")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER, "Writer", R.drawable.ic_writer,removable = true, tag = "writer")

        // Video-specific metadata
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH, "Video Width", R.drawable.ic_video,removable = true, tag = "video_width")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT, "Video Height", R.drawable.ic_video,removable = true, tag = "video_height")
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION, "Video Rotation", R.drawable.ic_video,removable = true, tag = "video_rotation")

        // Image-specific metadata
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_WIDTH, "Image Width", R.drawable.ic_image,removable = true, tag = "image_width")
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT, "Image Height", R.drawable.ic_image,removable = true, tag = "image_height")
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_ROTATION, "Image Rotation", R.drawable.ic_image,removable = true, tag = "image_rotation")
        }

        // Boolean metadata
        fun addBooleanMetadata(key: Int, label: String, iconRes: Int,removable : Boolean , tag : String) {
            val value = retriever.extractMetadata(key)
            if (value != null) {
                metadataList.add(Metadata.Attribute(
                    label = Text(label),
                    icon = Image(iconRes),
                    primaryValue = Text(if (value == "yes") "Yes" else "No"),
                    removable = removable,
                    tag = tag
                ))
            }
        }

        addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO, "Has Audio", R.drawable.ic_audio,removable = true, tag = "has_audio")
        addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO, "Has Video", R.drawable.ic_video,removable = true, tag = "has_video")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE, "Has Image", R.drawable.ic_image,removable = true, tag = "has_image")
        }

        retriever.release()
        return metadataList
    }
}