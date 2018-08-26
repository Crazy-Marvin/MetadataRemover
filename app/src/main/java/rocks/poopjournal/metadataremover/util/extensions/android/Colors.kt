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

package rocks.poopjournal.metadataremover.util.extensions.android

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import java.lang.IllegalArgumentException

/**
 * Try to parse a [toColorOrNull][ColorInt] from this [String] or
 * return `null` if no toColorOrNull could be parsed.
 */
@ColorInt
internal fun String.toColorOrNull(): Int? {
    return try {
        Color.parseColor(this)
    } catch (exception: IllegalArgumentException) {
        null
    }
}

/**
 * Specifies a tint for this [Drawable] after mutating it to not affect in-memory copies
 * and wrapping it to support backwards compatibility.
 *
 * @see DrawableCompat.setTint
 * @see Drawable.mutate
 */
fun Drawable.tint(@ColorInt color: Int): Drawable =
        DrawableCompat.wrap(this)
                .mutate()
                .also { drawable ->
                    DrawableCompat.setTint(drawable, color)
                }

/**
 * Specifies a tint for this [ImageView]'s [Drawable].
 */
fun ImageView.tint(@ColorInt color: Int) {
    drawable?.tint(color)
            ?.let { setImageDrawable(it) }
}

/**
 * Specifies a tint for this [MenuItem]'s icon [Drawable].
 */
fun MenuItem.tintIcon(@ColorInt color: Int) {
    icon?.tint(color)
            ?.let { icon = it }
}

/**
 * Specifies a tint for this [View]'s background.
 */
fun View.tintBackground(@ColorInt color: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(color))
}
