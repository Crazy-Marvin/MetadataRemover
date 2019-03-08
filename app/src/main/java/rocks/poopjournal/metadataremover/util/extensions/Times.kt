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

package rocks.poopjournal.metadataremover.util.extensions

import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

val ATTRIBUTE_DATE_FORMAT: DateFormat = DateFormat.getDateInstance(DateFormat.LONG)
val ATTRIBUTE_TIME_FORMAT: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
val RFC_822_DATE_FORMAT: DateFormat = SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US)

fun String.toRfc822Calendar() = RFC_822_DATE_FORMAT.parse(this).toCalendar()

fun DateFormat.parseOrNull(source: String): Date? = parse(source, ParsePosition(0))

fun String.toRfc822CalendarOrNull() = RFC_822_DATE_FORMAT.parseOrNull(this)?.toCalendar()

fun Date.toCalendar(): Calendar = Calendar.getInstance().also { it.time = this }