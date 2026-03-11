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
import android.location.Geocoder
import androidx.exifinterface.media.ExifInterface
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import rocks.poopjournal.metadataremover.util.ImageFile
import rocks.poopjournal.metadataremover.util.extensions.android.*
import rocks.poopjournal.metadataremover.util.extensions.deleteIfExists
import java.io.File
import java.util.*

class ExifMetadataHandler(context: Context? = null) : MetadataHandler {

    private val geocoder = context?.let {
        Geocoder(context, context.defaultLocale ?: Locale.getDefault())
    }

    override val writableMimeTypes = MediaTypes[MediaTypes.JPEG]

    override val readableMimeTypes =
        MediaTypes[MediaTypes.JPEG] +
                MediaTypes[MediaTypes.DNG] + MediaTypes[MediaTypes.CR2] +
                MediaTypes[MediaTypes.NEF] + MediaTypes[MediaTypes.NRW] +
                MediaTypes[MediaTypes.ARW] + MediaTypes[MediaTypes.RW2] +
                MediaTypes[MediaTypes.ORF] + MediaTypes[MediaTypes.PEF] +
                MediaTypes[MediaTypes.SRW] + MediaTypes[MediaTypes.RAF]

    override suspend fun loadMetadata(
        mediaType: MediaType,
        inputFile: File
    ): Metadata? {

        check(mediaType in readableMimeTypes)

        val exif = ExifInterface(inputFile.inputStream())

        val thumbnail =
            exif.thumbnailBitmap
                ?.takeIf { it.width * it.height > 1_000_000 }
                ?.let { Image(it) }
                ?: Image(inputFile)

        val imageFile = ImageFile(inputFile)

        val attributes = mutableListOf<Metadata.Attribute>()

        exif.creationAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_DATETIME
                )
            )
        }



        imageFile.resolutionAttribute?.let {
            attributes.add(
                it.copy(removable = true, tag = ExifInterface.TAG_LENS_MODEL)
            )
        }

        exif.exposureAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_EXPOSURE_TIME
                )
            )
        }


        exif.getLocationAttribute(geocoder)?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_GPS_LATITUDE
                )
            )
        }

        exif.cameraAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_MODEL
                )
            )
        }

        exif.lensAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_LENS_MODEL
                )
            )
        }

        exif.lightAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_FLASH
                )
            )
        }

        exif.ownerAttribute?.let {
            attributes.add(
                it.copy(
                    removable = true,
                    tag = ExifInterface.TAG_ARTIST
                )
            )
        }

        return Metadata(
            thumbnail = thumbnail,
            attributes = attributes.toSet()
        )
    }

    override suspend fun removeMetadata(
        mediaType: MediaType,
        inputFile: File,
        outputFile: File,
        attributes: List<Metadata.Attribute>
    ): Boolean {

        check(mediaType in writableMimeTypes)

        // Delete previous output
        outputFile.deleteIfExists()

        // Copy original to output
        inputFile.copyTo(outputFile)

        val exif = ExifInterface(outputFile.path)

        // Collect selected removable tags
        val tagsToRemove = attributes
            .filter { it.removable && it.selected }
            .mapNotNull { it.tag }

        if (tagsToRemove.isEmpty()) {
            // If nothing selected → remove everything
            exif.clearAllAttributes()
        } else {

            tagsToRemove.forEach { tag ->
                exif.setAttribute(tag, null)
            }
        }

        exif.saveAttributes()

        return true
    }
}