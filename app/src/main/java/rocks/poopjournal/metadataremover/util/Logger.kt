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

package rocks.poopjournal.metadataremover.util

import android.util.Log
import rocks.poopjournal.metadataremover.BuildConfig
import timber.log.Timber

@Suppress("UNUSED")
object Logger {
    private const val USE_TIMBER = false
    private val SHOULD_LOG = BuildConfig.DEBUG

    private const val tag = "MetadataRemover"

    @JvmStatic
    fun v(t: Throwable) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.v(t)
                else -> Log.v(
                        tag, t.message, t)
            }
        }
    }

    @JvmStatic
    fun v(t: Throwable, message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.v(t, message, args)
                else -> Log.v(
                        tag, String.format(message, args), t)
            }
        }
    }

    @JvmStatic
    fun v(message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.v(message, args)
                else -> Log.v(
                        tag, String.format(message, args))
            }
        }
    }

    @JvmStatic
    fun d(t: Throwable) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.d(t)
                else -> Log.d(
                        tag, t.message, t)
            }
        }
    }

    @JvmStatic
    fun d(t: Throwable, message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.d(t, message, args)
                else -> Log.d(
                        tag, String.format(message, args), t)
            }
        }
    }

    @JvmStatic
    fun d(message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.d(message, args)
                else -> Log.d(
                        tag, String.format(message, args))
            }
        }
    }

    @JvmStatic
    fun i(t: Throwable) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.i(t)
                else -> Log.i(
                        tag, t.message, t)
            }
        }
    }

    @JvmStatic
    fun i(t: Throwable, message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.i(t, message, args)
                else -> Log.i(
                        tag, String.format(message, args), t)
            }
        }
    }

    @JvmStatic
    fun i(message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.i(message, args)
                else -> Log.i(
                        tag, String.format(message, args))
            }
        }
    }

    @JvmStatic
    fun w(t: Throwable) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.w(t)
                else -> Log.w(
                        tag, t.message, t)
            }
        }
    }

    @JvmStatic
    fun w(t: Throwable, message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.w(t, message, args)
                else -> Log.w(
                        tag, String.format(message, args), t)
            }
        }
    }

    @JvmStatic
    fun w(message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.w(message, args)
                else -> Log.w(
                        tag, String.format(message, args))
            }
        }
    }

    @JvmStatic
    fun e(t: Throwable) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.e(t)
                else -> Log.e(
                        tag, t.message, t)
            }
        }
    }

    @JvmStatic
    fun e(t: Throwable, message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.e(t, message, args)
                else -> Log.e(
                        tag, String.format(message, args), t)
            }
        }
    }

    @JvmStatic
    fun e(message: String, vararg args: Any?) {
        if (SHOULD_LOG) {
            when {
                USE_TIMBER -> Timber.e(message, args)
                else -> Log.e(
                        tag, String.format(message, args))
            }
        }
    }
}