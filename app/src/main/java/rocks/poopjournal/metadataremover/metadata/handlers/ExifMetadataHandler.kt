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
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.metadata.MetadataHandler
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.MediaTypes
import rocks.poopjournal.metadataremover.util.ImageFile
import rocks.poopjournal.metadataremover.util.extensions.android.*
import rocks.poopjournal.metadataremover.util.extensions.deleteIfExists
import java.io.ByteArrayOutputStream
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
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_DATETIME))
        }
        imageFile.resolutionAttribute.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_LENS_MODEL))
        }
        exif.exposureAttribute?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_EXPOSURE_TIME))
        }
        exif.getLocationAttribute(geocoder)?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_GPS_LATITUDE))
        }
        exif.cameraAttribute?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_MODEL))
        }
        exif.lensAttribute?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_LENS_MODEL))
        }
        exif.lightAttribute?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_FLASH))
        }
        exif.ownerAttribute?.let {
            attributes.add(it.copy(removable = true, tag = ExifInterface.TAG_ARTIST))
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

        outputFile.deleteIfExists()
        inputFile.copyTo(outputFile)

        val exif = ExifInterface(outputFile.path)

        val tagsToRemove = attributes
            .filter { it.removable && it.selected }
            .mapNotNull { it.tag }

        if (tagsToRemove.isEmpty()) {
            exif.clearAllAttributes()
        } else {
            tagsToRemove.forEach { tag -> exif.setAttribute(tag, null) }
        }
        exif.saveAttributes()

        val beforeBytes = outputFile.readBytes()
        val xmpBefore = containsXmp(beforeBytes)
        val trailerBefore = hasTrailer(beforeBytes)
        Log.d(TAG, "XMP[${outputFile.name}] pre-scrub : xmp=$xmpBefore trailer=$trailerBefore size=${beforeBytes.size}")

        val cleaned = scrubJpeg(beforeBytes)
        if (cleaned == null) {
            Log.w(TAG, "XMP[${outputFile.name}] scrubJpeg returned null (not a parseable JPEG) — left as-is")
        } else {
            outputFile.writeBytes(cleaned)
            val xmpAfter = containsXmp(cleaned)
            val trailerAfter = hasTrailer(cleaned)
            val removed = beforeBytes.size - cleaned.size
            Log.d(TAG, "XMP[${outputFile.name}] post-scrub: xmp=$xmpAfter trailer=$trailerAfter size=${cleaned.size} (removed $removed bytes)")
            when {
                xmpBefore && !xmpAfter -> Log.d(TAG, "XMP[${outputFile.name}] ✓ XMP removed successfully")
                xmpBefore && xmpAfter  -> Log.e(TAG, "XMP[${outputFile.name}] ✗ XMP STILL PRESENT after scrub")
                else                   -> Log.d(TAG, "XMP[${outputFile.name}] no XMP segment was present")
            }
        }

        return true
    }

    // ============================ JPEG byte-level scrubber ============================

    /**
     * Removes XMP (standard + extended) APP1 segments and truncates any trailer after
     * the EOI marker (e.g. Samsung SEF). Compressed scan data is copied byte-for-byte,
     * so there is NO recompression / quality loss. Returns null if not a parseable JPEG.
     */
    private fun scrubJpeg(data: ByteArray): ByteArray? {
        if (data.size < 2 || u(data, 0) != 0xFF || u(data, 1) != 0xD8) return null
        val out = ByteArrayOutputStream(data.size)
        out.write(0xFF); out.write(0xD8)                       // SOI

        var i = 2
        while (i + 1 < data.size) {
            if (u(data, i) != 0xFF) return null                // malformed -> refuse
            var m = i + 1
            while (m < data.size && u(data, m) == 0xFF) m++     // skip fill bytes
            if (m >= data.size) break
            val code = u(data, m)

            when {
                code == 0xDA -> {                               // Start Of Scan
                    if (m + 2 >= data.size) return null
                    val len = (u(data, m + 1) shl 8) or u(data, m + 2)
                    val headerEnd = m + 1 + len
                    if (headerEnd > data.size) return null
                    out.write(0xFF); out.write(0xDA)
                    out.write(data, m + 1, len)                 // SOS header
                    var j = headerEnd
                    var resumed = false
                    while (j < data.size) {
                        val b = u(data, j)
                        if (b != 0xFF) { out.write(b); j++; continue }
                        if (j + 1 >= data.size) { out.write(0xFF); j++; break }
                        val n = u(data, j + 1)
                        when {
                            n == 0x00       -> { out.write(0xFF); out.write(0x00); j += 2 }
                            n in 0xD0..0xD7 -> { out.write(0xFF); out.write(n);    j += 2 }
                            n == 0xD9       -> { out.write(0xFF); out.write(0xD9); return out.toByteArray() }
                            else            -> { i = j; resumed = true; break } // progressive
                        }
                    }
                    if (!resumed) break
                }
                code == 0xD9 -> { out.write(0xFF); out.write(0xD9); return out.toByteArray() }
                code in 0xD0..0xD7 || code == 0x01 -> { out.write(0xFF); out.write(code); i = m + 1 }
                else -> {                                       // length-bearing segment
                    if (m + 2 >= data.size) return null
                    val len = (u(data, m + 1) shl 8) or u(data, m + 2)
                    val payloadStart = m + 3
                    val payloadLen = len - 2
                    if (payloadLen < 0 || payloadStart + payloadLen > data.size) return null
                    val dropXmp = code == 0xE1 &&
                            (startsWith(data, payloadStart, XMP_SIG) ||
                                    startsWith(data, payloadStart, XMP_EXT_SIG))
                    if (!dropXmp) {
                        out.write(0xFF); out.write(code)
                        out.write(u(data, m + 1)); out.write(u(data, m + 2))
                        out.write(data, payloadStart, payloadLen)
                    }
                    i = payloadStart + payloadLen
                }
            }
        }
        return out.toByteArray()
    }

    private fun u(a: ByteArray, idx: Int) = a[idx].toInt() and 0xFF

    private fun startsWith(data: ByteArray, off: Int, sig: ByteArray): Boolean {
        if (off + sig.size > data.size) return false
        for (k in sig.indices) if (data[off + k] != sig[k]) return false
        return true
    }

    // ---- verification helpers (used only for logging) ----

    private fun containsXmp(data: ByteArray): Boolean =
        indexOf(data, XMP_SIG) >= 0 || indexOf(data, XMP_EXT_SIG) >= 0

    /** Heuristic: any bytes after the last EOI marker. Reliable AFTER scrub (should be false). */
    private fun hasTrailer(data: ByteArray): Boolean {
        val eoi = lastIndexOfMarker(data, 0xD9)
        return eoi >= 0 && eoi + 2 < data.size
    }

    private fun indexOf(haystack: ByteArray, needle: ByteArray): Int {
        if (needle.isEmpty() || haystack.size < needle.size) return -1
        outer@ for (i in 0..haystack.size - needle.size) {
            for (j in needle.indices) if (haystack[i + j] != needle[j]) continue@outer
            return i
        }
        return -1
    }

    private fun lastIndexOfMarker(data: ByteArray, code: Int): Int {
        for (i in data.size - 2 downTo 0) {
            if (u(data, i) == 0xFF && u(data, i + 1) == code) return i
        }
        return -1
    }

    companion object {
        private const val TAG = "DataWiper"
        private val XMP_SIG     = "http://ns.adobe.com/xap/1.0/\u0000".toByteArray(Charsets.US_ASCII)
        private val XMP_EXT_SIG = "http://ns.adobe.com/xmp/extension/\u0000".toByteArray(Charsets.US_ASCII)
    }
}