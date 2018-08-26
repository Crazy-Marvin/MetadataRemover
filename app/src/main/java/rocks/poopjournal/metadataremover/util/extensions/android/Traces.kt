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

import android.annotation.TargetApi
import android.os.Build
import android.os.Trace

/**
 * Trace a given section of code.
 *
 * At this time the vertical bar character `|`, newline character `\n`, and
 * null character `\0` are used internally by the tracing mechanism. If [sectionName] contains
 * these characters they will be replaced with a space character in the trace.
 *
 * @param sectionName The uri of the code section to appear in the trace. This may be at
 *  most 127 Unicode code units long.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
inline fun <T> trace(sectionName: String, body: () -> T): T {
    Trace.beginSection(sectionName)
    return try {
        body()
    } finally {
        Trace.endSection()
    }
}
