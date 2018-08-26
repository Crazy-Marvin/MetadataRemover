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

import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.util.extensions.MimeType
import java.io.File
import java.util.*

class CombiningMetadataHandler(private val handlers: Queue<MetadataHandler>) : MetadataHandler {

    override val readableMimeTypes = handlers
            .flatMap { handler ->
                handler.readableMimeTypes
            }
            .toSet()
    override val writableMimeTypes = handlers
            .flatMap { handler ->
                handler.writableMimeTypes
            }
            .toSet()

    override suspend fun loadMetadata(mimeType: MimeType, inputFile: File): Metadata? {
        return handlers
                .find { fileHandler ->
                    mimeType in fileHandler.readableMimeTypes
                }
                ?.loadMetadata(mimeType, inputFile)
    }

    override suspend fun removeMetadata(
            mimeType: MimeType,
            inputFile: File,
            outputFile: File): Boolean {
        return handlers
                .find { fileHandler ->
                    mimeType in fileHandler.writableMimeTypes
                }
                ?.removeMetadata(mimeType, inputFile, outputFile)
                ?: false
    }
}