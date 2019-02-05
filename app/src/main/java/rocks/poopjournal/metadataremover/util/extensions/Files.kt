/*
 * MIT License
 *
 * Copyright (c) 2017 Jan Heinrich Reimer
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

package rocks.poopjournal.metadataremover.util.extensions

import android.net.Uri
import android.webkit.MimeTypeMap
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.Text
import java.io.File
import java.util.Date

/**
 * Delete this file if it exists.
 *
 * @return  `true` if and only if the file or directory exists and is
 *          successfully deleted, `false` otherwise.
 */
fun File.deleteIfExists() = exists() && delete()

val File.mediaType: MediaType?
    get() {
        return extension
                .let(MimeTypeMap.getSingleton()::getMimeTypeFromExtension)
                ?.let(MediaType.Companion::parse)
    }

val File.size: Long
    get() = length()


val File.nameAttribute: Metadata.Attribute
    get() = Metadata.Attribute(
            label = Text(R.string.label_attribute_file_name),
            icon = Image(R.drawable.ic_title),
            primaryValue = Text(name)
    )

val File.lastModified: Long?
    get() = lastModified().takeUnless { it == 0L }
val File.lastModifiedDate: Date?
    get() = lastModified?.let { Date(it) }
val File.lastModifiedAttribute: Metadata.Attribute?
    get() {
        val lastModifiedDate = lastModifiedDate ?: return null
        return Metadata.Attribute(
                label = Text(R.string.label_attribute_file_last_modification_date_time),
                icon = Image(R.drawable.ic_history),
                primaryValue = Text(attributeDateFormat.format(lastModifiedDate)),
                secondaryValue = Text(attributeTimeFormat.format(lastModifiedDate))
        )
    }

val File.sizeLabel: String
    get() = size.formatScaled(
            digits = 2,
            unit = "B",
            scaleFactor = 1024,
            upscalePrefixes = arrayOf("K", "M", "G", "T", "G"),
            downscalePrefixes = emptyArray()
    )
val File.sizeAttribute: Metadata.Attribute
    get() = Metadata.Attribute(
            label = Text(R.string.label_attribute_file_size),
            icon = Image(R.drawable.ic_storage),
            primaryValue = Text(sizeLabel),
            secondaryValue = Text(R.string.description_attribute_file_size_bytes, size)
    )
