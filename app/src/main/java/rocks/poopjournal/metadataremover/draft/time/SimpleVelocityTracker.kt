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

import rocks.poopjournal.metadataremover.util.Logger
import java.util.*
import java.util.concurrent.TimeUnit

class SimpleVelocityTracker(
        maxLifetime: Time = DEFAULT_MAX_LIFETIME
) : MutableSet<Float> {

    val values: NavigableSet<TimedValue> = LimitedSizeSet(maxLifetime)

    override fun add(element: Float) = values.add(TimedValue(element))
    override fun addAll(elements: Collection<Float>) = values.addAll(elements.map { TimedValue(it) })
    override fun clear() = values.clear()
    override fun iterator() = values.map { it.value }.toMutableSet().iterator()
    override fun remove(element: Float) = values.removeAll { it.value == element }
    override fun removeAll(elements: Collection<Float>) = values.removeAll { it.value in elements }
    override fun retainAll(elements: Collection<Float>) = values.retainAll { it.value in elements }
    override val size get() = values.size
    override fun contains(element: Float) = values.any { it.value == element }
    override fun containsAll(elements: Collection<Float>) = elements.all { contains(it) }
    override fun isEmpty() = values.isEmpty()

    fun getVelocity(perUnit: TimeUnit): Float {
        if (size <= 1) {
            // If there are no different values, the velocity must be zero.
            return 0f
        }
        Logger.d("$size elements: ${values.joinToString(limit = 10) { it.time.toString() }}")

        // TODO Improve accuracy.
        // Does calculating the speed of different samples of the set
        // improve the accuracy of the average speed in comparison
        // to just calculating the slope between the first and last known value?
        val first = values.first()
        val last = values.last()
        val duration =
                perUnit.convert(first.time.time, first.time.unit) -
                        perUnit.convert(last.time.time, last.time.unit)
        if (duration == 0L) {
            return 0f
        }
        val distance = first.value - last.value
        return distance / duration
    }

    companion object {
        val DEFAULT_MAX_LIFETIME = Time(250, TimeUnit.MILLISECONDS)
    }

    data class TimedValue(
            val value: Float,
            val time: Time
    ) : Comparable<TimedValue> {
        constructor(value: Float) : this(value, Time.now)

        override fun compareTo(other: TimedValue) = time.compareTo(other.time)
    }

    class LimitedSizeSet(
            private val maxLifetime: Time
    ) : TreeSet<TimedValue>() {

        override fun addAll(elements: Collection<TimedValue>): Boolean {
            var modified = false
            for (element in elements) {
                if (add(element)) {
                    modified = true
                }
            }
            return modified
        }

        override fun add(element: TimedValue): Boolean {
            val modified = super.add(element)
            removeOutdated()
            return modified
        }

        private fun removeOutdated(): Int {
            val sizeBeforeRemoval = size
            val leastRecent = last()
            // Remove all elements older than the least recent one plus the
            //Logger.d(joinToString { "${leastRecent.time - it.time}, $maxLifetime" })
            removeAll { leastRecent.time - it.time > maxLifetime }
            return sizeBeforeRemoval - size
        }
    }
}