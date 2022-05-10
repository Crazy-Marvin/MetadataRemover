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

import com.drew.metadata.Directory
import com.drew.metadata.adobe.AdobeJpegDirectory
import com.drew.metadata.avi.AviDirectory
import com.drew.metadata.bmp.BmpHeaderDirectory
import com.drew.metadata.eps.EpsDirectory
import com.drew.metadata.exif.*
import com.drew.metadata.file.FileSystemDirectory
import com.drew.metadata.file.FileTypeDirectory
import com.drew.metadata.gif.*
import com.drew.metadata.icc.IccDirectory
import com.drew.metadata.ico.IcoDirectory
import com.drew.metadata.iptc.IptcDirectory
import com.drew.metadata.jfif.JfifDirectory
import com.drew.metadata.jfxx.JfxxDirectory
import com.drew.metadata.jpeg.HuffmanTablesDirectory
import com.drew.metadata.jpeg.JpegCommentDirectory
import com.drew.metadata.jpeg.JpegDirectory
import com.drew.metadata.mov.QuickTimeDirectory
import com.drew.metadata.mp4.Mp4Directory
import com.drew.metadata.pcx.PcxDirectory
import com.drew.metadata.photoshop.PhotoshopDirectory
import com.drew.metadata.photoshop.PsdHeaderDirectory
import com.drew.metadata.png.PngDirectory
import com.drew.metadata.wav.WavDirectory
import com.drew.metadata.webp.WebpDirectory
import com.drew.metadata.xmp.XmpDirectory
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataReader
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import java.io.File
import com.drew.imaging.ImageMetadataReader as DrewImageMetadataReader
import com.drew.metadata.Metadata as DrewMetadata

object DrewMetadataReader : MetadataReader {

    override val readableMimeTypes = MediaTypes[MediaTypes.JPEG] +
            MediaTypes[MediaTypes.AVI_VIDEO] + MediaTypes[MediaTypes.BMP] +
            MediaTypes[MediaTypes.POSTSCRIPT] + MediaTypes[MediaTypes.JPEG] +
            MediaTypes[MediaTypes.DNG] + MediaTypes[MediaTypes.CR2] +
            MediaTypes[MediaTypes.NEF] + MediaTypes[MediaTypes.NRW] +
            MediaTypes[MediaTypes.ARW] + MediaTypes[MediaTypes.RW2] +
            MediaTypes[MediaTypes.ORF] + MediaTypes[MediaTypes.PEF] +
            MediaTypes[MediaTypes.SRW] + MediaTypes[MediaTypes.RAF] +
            MediaTypes[MediaTypes.GIF] + MediaTypes[MediaTypes.ICC] +
            MediaTypes[MediaTypes.ICO] + MediaTypes[MediaTypes.QUICKTIME] +
            MediaTypes[MediaTypes.MP4_VIDEO] + MediaTypes[MediaTypes.PCX] +
            MediaTypes[MediaTypes.PSD] + MediaTypes[MediaTypes.PNG] +
            MediaTypes[MediaTypes.WAV_AUDIO] + MediaTypes[MediaTypes.WEBP]

