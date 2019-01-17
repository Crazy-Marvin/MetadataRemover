/*
 * MIT License
 *
 * Copyright (c) 2018 Jan Heinrich Reimer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rocks.poopjournal.metadataremover.draft

/*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.media.ExifInterface
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.common.IImageMetadata.IImageMetadataItem
import org.apache.commons.imaging.common.RationalNumber
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort
import org.apache.tika.parser.AutoDetectParser
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.ExifMetadataHandler.ImageMetadata.Location
import rocks.poopjournal.metadataremover.model.ExifMetadataHandler.ImageMetadata.Location.Length
import rocks.poopjournal.metadataremover.model.metadata.Metadata.Attribute
import rocks.poopjournal.metadataremover.model.util.Image
import rocks.poopjournal.metadataremover.model.util.MimeType
import rocks.poopjournal.metadataremover.model.util.Text
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.buffer
import rocks.poopjournal.metadataremover.util.inputStream
import rocks.poopjournal.metadataremover.util.outputStream
import rocks.poopjournal.metadataremover.util.sink
import rocks.poopjournal.metadataremover.util.source
import rocks.poopjournal.metadataremover.util.toBitmap
import java.io.File

class ExifMetadataHandler : MetadataHandler {

    companion object {

        @JvmField val APACHE_IMAGING_WRITEABLE_METADATA_MIME_TYPES = JPEG_MIME_TYPES + TIFF_MIME_TYPES
        @JvmField val BITMAP_WRITEABLE_METADATA_MIME_TYPES = PNG_MIME_TYPES + WEBP_MIME_TYPES

        @JvmField val APACHE_IMAGING_READABLE_METADATA_MIME_TYPES = BMP_MIME_TYPES +
                DCX_MIME_TYPES + GIF_MIME_TYPES + ICO_MIME_TYPES + JPEG_MIME_TYPES +
                PCX_MIME_TYPES + PNG_MIME_TYPES + PNM_MIME_TYPES + PSD_MIME_TYPES +
                TIFF_MIME_TYPES + WBMP_MIME_TYPES + XBM_MIME_TYPES + XPM_MIME_TYPES


    }

    override val writableMimeTypes = APACHE_IMAGING_WRITEABLE_METADATA_MIME_TYPES +
            BITMAP_WRITEABLE_METADATA_MIME_TYPES
    override val readableMimeTypes = APACHE_IMAGING_READABLE_METADATA_MIME_TYPES

    fun List<IImageMetadataItem>.flatten(): Collection<Pair<String, String>> {
        return mapNotNull { item ->
            when (item) {
                is ImageMetadata.Item -> {
                    item.keyword to item.text
                }
                else -> null
            }
        }
    }

    override suspend fun loadMetadata(mimeType: MimeType, inputFile: File): Metadata {
        val thumbnail = Image(inputFile)

        val resolutionUnit: String?
        val imageResolutionMegaPixels: RationalNumber?
        val imageHeight: Int
        val imageWidth: Int


        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(inputFile.path, options)
        val x = options.outWidth
        val y = options.outHeight
        val pixels = RationalNumber(x * y, 1000000)


        val metadata = ImageMetadataReader.readMetadata(inputFile)
        val exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        val date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
        val date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
        val date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)





        val parser = AutoDetectParser()
        val metadata2 = org.apache.tika.metadata.Metadata()
        parser.parse()





        ExifInterface(inputFile.path).


        val imageMetadata: TiffImageMetadata? = Imaging.getmetadata(inputFile)
                .let { metadata ->
                    when (metadata) {
                        is JpegImageMetadata -> metadata.exif
                        is TiffImageMetadata -> metadata
                        else -> null
                    }
                }
                ?.let {  }



        val locationLatitude = imageMetadata[GpsTagConstants.GPS_TAG_GPS_LATITUDE]
                ?.takeIf { it.size >= 3 }
        val locationLatitudeDirection = imageMetadata[GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF]
                ?.firstOrNull()
                ?.firstOrNull()
        val locationLongitude = imageMetadata[GpsTagConstants.GPS_TAG_GPS_LONGITUDE]
                ?.takeIf { it.size >= 3 }
        val locationLongitudeDirection = imageMetadata[GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF]
                ?.firstOrNull()
                ?.firstOrNull()
        val location: Location? =
                if (locationLatitude != null &&
                        locationLatitudeDirection != null &&
                        locationLongitude != null &&
                        locationLongitudeDirection != null) {
                    Location(
                            latitude = Length(
                                    degrees = locationLatitude[0],
                                    minutes = locationLatitude[1],
                                    seconds = locationLatitude[2],
                                    direction = locationLatitudeDirection),
                            longitude = Length(
                                    degrees = locationLongitude[0],
                                    minutes = locationLongitude[1],
                                    seconds = locationLongitude[2],
                                    direction = locationLongitudeDirection)
                    )
                } else null


                            val items = imageMetadata.items;
                     for (int i = 0; i < items.size(); i++) {
                             final ImageMetadataItem item = items.get(i);
                             System.out.println("    " + "item: " + item);
                         }



        // TODO remove after debugging
        imageMetadata.items
                .mapNotNull { item ->
                    when (item) {
                        is ImageMetadata.Item -> {
                            item.keyword to item.text
                        }
                        else -> null
                    }
                }
                .forEach { (keyword, text) ->
                    Logger.d("Metadata Entry '$keyword': '$text'")
                }


        val sizeAttribute = Attribute(
                Text(R.string.label_attribute_image_size),
                Image(R.drawable.ic_landscape),
                Text("${imageResolutionMegaPixels.toDisplayString()}MP"))
        val dateTimeAttribute = Attribute(
                Text(R.string.label_attribute_image_date_time),
                Image(R.drawable.ic_event))
        val cameraAttribute = Attribute(
                Text(R.string.label_attribute_image_camera),
                Image(R.drawable.ic_camera))
        val locationAttribute = Attribute(
                Text(R.string.label_attribute_image_location),
                Image(R.drawable.ic_location))






        TODO("Fill attributes.")

        return Metadata(
                thumbnail = thumbnail,
                attributes = attributes
        )
    }

    override suspend fun removeMetadata(mimeType: MimeType, inputFile: File,
            outputFile: File): Boolean {
        if (mimeType !in writableMimeTypes) {
            return false
        }

        // Create a buffer to read the image from.
        val imageBuffer = inputFile.source.buffer()
        // Create a buffer to save the image to.
        val outputBuffer = outputFile.sink.buffer()

        when (mimeType) {
            in APACHE_IMAGING_WRITEABLE_METADATA_MIME_TYPES -> {
                // If Apache Imaging supports write access for metadata of this MIME type,
                // then just clear all attributes and copy the result to the output file.
                // That way we can remove attributes without losing image quality
                // for lossy formats like JPEG.

                // Remove the image metadata and copy the results to the output file.
                imageBuffer
                        .pipe { source, sink ->
                            // Remove the EXIF metadata.
                            ExifRewriter().removeExifMetadata(source.inputStream, sink.outputStream)
                        }
                        .pipe(outputBuffer) { source, sink ->
                            // Remove the XMP metadata.
                            JpegXmpRewriter().removeXmpXml(source.inputStream, sink.outputStream)
                        }
                return true
            }
            in BITMAP_WRITEABLE_METADATA_MIME_TYPES -> {
                // Otherwise try to read a Bitmap of the input file (which omits meta attributes)
                // and directly save that to the output file.

                // Copy the image (without metadata).
                imageBuffer.use { source ->
                    val inputBitmap = source.inputStream.toBitmap()
                    val compressFormat = when (mimeType) {
                        in JPEG_MIME_TYPES -> Bitmap.CompressFormat.JPEG
                        in PNG_MIME_TYPES -> Bitmap.CompressFormat.PNG
                        in WEBP_MIME_TYPES -> Bitmap.CompressFormat.WEBP
                        else -> null
                    }
                    return if (compressFormat == null) {
                        false
                    } else {
                        inputBitmap.compress(compressFormat, 100, outputBuffer.outputStream)
                        true
                    }
                }
            }
            else -> return false
        }
    }

    class ExifMetadata(private val mimeType: MimeType, private val inputFile: File) : Metadata {

        private val exifInterface = ExifInterface(inputFile.absolutePath)

        override val attributes: Set<MetaAttribute>
            get() = TODO("not implemented")

        override suspend fun rocks.poopjournal.metadataremover.draft.clearAttributes(outputFile: File) {
        }

        override val thumbnail = Image(inputFile)
    }
}


operator fun TiffImageMetadata.get(tagInfo: TagInfoRational): Array<out RationalNumber>? {
    val exifValue = findField(tagInfo, true)
    return tagInfo.getValue(exifValue.byteOrder, exifValue.byteArrayValue)
}
operator fun TiffImageMetadata.get(tagInfo: TagInfoAscii): Array<out String>? {
    val exifValue = findField(tagInfo, true)
    return tagInfo.getValue(exifValue.byteOrder, exifValue.byteArrayValue)
}
operator fun TiffImageMetadata.get(tagInfo: TagInfoShort): ShortArray? {
    val exifValue = findField(tagInfo, true)
    return tagInfo.getValue(exifValue.byteOrder, exifValue.byteArrayValue)
}


fun ExifInterface.rocks.poopjournal.metadataremover.draft.clearAttribute(tag: String) {
    setAttribute(tag, null)
}

fun ExifInterface.rocks.poopjournal.metadataremover.draft.clearAttributes(tags: Collection<String>) {
    tags.forEach { rocks.poopjournal.metadataremover.draft.clearAttribute(it) }
}

fun BufferedSource.pipe(
        to: BufferedSink = Buffer(),
        transform: (
                source: BufferedSource,
                sink: BufferedSink) -> Unit): Buffer {
    val out: Buffer = to.buffer()
    use { source ->
        transform(source, out)
    }
    return out
}

// */
