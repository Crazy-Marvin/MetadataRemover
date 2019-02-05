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

package rocks.poopjournal.metadataremover.model.resources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import rocks.poopjournal.metadataremover.model.resources.Image.Type.*
import rocks.poopjournal.metadataremover.util.extensions.glide.async
import rocks.poopjournal.metadataremover.util.extensions.parseUri
import java.io.File

/**
 * An umbrella container for several graphics representations, including [Bitmap]s,
 * compressed [Bitmap] images (e.g. JPG or PNG)
 * and [drawable resources][DrawableRes] (including vectors).
 *
 * This class is a simplified copy of [android.graphics.drawable.Icon] which is released under
 * the Apache License 2.0.
 */
class Image private constructor(
        /**
         * This image's type as in [Text.Type].
         */
        private val type: Type,

        /**
         * [bitmap], [bytes], [uri] or `null`, depending on the [type].
         */
        private var object1: Any? = null,

        /**
         * [resId], [bytesStartOffset] or `null`, depending on the [type].
         */
        private val int1: Int? = null,

        /**
         * [bytesLength] or `null`, depending on the [type].
         */
        private val int2: Int? = null
) {
    /**
     * Image source type.
     */
    enum class Type {
        BITMAP,
        RESOURCE,
        BYTES,
        URI,
        ADAPTIVE_BITMAP
    }

    /**
     * Throws an exception if this image's type is not [allowed][allowedTypes].
     */
    private fun checkType(vararg allowedTypes: Type) {
        if (type !in allowedTypes) {
            throw IllegalStateException("The image type was expected to be " +
                    "one of ${allowedTypes.joinToString()} but was $type instead.")
        }
    }

    /**
     * The image's [Bitmap] source.
     *
     * Note: This property is only available to [BITMAP] and [ADAPTIVE_BITMAP] images.
     */
    private val bitmap: Bitmap
        get() {
            checkType(BITMAP, ADAPTIVE_BITMAP)
            return object1 as Bitmap
        }

    /**
     * The image's [DrawableRes] ID source.
     *
     * Note: This property is only available to [RESOURCE] images.
     */
    private val resId: Int
        @DrawableRes get() {
            checkType(RESOURCE)
            return int1 as Int
        }

    /**
     * The image's [ByteArray] source.
     *
     * Note: This property is only available to [BYTES] images.
     */
    private val bytes: ByteArray
        get() {
            checkType(BYTES)
            return object1 as ByteArray
        }
    /**
     * The [bytes]'s start offset.
     *
     * Note: This property is only available to [BYTES] images.
     */
    private val bytesStartOffset: Int
        @IntRange(from = 0) get() {
            checkType(BYTES)
            return int1 ?: 0
        }
    /**
     * The [bytes]'s length.
     *
     * Note: This property is only available to [BYTES] images.
     */
    private val bytesLength: Int
        @IntRange(from = 0) get() {
            checkType(BYTES)
            return int2 ?: (bytes.size - bytesStartOffset)
        }

    /**
     * The image's [Uri] source.
     *
     * Note: This property is only available to [URI] images.
     */
    private val uri: Uri
        get() {
            checkType(URI)
            return object1 as Uri
        }


    /**
     * Create an [Image] pointing to a bitmap in memory.
     *
     * @param bitmap A valid [Bitmap] object.
     * @param adaptive Whether or not the bitmap follows the icon design guideline defined
     * by [AdaptiveIconDrawable].
     */
    constructor(bitmap: Bitmap, adaptive: Boolean = false) : this(
            type = if (adaptive) ADAPTIVE_BITMAP else BITMAP,
            object1 = bitmap
    )

    /**
     * Create an [Image] pointing to a drawable resource.
     *
     * @param resId ID of the drawable resource.
     */
    constructor(@DrawableRes resId: Int) : this(
            type = RESOURCE,
            int1 = resId
    )

    /**
     * Create an [Image] pointing to a compressed bitmap stored in a byte array.
     *
     * @param bytes Byte array storing compressed bitmap data of a type that [BitmapFactory]
     * can decode (see [Bitmap.CompressFormat]).
     * @param startOffset Offset into [bytes] at which the bitmap data starts
     * or `null` if no offset is specified aka. the bitmap data starts at the first position.
     * @param length Length of the bitmap data or `null` if no length is specified aka. the
     * bitmap data continues to the end of the array.
     */
    constructor(bytes: ByteArray, @IntRange(from = 0) startOffset: Int? = null, @IntRange(from = 0) length: Int? = null) : this(
            type = BYTES,
            object1 = bytes,
            int1 = startOffset,
            int2 = length
    )

    /**
     * Create an [Image] pointing to an image file specified by [Uri].
     *
     * @param uri A URI referring to local `content://` or `file://` image data.
     */
    constructor(uri: Uri) : this(
            type = URI,
            object1 = uri
    )

    /**
     * Create an [Image] pointing to an image file specified by [Uri].
     *
     * @param uri A URI referring to local `content://` or `file://` image data.
     */
    constructor(uri: String) : this(uri.parseUri())

    /**
     * Create an [Image] pointing to an image [File].
     *
     * @param file A [File] containing image data.
     */
    constructor(file: File) : this(file.toUri())

    /**
     * Load a [Drawable] containing this image.
     *
     * @return The [Drawable] containing this image or `null` if this image could not be loaded.
     */
    suspend fun load(context: Context): Drawable? {
        return when (type) {
            BITMAP -> BitmapDrawable(context.resources, bitmap)
            ADAPTIVE_BITMAP -> {
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    AdaptiveIconDrawable(
                            null,
                            BitmapDrawable(context.resources, bitmap)
                    )
                } else null
            }
            RESOURCE -> ContextCompat.getDrawable(context, resId)
            BYTES -> {
                BitmapDrawable(
                        context.resources,
                        BitmapFactory.decodeByteArray(bytes, bytesStartOffset, bytesLength)
                )
            }
            URI -> Glide.with(context)
                    .asDrawable()
                    .load(uri)
                    .async()
                    .await()
        }
    }

    suspend operator fun invoke(context: Context) = load(context)

    companion object {
        private val EMPTY_IMAGE = Image(android.R.color.transparent)
        fun emptyImage() = EMPTY_IMAGE
    }
}