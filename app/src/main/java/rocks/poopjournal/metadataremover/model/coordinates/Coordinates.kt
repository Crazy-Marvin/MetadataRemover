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
import kotlin.math.max
import kotlin.math.min

sealed class Coordinate(value: Double, range: ClosedRange<Double>) {

    private val min: Double = min(range.start, range.endInclusive)
    private val max: Double = max(range.start, range.endInclusive)
    private val interval: Double = (max - min).absoluteValue

    val value: Double = value.normalize()
    private val absoluteValue: Double = this.value.absoluteValue

    @get:StringRes
    protected abstract val directionRes: Int
    val direction get() = Text(directionRes)

    val hours: Long = absoluteValue.toLong()
    val minutes: Long = ((absoluteValue - hours) * 60).toLong()
    val seconds: Double = ((absoluteValue - hours) * 60 - minutes) * 60

    private fun Double.normalize(): Double {
        return when {
            this < min -> (this + interval).normalize()
            this > max -> (this - interval).normalize()
            else -> this
        }
    }
}

class Latitude(value: Double) : Coordinate(value, -180.0..180.0) {

    constructor(
            hours: Int,
            minutes: Int,
            seconds: Double
    ) : this((seconds / 60 + minutes) / 60 + hours)

    @StringRes
    override val directionRes =
            if (this.value < 0) R.string.description_attribute_file_creation_location_direction_west
            else R.string.description_attribute_file_creation_location_direction_east

    override fun toString(): String {
        val direction = if (value < 0) "West" else "East"
        return "${hours.absoluteValue}  $minutes' $seconds\" $direction"
    }
}

class Longitude(value: Double) : Coordinate(value, -90.0..90.0) {

    constructor(
            hours: Int,
            minutes: Int,
            seconds: Double
    ) : this((seconds / 60 + minutes) / 60 + hours)

    @StringRes
    override val directionRes =
            if (this.value < 0) R.string.description_attribute_file_creation_location_direction_south
            else R.string.description_attribute_file_creation_location_direction_north

    override fun toString(): String {
        val direction = if (value < 0) "South" else "North"
        return "${hours.absoluteValue}  $minutes' $seconds\" $direction"
    }
}

data class Coordinates(
        val latitude: Latitude,
        val longitude: Longitude
)
