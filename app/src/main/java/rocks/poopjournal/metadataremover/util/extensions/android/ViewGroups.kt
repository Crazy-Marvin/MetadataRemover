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

import android.view.View
import android.view.ViewGroup

/**
 * Performs the given [action] on each child [View].
 */
inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

/**
 * Performs the given [action] on each  child [View], providing sequential index with the element.
 *
 * @param [action] Function that takes the index of an element and the element itself
 *  and performs the desired action on the element.
 */
inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (index in 0 until childCount) {
        action(index, getChildAt(index))
    }
}


/**
 * Returns the [View] at the specified position in the group.
 *
 * @param index The position at which to get the view from.
 *
 * @return The view at the specified position or `null` if the position
 *  does not exist within the group.
 */
operator fun ViewGroup.get(index: Int): View? = getChildAt(index)

/**
 * Removes a child view.
 *
 * **Note:** Do not invoke this method from [View.draw], [View.onDraw], [View.dispatchDraw]
 * or any related method.
 *
 * @param child The child view to remove.
 */
operator fun ViewGroup.minusAssign(child: View) = removeView(child)

/**
 * Adds a child view. If no layout parameters are already set on the child, the
 * default parameters for this [ViewGroup] are set on the child.
 *
 * **Note:** Do not invoke this method from [View.draw], [View.onDraw], [View.dispatchDraw]
 * or any related method.
 *
 * @param child The child view to add.
 *
 * @see #generateDefaultLayoutParams()
 */
operator fun ViewGroup.plusAssign(child: View) = addView(child)

/**
 * Returns `true` if [child] is found in the [ViewGroup].
 */
operator fun ViewGroup.contains(child: View) = indexOfChild(child) != -1

/**
 * Returns the number of children in the [ViewGroup].
 *
 * @return A positive integer representing the number of children in the group.
 */
val ViewGroup.size: Int
    get() = childCount

/**
 * Returns this [ViewGroup]'s children as an [Iterable].
 */
val ViewGroup.children
    get() = object : Iterable<View> {
        override fun iterator() = object : Iterator<View> {
            var index = 0
            override fun hasNext() = index < childCount
            override fun next() = getChildAt(index++)
        }
    }
