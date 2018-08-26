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

package rocks.poopjournal.metadataremover.util.extensions.okio

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okio.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.file.Path

/**
 * Returns a new [Source] that buffers reads from this original source.
 * The returned source will perform bulk reads into its in-memory buffer.
 * Use this wherever you read a source to get an ergonomic and efficient access to data.
 */
fun Source.buffer(): BufferedSource = Okio.buffer(this)

/**
 * Returns a new [Sink] that buffers writes to this original sink.
 * The returned sink will batch writes to this original sink.
 * Use this wherever you write to a sink to get an ergonomic and efficient access to data.
 */
fun Sink.buffer(): BufferedSink = Okio.buffer(this)

/**
 * Returns a [Sink] that writes to this [OutputStream].
 */
val OutputStream.sink: Sink
    get() = Okio.sink(this)

/**
 * Returns a [Sink] that writes to this [Socket].
 */
val Socket.sink: Sink
    get() = Okio.sink(this)

/**
 * Returns a [Sink] that writes to this [File].
 */
val File.sink: Sink
    get() = Okio.sink(this)

/**
 * Returns a [Sink] that writes to this [Path].
 */
val Path.sink: Sink
    get() = Okio.sink(this)

/**
 * Returns a [Sink] that appends to this [File].
 */
val File.appendingSink: Sink
    get() = Okio.appendingSink(this)

/**
 * Returns a [Source] that reads from this [InputStream].
 */
val InputStream.source: Source
    get() = Okio.source(this)

/**
 * Returns a [Source] that reads from this [Socket].
 */
val Socket.source: Source
    get() = Okio.source(this)

/**
 * Returns a [Source] that reads from this [File].
 */
val File.source: Source
    get() = Okio.source(this)

/**
 * Returns a [Source] that reads from this [Path].
 */
val Path.source: Source
    get() = Okio.source(this)

/**
 * Returns an [OutputStream] that writes to this [BufferedSink].
 */
val BufferedSink.outputStream: OutputStream
    get() = outputStream()

/**
 * Returns an [InputStream] that reads from this [BufferedSource].
 */
val BufferedSource.inputStream: InputStream
    get() = inputStream()

/**
 * Decode a [Bitmap] from an [Source].
 */
fun Source.toBitmap(): Bitmap = buffer().inputStream.toBitmap()

/**
 * Decode a [Bitmap] from an [InputStream].
 */
fun InputStream.toBitmap(): Bitmap = BitmapFactory.decodeStream(this)

fun BufferedSource.pipe(
        to: BufferedSink = Buffer(),
        transform: (
                source: BufferedSource,
                sink: BufferedSink) -> Unit): Buffer {
    val out: Buffer = to.buffer()
    use { source ->
        transform(source, out)
    }
    return out
}