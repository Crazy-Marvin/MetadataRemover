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

import java.util.*


/**
 * Produces the [cartesian product](http://en.wikipedia.org/wiki/Cartesian_product#n-ary_product)
 * as a [Sequence] of ordered [Pair]s of elements lazily obtained from two [Sequence] instances.
 */
operator fun <A : Any, B : Any> Sequence<A>.times(other: Sequence<B>): Sequence<Pair<A, B>> {
    val firstIterator = iterator()
    var secondIterator = other.iterator()
    var currentFirstElement: A? = null

    fun nextPair(): Pair<A, B>? {
        if (currentFirstElement == null && firstIterator.hasNext()) {
            currentFirstElement = firstIterator.next()
        }


        if (secondIterator.hasNext()) {
            return currentFirstElement!! to secondIterator.next()
        }
        if (firstIterator.hasNext()) {
            currentFirstElement = firstIterator.next()
            secondIterator = other.iterator()
            return currentFirstElement!! to secondIterator.next()
        }

        return null
    }

    return generateSequence { nextPair() }
}

/**
 * Produces the [cartesian product](http://en.wikipedia.org/wiki/Cartesian_product#n-ary_product)
 * as a [Iterable] of ordered [Pair]s of elements lazily obtained from two [Iterable] instances.
 */
operator fun <A : Any, B : Any> Iterable<A>.times(other: Iterable<B>): Iterable<Pair<A, B>> {
    return flatMap { thisElement ->
        other.map { otherElement ->
            thisElement to otherElement
        }
    }
}

infix fun <T> Set<T>.intersect(other: Set<T>): Set<T> =
        filterTo(mutableSetOf()) { it in other }

infix fun <T> List<T>.intersect(other: List<T>): List<T> =
        filterTo(mutableListOf()) { it in other }

infix fun <K, V> Map<K, V>.intersect(other: Map<K, V>): Map<K, V> =
        filterTo(mutableMapOf()) { it in other.entries }

/**
 * Returns a new [Deque] with the given elements.
 * Elements of the set are iterated in the order they were specified.
 */
fun <T> dequeOf(vararg elements: T): Deque<T> = elements.toCollection(LinkedList())