    override suspend fun loadMetadata(mediaType: MediaType, inputFile: File): Metadata? {
        check(mediaType in readableMimeTypes)

        val metadata = DrewImageMetadataReader.readMetadata(inputFile)

        val adobeJpegDirectory = metadata.getFirstDirectoryOfType<AdobeJpegDirectory>()
        adobeJpegDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val aviDirectory = metadata.getFirstDirectoryOfType<AviDirectory>()
        aviDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val bmpHeaderDirectory = metadata.getFirstDirectoryOfType<BmpHeaderDirectory>()
        bmpHeaderDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val epsDirectory = metadata.getFirstDirectoryOfType<EpsDirectory>()
        epsDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val exifIFD0Directory = metadata.getFirstDirectoryOfType<ExifIFD0Directory>()
        exifIFD0Directory?.let { directory ->
            TODO("Read directory $directory")
        }
        val exifImageDirectory = metadata.getFirstDirectoryOfType<ExifImageDirectory>()
        exifImageDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val exifInteropDirectory = metadata.getFirstDirectoryOfType<ExifInteropDirectory>()
        exifInteropDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val exifSubIFDDirectory = metadata.getFirstDirectoryOfType<ExifSubIFDDirectory>()
        exifSubIFDDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val exifThumbnailDirectory = metadata.getFirstDirectoryOfType<ExifThumbnailDirectory>()
        exifThumbnailDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gpsDirectory = metadata.getFirstDirectoryOfType<GpsDirectory>()
        gpsDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val panasonicRawDistortionDirectory = metadata.getFirstDirectoryOfType<PanasonicRawDistortionDirectory>()
        panasonicRawDistortionDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val panasonicRawIFD0Directory = metadata.getFirstDirectoryOfType<PanasonicRawIFD0Directory>()
        panasonicRawIFD0Directory?.let { directory ->
            TODO("Read directory $directory")
        }
        val panasonicRawWbInfo2Directory = metadata.getFirstDirectoryOfType<PanasonicRawWbInfo2Directory>()
        panasonicRawWbInfo2Directory?.let { directory ->
            TODO("Read directory $directory")
        }
        val panasonicRawWbInfoDirectory = metadata.getFirstDirectoryOfType<PanasonicRawWbInfoDirectory>()
        panasonicRawWbInfoDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val printIMDirectory = metadata.getFirstDirectoryOfType<PrintIMDirectory>()
        printIMDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val fileSystemDirectory = metadata.getFirstDirectoryOfType<FileSystemDirectory>()
        fileSystemDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val fileTypeDirectory = metadata.getFirstDirectoryOfType<FileTypeDirectory>()
        fileTypeDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gifAnimationDirectory = metadata.getFirstDirectoryOfType<GifAnimationDirectory>()
        gifAnimationDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gifCommentDirectory = metadata.getFirstDirectoryOfType<GifCommentDirectory>()
        gifCommentDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gifControlDirectory = metadata.getFirstDirectoryOfType<GifControlDirectory>()
        gifControlDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gifHeaderDirectory = metadata.getFirstDirectoryOfType<GifHeaderDirectory>()
        gifHeaderDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val gifImageDirectory = metadata.getFirstDirectoryOfType<GifImageDirectory>()
        gifImageDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val iccDirectory = metadata.getFirstDirectoryOfType<IccDirectory>()
        iccDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val icoDirectory = metadata.getFirstDirectoryOfType<IcoDirectory>()
        icoDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val iptcDirectory = metadata.getFirstDirectoryOfType<IptcDirectory>()
        iptcDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val jfifDirectory = metadata.getFirstDirectoryOfType<JfifDirectory>()
        jfifDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val jfxxDirectory = metadata.getFirstDirectoryOfType<JfxxDirectory>()
        jfxxDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val huffmanTablesDirectory = metadata.getFirstDirectoryOfType<HuffmanTablesDirectory>()
        huffmanTablesDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val jpegCommentDirectory = metadata.getFirstDirectoryOfType<JpegCommentDirectory>()
        jpegCommentDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val jpegDirectory = metadata.getFirstDirectoryOfType<JpegDirectory>()
        jpegDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val quickTimeDirectory = metadata.getFirstDirectoryOfType<QuickTimeDirectory>()
        quickTimeDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val mp4Directory = metadata.getFirstDirectoryOfType<Mp4Directory>()
        mp4Directory?.let { directory ->
            TODO("Read directory $directory")
        }
        val pcxDirectory = metadata.getFirstDirectoryOfType<PcxDirectory>()
        pcxDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val photoshopDirectory = metadata.getFirstDirectoryOfType<PhotoshopDirectory>()
        photoshopDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val psdHeaderDirectory = metadata.getFirstDirectoryOfType<PsdHeaderDirectory>()
        psdHeaderDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val pngDirectory = metadata.getFirstDirectoryOfType<PngDirectory>()
        pngDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val wavDirectory = metadata.getFirstDirectoryOfType<WavDirectory>()
        wavDirectory?.let { directory ->
            TODO("Read directory $directory")
        }
        val webpDirectory = metadata.getFirstDirectoryOfType<WebpDirectory>()
        webpDirectory?.let { directory ->
            directory.getBoolean(WebpDirectory.TAG_HAS_ALPHA)
            directory.getInt(WebpDirectory.TAG_IMAGE_HEIGHT)
            directory.getInt(WebpDirectory.TAG_IMAGE_WIDTH)
            directory.getBoolean(WebpDirectory.TAG_IS_ANIMATION)
            TODO("Read directory $directory")
        }
        val xmpDirectory = metadata.getFirstDirectoryOfType<XmpDirectory>()
        xmpDirectory?.let { directory ->
            directory.xmpMeta
            directory.xmpProperties
            TODO("Read directory $directory")
        }

        return null
    }

    private inline fun <reified T : Directory> DrewMetadata.getFirstDirectoryOfType(): T? =
            getFirstDirectoryOfType(T::class.java)

    private inline fun <reified T : Directory> DrewMetadata.getDirectoriesOfType(): Collection<T> =
            getDirectoriesOfType(T::class.java)
}