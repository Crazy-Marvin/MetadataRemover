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

package rocks.poopjournal.metadataremover.util.extensions.android

import android.database.CharArrayBuffer
import android.database.Cursor

/**
 * Returns the value of the requested column as a byte array.
 *
 * The result and whether this method throws an exception when the
 * column value is `null` or the column type is not a blob type is
 * implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a byte array.
 */
fun Cursor.getBlob(columnName: String): ByteArray {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getBlob(columnIndex)
}

/**
 * Returns the value of the requested column as a string.
 *
 * The result and whether this method throws an exception when the
 * column value is `null` or the column type is not a string type is
 * implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a string.
 */
fun Cursor.getString(columnName: String): String {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getString(columnIndex)
}

/**
 * Retrieves the requested column text and stores it in the buffer provided.
 *
 * If the buffer size is not sufficient, a new char buffer will be allocated
 * and assigned to [CharArrayBuffer.data].
 *
 * @param columnName The name of the target column.
 * If the target column is `nul`, return [buffer]
 * @param buffer The buffer to copy the text into.
 */
fun Cursor.copyStringToBuffer(columnName: String, buffer: CharArrayBuffer) {
    val columnIndex = getColumnIndexOrThrow(columnName)
    copyStringToBuffer(columnIndex, buffer)
}

/**
 * Returns the value of the requested column as a short.
 *
 * The result and whether this method throws an exception when the
 * column value is `null`, the column type is not an integral type, or the
 * integer value is outside the range ([Short.MIN_VALUE], [Short.MAX_VALUE])
 * is implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a short.
 */
fun Cursor.getShort(columnName: String): Short {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getShort(columnIndex)
}

/**
 * Returns the value of the requested column as an integer.
 *
 * The result and whether this method throws an exception when the
 * column value is `null`, the column type is not an integral type, or the
 * integer value is outside the range ([Int.MIN_VALUE], [Integer.MAX_VALUE])
 * is implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as an integer.
 */
fun Cursor.getInt(columnName: String): Int {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getInt(columnIndex)
}

/**
 * Returns the value of the requested column as a long.
 *
 * The result and whether this method throws an exception when the
 * column value is `null`, the column type is not an integral type, or the
 * integer value is outside the range ([Long.MIN_VALUE], [Long.MAX_VALUE])
 * is implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a long.
 */
fun Cursor.getLong(columnName: String): Long {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getLong(columnIndex)
}

/**
 * Returns the value of the requested column as a float.
 *
 * The result and whether this method throws an exception when the
 * column value is `null`, the column type is not a floating-point type, or the
 * floating-point value is not representable as a float value is
 * implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a float.
 */
fun Cursor.getFloat(columnName: String): Float {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getFloat(columnIndex)
}

/**
 * Returns the value of the requested column as a double.
 *
 * The result and whether this method throws an exception when the
 * column value is `null`, the column type is not a floating-point type, or the
 * floating-point value is not representable as a double value is
 * implementation-defined.
 *
 * @param columnName The name of the target column.
 *
 * @return The value of that column as a double.
 */
fun Cursor.getDouble(columnName: String): Double {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getDouble(columnIndex)
}

/**
 * Returns data type of the given column's value.
 * The preferred type of the column is returned but the data may be converted to other types
 * as documented in the get-type methods such as [Cursor.getInt], [Cursor.getFloat] etc.
 *
 * Returned column types are
 *  * [Cursor.FIELD_TYPE_NULL]
 *  * [Cursor.FIELD_TYPE_INTEGER]
 *  * [Cursor.FIELD_TYPE_FLOAT]
 *  * [Cursor.FIELD_TYPE_STRING]
 *  * [Cursor.FIELD_TYPE_BLOB]
 *
 * @param columnName The name of the target column.
 *
 * @return Column value type.
 */
fun Cursor.getType(columnName: String): Int {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return getType(columnIndex)
}

/**
 * Returns `true` if the value in the indicated column is `null`.
 *
 * @param columnName The name of the target column.
 *
 * @return Whether the column value is `null`.
 */
fun Cursor.isNull(columnName: String): Boolean {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return isNull(columnIndex)
}
