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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.ViewPropertyAnimator

inline fun ViewPropertyAnimator.setStartListener(
        crossinline listener: ViewPropertyAnimator.() -> Unit): ViewPropertyAnimator =
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                listener()
            }
        })

inline fun ViewPropertyAnimator.setEndListener(
        crossinline listener: ViewPropertyAnimator.(canceled: Boolean) -> Unit): ViewPropertyAnimator =
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                listener(true)
            }

            override fun onAnimationEnd(animation: Animator) {
                listener(false)
            }
        })