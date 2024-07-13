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

class VideoMetadataHandler(private val context: Context): MetadataHandler {

    override val readableMimeTypes =    MediaTypes[MediaTypes.AVI_VIDEO]    +
                                        MediaTypes[MediaTypes.MP4_VIDEO]    +
                                        MediaTypes[ MediaTypes.MPEG_VIDEO]  +
                                        MediaTypes[ MediaTypes.OGG_VIDEO]   +
                                        MediaTypes[ MediaTypes.QUICKTIME]   +
                                        MediaTypes[ MediaTypes.WEBM_VIDEO]  +
                                        MediaTypes[ MediaTypes.WMV]

    override val writableMimeTypes =    MediaTypes[MediaTypes.AVI_VIDEO]    +
                                        MediaTypes[MediaTypes.MP4_VIDEO]    +
                                        MediaTypes[ MediaTypes.MPEG_VIDEO]  +
                                        MediaTypes[ MediaTypes.OGG_VIDEO]   +
                                        MediaTypes[ MediaTypes.QUICKTIME]   +
                                        MediaTypes[ MediaTypes.WEBM_VIDEO]  +
                                        MediaTypes[ MediaTypes.WMV]

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
    ): Boolean {
        check(mediaType in writableMimeTypes)

        val command = "-y -i ${inputFile.absolutePath} -map 0 -map_metadata -1 -c copy ${outputFile.absolutePath}"
        val session = FFmpegKit.execute(command)
        return ReturnCode.isSuccess(session.returnCode)
    }

    private fun mediaMetadataRetrieve(uri: Uri): List<Metadata.Attribute>{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        val metadataList = mutableListOf<Metadata.Attribute>()

        fun addMetadata(key: Int, label: String, iconRes: Int) {
            val value = retriever.extractMetadata(key)
            if (value != null) {
                metadataList.add(Metadata.Attribute(
                    label = Text(label),
                    icon = Image(iconRes),
                    primaryValue = Text(value)
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
            primaryValue = durationValue
        ))

        // Add other metadata
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM, "Album", R.drawable.ic_album)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "Album Artist", R.drawable.ic_artist)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST, "Artist", R.drawable.ic_artist)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR, "Author", R.drawable.ic_author)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE, "Bitrate", R.drawable.ic_bitrate)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE, "Bits Per Sample", R.drawable.ic_audio)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE, "Capture Framerate", R.drawable.ic_video)
        }

        addMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER, "CD Track Number", R.drawable.ic_tracks)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE, "Color Range", R.drawable.ic_color)
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_STANDARD, "Color Standard", R.drawable.ic_color)
            addMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_TRANSFER, "Color Transfer", R.drawable.ic_color)
        }

        addMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION, "Compilation", R.drawable.ic_compilation)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER, "Composer", R.drawable.ic_composer)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_DATE, "Date", R.drawable.ic_date)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER, "Disc Number", R.drawable.ic_album)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE, "Genre", R.drawable.ic_genre)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE, "MIME Type", R.drawable.ic_file)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS, "Number of Tracks", R.drawable.ic_tracks)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE, "Title", R.drawable.ic_title)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR, "Year", R.drawable.ic_year)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER, "Writer", R.drawable.ic_writer)

        // Video-specific metadata
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH, "Video Width", R.drawable.ic_video)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT, "Video Height", R.drawable.ic_video)
        addMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION, "Video Rotation", R.drawable.ic_video)

        // Image-specific metadata
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_WIDTH, "Image Width", R.drawable.ic_image)
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT, "Image Height", R.drawable.ic_image)
            addMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_ROTATION, "Image Rotation", R.drawable.ic_image)
        }

        // Boolean metadata
        fun addBooleanMetadata(key: Int, label: String, iconRes: Int) {
            val value = retriever.extractMetadata(key)
            if (value != null) {
                metadataList.add(Metadata.Attribute(
                    label = Text(label),
                    icon = Image(iconRes),
                    primaryValue = Text(if (value == "yes") "Yes" else "No")
                ))
            }
        }

        addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO, "Has Audio", R.drawable.ic_audio)
        addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO, "Has Video", R.drawable.ic_video)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addBooleanMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE, "Has Image", R.drawable.ic_image)
        }

        retriever.release()
        return metadataList
    }
}