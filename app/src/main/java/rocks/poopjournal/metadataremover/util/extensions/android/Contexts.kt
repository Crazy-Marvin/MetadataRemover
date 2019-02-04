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

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import java.util.*

/**
 * Obtains the [LayoutInflater] from this [Context].
 */
val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

@ColorInt
fun Context.getThemeColor(@AttrRes attribute: Int): Int {
    return theme.getColor(attribute)
}

@ColorInt
fun Resources.Theme.getColor(@AttrRes attribute: Int): Int {
    return TypedValue()
            .also { resolveAttribute(attribute, it, true) }
            .data
}

val Context.defaultLocale: Locale?
    get() = resources.defaultLocale

val Resources.defaultLocale: Locale?
    get() = configuration.defaultLocale

val Configuration.defaultLocale: Locale?
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales.takeUnless(LocaleList::isEmpty)?.get(0)
        } else {
            @Suppress("DEPRECATION")
            locale
        }
    }
