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
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Create an [Intent] for the component specified by [ComponentType].
 */
@JvmName("intent")
inline fun <reified ComponentType> Context.intent(): Intent = Intent(this, ComponentType::class.java)

operator fun Intent.set(name: String, value: Boolean): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Byte): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Char): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Short): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Int): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Long): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Float): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Double): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: String): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: CharSequence): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Parcelable): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<Parcelable>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Serializable): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: BooleanArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: ByteArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: ShortArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: CharArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: IntArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: LongArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: FloatArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: DoubleArray): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<String>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Array<CharSequence>): Intent = putExtra(name, value)
operator fun Intent.set(name: String, value: Bundle): Intent = putExtra(name, value)
