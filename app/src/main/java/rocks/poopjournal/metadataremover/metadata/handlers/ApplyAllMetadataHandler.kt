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
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.util.extensions.dequeOf
import java.io.File
import java.util.*

class ApplyAllMetadataHandler(
        private val handlers: Queue<MetadataHandler>
) : MetadataHandler {

    constructor(vararg handlers: MetadataHandler) : this(dequeOf(*handlers))

    override val readableMimeTypes = handlers
            .flatMapTo(mutableSetOf()) { handler ->
                handler.readableMimeTypes
            }
    override val writableMimeTypes = handlers
            .flatMapTo(mutableSetOf()) { handler ->
                handler.writableMimeTypes
            }

    private fun filterMetadataHandlers(mediaType: MediaType): Queue<MetadataHandler> {
        return handlers
                .filterTo(LinkedList()) { fileHandler ->
                    mediaType in fileHandler.readableMimeTypes
                }
    }

    override suspend fun loadMetadata(mediaType: MediaType, inputFile: File): Metadata? {
        return filterMetadataHandlers(mediaType)
                .map { it.loadMetadata(mediaType, inputFile) }
                .reduce(::mergeMetadata)
    }

    private fun mergeMetadata(acc: Metadata?, next: Metadata?): Metadata? {
        return when {
            acc == null -> next
            next == null -> acc
            else -> Metadata(
                    title = acc.title ?: next.title,
                    thumbnail = acc.thumbnail,
                    attributes = acc.attributes + next.attributes
            )
        }
    }

    override suspend fun removeMetadata(
            mediaType: MediaType,
            inputFile: File,
            outputFile: File): Boolean {
        return filterMetadataHandlers(mediaType)
                .any { it.removeMetadata(mediaType, inputFile, outputFile) }
    }
}