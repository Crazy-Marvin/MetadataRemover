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

package rocks.poopjournal.metadataremover.model.coordinates

import androidx.annotation.StringRes
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.resources.Text
import kotlin.math.absoluteValue

sealed class Coordinate(value: Double) {

    val value: Double = value.normalize()
    abstract val range: ClosedRange<Double>
    private val min: Double get() = range.start
    private val max: Double get() = range.endInclusive
    private val interval: Double get() = max - min
    @StringRes
    private val directionRes =
            if (value < 0) R.string.description_attribute_file_creation_location_direction_west
            else R.string.description_attribute_file_creation_location_direction_east
    val direction = Text(directionRes)
    val hours: Int = (value % 1).toInt().absoluteValue
    val minutes: Int = ((value - hours) / 60 % 1).toInt()
    val seconds: Double = ((value - hours) / 60 - minutes)

    private fun Double.normalize(): Double {
        return when {
            this < min -> (this - min) % interval
            this > max -> (this - max) % interval
            else -> this
        }
    }
}

class Latitude(value: Double) : Coordinate(value) {

    constructor(
            hours: Int,
            minutes: Int,
            seconds: Double
    ) : this((seconds / 60 + minutes) / 60 + hours)

    override val range = -180.0..180.0

    override fun toString(): String {
        val direction = if (value < 0) "West" else "East"
        return "${hours.absoluteValue}  $minutes' $seconds\" $direction"
    }
}

class Longitude(value: Double) : Coordinate(value) {

    constructor(
            hours: Int,
            minutes: Int,
            seconds: Double
    ) : this((seconds / 60 + minutes) / 60 + hours)

    override val range = -90.0..90.0

    override fun toString(): String {
        val direction = if (value < 0) "South" else "North"
        return "${hours.absoluteValue}  $minutes' $seconds\" $direction"
    }
}
