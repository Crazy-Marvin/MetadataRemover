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
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

private object PreferenceKeys {
    internal const val FIRST_START = "com.heinrichreimer.meinemensa.FIRST_START"
}

/**
 * Determine whether this [android.app.Application] is running the first time after installation.
 */
val Context.isFirstStart: Boolean
    get() = getPreferences()
            .let { preferences ->
                if (!preferences[PreferenceKeys.FIRST_START, true]) {
                    // Not the first start
                    false
                } else {
                    // First start
                    preferences.edit {
                        set(PreferenceKeys.FIRST_START, false)
                    }
                    true
                }
            }

/**
 * Determine whether the given permission is allowed for this process/user running in the system.
 *
 * @param permission The uri of the permission being checked.
 *
 * @return [PackageManager.PERMISSION_GRANTED] if this process/user is allowed that permission,
 *  or [PackageManager.PERMISSION_DENIED] if it is not.
 *
 * @see PackageManager.checkPermission
 * @see Context.checkCallingPermission
 */
fun Context.checkPermission(permission: String): Int {
    return ContextCompat.checkSelfPermission(this, permission)
}

/**
 * Determine whether the given permission is allowed for this process/user running in the system.
 *
 * @param permission The uri of the permission being checked.
 *
 * @return `true` if this process/user is allowed that permission, or `false` if it is not.
 *
 * @see checkPermission
 */
fun Context.hasPermission(permission: String): Boolean {
    return checkPermission(permission) == PackageManager.PERMISSION_GRANTED
}

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
