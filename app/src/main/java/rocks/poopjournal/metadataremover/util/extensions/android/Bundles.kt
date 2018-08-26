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

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

operator fun Bundle.set(name: String, value: Boolean): Bundle {
    putBoolean(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Byte): Bundle {
    putByte(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Char): Bundle {
    putChar(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Short): Bundle {
    putShort(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Int): Bundle {
    putInt(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Long): Bundle {
    putLong(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Float): Bundle {
    putFloat(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Double): Bundle {
    putDouble(name, value)
    return this
}

operator fun Bundle.set(name: String, value: String): Bundle {
    putString(name, value)
    return this
}

operator fun Bundle.set(name: String, value: CharSequence): Bundle {
    putCharSequence(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Parcelable): Bundle {
    putParcelable(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Array<Parcelable>): Bundle {
    putParcelableArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Serializable): Bundle {
    putSerializable(name, value)
    return this
}

operator fun Bundle.set(name: String, value: BooleanArray): Bundle {
    putBooleanArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: ByteArray): Bundle {
    putByteArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: ShortArray): Bundle {
    putShortArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: CharArray): Bundle {
    putCharArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: IntArray): Bundle {
    putIntArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: LongArray): Bundle {
    putLongArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: FloatArray): Bundle {
    putFloatArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: DoubleArray): Bundle {
    putDoubleArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Array<String>): Bundle {
    putStringArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Array<CharSequence>): Bundle {
    putCharSequenceArray(name, value)
    return this
}

operator fun Bundle.set(name: String, value: Bundle): Bundle {
    putBundle(name, value)
    return this
}
