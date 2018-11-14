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

package rocks.poopjournal.metadataremover.draft.time

import java.util.concurrent.TimeUnit

data class Time(
        val time: Long,
        val unit: TimeUnit
) : Comparable<Time> {

    /**
     * Compare the mapped times in descending magnitudes.
     * This prevents overflow issues when comparing very large times with very small ones.
     */
    override fun compareTo(other: Time) =
            MOST_PRECISE_UNIT.convert(this).compareTo(MOST_PRECISE_UNIT.convert(other))

    operator fun plus(other: Time) =
            Time(MOST_PRECISE_UNIT.convert(this) + MOST_PRECISE_UNIT.convert(other),
                    MOST_PRECISE_UNIT)

    operator fun minus(other: Time) =
            Time(MOST_PRECISE_UNIT.convert(this) - MOST_PRECISE_UNIT.convert(other),
                    MOST_PRECISE_UNIT)

    private fun TimeUnit.convert(time: Time) = convert(time.time, time.unit)

    companion object {
        val now get() = Time(System.nanoTime(), TimeUnit.NANOSECONDS)
        private val MOST_PRECISE_UNIT = TimeUnit.NANOSECONDS
    }
}