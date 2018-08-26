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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import rocks.poopjournal.metadataremover.model.resources.Text

/**
 * Execute some [code][body], when the layout gets updated the first time.
 */
inline fun View.onGlobalLayout(crossinline body: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            body()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

/**
 * The visibility state of this view, either `true` for [View.VISIBLE] or `false` for [View.GONE].
 */
var View.isVisible
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

/**
 * Obtains the [LayoutInflater] from this [View]'s [Context].
 */
val View.layoutInflater: LayoutInflater
    get() = context.layoutInflater

/**
 * Sets the [contentDescription] to this [View].
 */
fun View.setContentDescription(contentDescription: Text) {
    setContentDescription(contentDescription.load(context))
}

/**
 * Register a callback to be invoked when this view is clicked. If this view is not
 * clickable, it becomes clickable.
 *
 * @param listener The callback that will run.
 *
 * @see View.setClickable
 */
fun View.onClick(listener: () -> Unit): View {
    setOnClickListener {
        listener()
    }
    return this
}
