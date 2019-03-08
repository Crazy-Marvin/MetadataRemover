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

import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.PngWriter
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour
import ar.com.hjg.pngj.chunks.PngChunkTextVar
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.ImageFile
import rocks.poopjournal.metadataremover.util.extensions.pngj.*
import java.io.File

object PngMetadataHandler : MetadataHandler {
    override val readableMimeTypes = MediaTypes[MediaTypes.PNG]
    override val writableMimeTypes = MediaTypes[MediaTypes.PNG]

    override suspend fun loadMetadata(mediaType: MediaType, inputFile: File): Metadata? {
        check(mediaType in readableMimeTypes)

        val metadata = PngReader(inputFile)
                .metadata
                .apply {

                    // Read metadata
                }

        val imageFile = ImageFile(inputFile)

        val attributes = listOfNotNull(
                metadata.creationAttribute,
                metadata.lastModifiedAttribute,
                imageFile.resolutionAttribute,
                metadata.authorAttribute,
                metadata.sourceAttribute,
                metadata.commentAttribute,
                metadata.warningAttribute
        )

        return Metadata(
                title = metadata.getTxtForKey(PngChunkTextVar.KEY_Title)?.let { Text(it) },
                thumbnail = Image(inputFile),
                attributes = attributes.toSet()
        )
    }


    override suspend fun removeMetadata(
            mediaType: MediaType,
            inputFile: File,
            outputFile: File): Boolean {
        check(mediaType in writableMimeTypes)

        usePngTransforming(inputFile, outputFile, true) { reader, writer ->

            System.out.println(reader.toString())

            writer.copyChunksFrom(reader.chunksList, ChunkCopyBehaviour.COPY_ALL_SAFE)

            writer.metadata.apply {
                // Reset date
                setTimeYMDHMS(0, 0, 0, 0, 0, 0)
                // Reset metadata
                setText(PngChunkTextVar.KEY_Title, "")
                setText(PngChunkTextVar.KEY_Author, "")
                setText(PngChunkTextVar.KEY_Description, "")
                setText(PngChunkTextVar.KEY_Copyright, "")
                setText(PngChunkTextVar.KEY_Creation_Time, "")
                setText(PngChunkTextVar.KEY_Software, "")
                setText(PngChunkTextVar.KEY_Disclaimer, "")
                setText(PngChunkTextVar.KEY_Warning, "")
                setText(PngChunkTextVar.KEY_Source, "")
                setText(PngChunkTextVar.KEY_Comment, "")
            }

            reader.copyRowsTo(writer)
        }

        return true
    }

    private fun <R> usePngTransforming(
            inputFile: File,
            outputFile: File,
            allowOverwrite: Boolean = true,
            block: (PngReader, PngWriter) -> R
    ): R {
        val reader = PngReader(inputFile)
        val writer = PngWriter(outputFile, reader.imgInfo, allowOverwrite)

        var exception: Throwable? = null
        try {
            return block(reader, writer)
        } catch (throwable: Throwable) {
            exception = throwable
            throw throwable
        } finally {
            when (exception) {
                null -> {
                    reader.end()
                    writer.end()
                }
                else -> {
                    try {
                        reader.end()
                        writer.end()
                    } catch (closeException: Throwable) {
                        exception.addSuppressed(closeException)
                    }
                }
            }
        }
    }
}