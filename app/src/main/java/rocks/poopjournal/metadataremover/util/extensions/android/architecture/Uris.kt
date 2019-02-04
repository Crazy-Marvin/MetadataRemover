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

package rocks.poopjournal.metadataremover.util.extensions.android.architecture

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.util.extensions.android.getString
import rocks.poopjournal.metadataremover.util.extensions.mediaType
import java.net.URLEncoder

fun Uri.getMediaType(context: Context): MediaType? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> context.contentResolver
                .getType(this)
                ?.let(MediaType.Companion::parse)
        ContentResolver.SCHEME_FILE -> toFile().mediaType
        else -> null
    }
}

fun Uri.getFileName(context: Context): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> context.contentResolver
                .query(
                        this,
                        arrayOf(OpenableColumns.DISPLAY_NAME),
                        null,
                        null,
                        null)
                ?.use { cursor ->
                    cursor.takeIf { it.moveToFirst() }
                            ?.getString(OpenableColumns.DISPLAY_NAME)
                            ?.let { URLEncoder.encode(it, Charsets.UTF_8.name()) }
                            ?.takeUnless { it == "." || it == ".." }
                }
        ContentResolver.SCHEME_FILE -> toFile().name
        else -> null
    }
}
