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

package rocks.poopjournal.metadataremover.metadata.handlers

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Geocoder
import androidx.exifinterface.media.ExifInterface
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.Metadata.Attribute
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.MimeTypes
import rocks.poopjournal.metadataremover.util.extensions.MimeType
import rocks.poopjournal.metadataremover.util.extensions.android.cameraAttribute
import rocks.poopjournal.metadataremover.util.extensions.android.clearAllAttributes
import rocks.poopjournal.metadataremover.util.extensions.android.getLocationAttribute
import rocks.poopjournal.metadataremover.util.extensions.format
import rocks.poopjournal.metadataremover.util.extensions.lastModifiedAttribute
import rocks.poopjournal.metadataremover.util.extensions.sizeAttribute
import java.io.File
import java.util.*

class ExifMetadataHandler(context: Context) : MetadataHandler {

    private val geocoder = Geocoder(context, Locale.getDefault())

    override val writableMimeTypes = MimeTypes.JPEG
    override val readableMimeTypes = MimeTypes.JPEG + MimeTypes.DNG + MimeTypes.CR2 +
            MimeTypes.NEF + MimeTypes.NRW + MimeTypes.ARW +
            MimeTypes.RW2 + MimeTypes.ORF + MimeTypes.PEF +
            MimeTypes.SRW + MimeTypes.RAF

    override suspend fun loadMetadata(mimeType: MimeType, inputFile: File): Metadata {
        val exif = ExifInterface(inputFile.inputStream())
        val thumbnail = exif.thumbnailBitmap
                ?.let { Image(it) }
                ?: Image(inputFile)

        val imageFile = ImageResolution(inputFile)

        val attributes = setOf(
                inputFile.lastModifiedAttribute,
                inputFile.sizeAttribute,
                imageFile.attributes.resolution,
                exif.getLocationAttribute(geocoder),
                exif.cameraAttribute
        ).filterNotNull()

        return Metadata(
                thumbnail = thumbnail,
                attributes = attributes.toSet()
        )
    }

    override suspend fun removeMetadata(
            mimeType: MimeType,
            inputFile: File,
            outputFile: File): Boolean {
        if (mimeType !in writableMimeTypes) {
            return false
        }

        // Copy input file to output directory,
        // then edit that output file.
        inputFile.copyTo(outputFile)

        // Clear all EXIF tags.
        ExifInterface(outputFile.path)
                .clearAllAttributes()
                .saveAttributes()

        return true
    }

    class ImageResolution(val file: File) : File(file.path) {
        private val bitmapOptions =
                BitmapFactory.Options()
                        .apply { inJustDecodeBounds = true }
                        .also { BitmapFactory.decodeFile(path, it) }
        val x: Long = bitmapOptions.outWidth.toLong()
        val y: Long = bitmapOptions.outHeight.toLong()
        val pixels: Long = x * y

        val attributes = Attributes()

        inner class Attributes {
            private val label: Text = pixels.let { pixels ->
                if (pixels > 1_000_000) Text(R.string.description_attribute_image_resolution_mega_pixels, (pixels / 1_000_000).format(2))
                else Text(R.string.description_attribute_image_resolution_pixels, pixels)
            }
            private val xyLabel: Text = Text(R.string.description_attribute_image_resolution_xy, x, y)

            val resolution = Attribute(
                    Text(R.string.label_attribute_file_size),
                    Image(R.drawable.ic_landscape),
                    label,
                    xyLabel
            )
        }
    }
}