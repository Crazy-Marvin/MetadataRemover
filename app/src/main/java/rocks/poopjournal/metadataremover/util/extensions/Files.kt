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

import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import java.io.File
import java.text.DateFormat
import java.util.*

/**
 * Get the [Uri] corresponding to a file.
 */
fun File.toUri(): Uri = Uri.fromFile(this)

val File.mimeType: MimeType?
    get() {
        return path
                .let(MimeTypeMap::getFileExtensionFromUrl)
                .let(MimeTypeMap.getSingleton()::getMimeTypeFromExtension)
                ?.takeIf(String::isNotBlank)
    }

val File.size: Long
    get() = length()

val File.lastModified: Long
    get() = lastModified()


val File.sizeAttribute: Metadata.Attribute
    get() = Metadata.Attribute(
            label = Text(R.string.label_attribute_file_size),
            icon = Image(R.drawable.ic_landscape),
            primaryValue = Text(name),
            secondaryValue = Text("$sizeLabel ($size Bytes)")
    )

private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG)
private val timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
val File.lastModifiedAttribute: Metadata.Attribute
    get() = Metadata.Attribute(
            label = Text(R.string.label_attribute_file_last_modification_date_time),
            icon = Image(R.drawable.ic_event),
            primaryValue = Text(dateFormat.format(lastModifiedDate)),
            secondaryValue = Text(timeFormat.format(lastModifiedDate))
    )

val File.lastModifiedDate: Date
    get() = Date(lastModified)

val File.sizeLabel: String
    get() = size.formatScaled(
            digits = 2,
            unit = "B",
            scaleFactor = 1024,
            upscalePrefixes = arrayOf("K", "M", "G", "T", "G"),
            downscalePrefixes = emptyArray()
    )

val File.inputStream
    get() = inputStream()

val File.outputStream
    get() = outputStream()

typealias MimeType = String

fun MimeType.normalize(): MimeType = Intent.normalizeMimeType(this)!!