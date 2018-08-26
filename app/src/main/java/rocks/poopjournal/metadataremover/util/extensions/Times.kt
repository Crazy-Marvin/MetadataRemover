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

/**
 * This number of seconds as milliseconds.
 */
inline val Number.secondsToMilliseconds
    inline get() = toLong() * 1000


/**
 * This number of minutes as seconds.
 */
inline val Number.minutesToSeconds
    inline get() = toLong() * 60

/**
 * This number of minutes as milliseconds.
 */
inline val Number.minutesToMilliseconds
    inline get() = minutesToSeconds.secondsToMilliseconds


/**
 * This number of hours as minutes.
 */
inline val Number.hoursToMinutes
    inline get() = toLong() * 60

/**
 * This number of hours as seconds.
 */
inline val Number.hoursToSeconds
    inline get() = hoursToMinutes.minutesToSeconds

/**
 * This number of hours as milliseconds.
 */
inline val Number.hoursToMilliseconds
    inline get() = hoursToMinutes.minutesToMilliseconds


/**
 * This number of dateRange as hours.
 */
inline val Number.daysToHours
    inline get() = toLong() * 24

/**
 * This number of dateRange as minutes.
 */
inline val Number.daysToMinutes
    inline get() = daysToHours.hoursToMinutes

/**
 * This number of dateRange as seconds.
 */
inline val Number.daysToSeconds
    inline get() = daysToHours.hoursToSeconds

/**
 * This number of dateRange as milliseconds.
 */
inline val Number.daysToMilliseconds
    inline get() = daysToHours.hoursToMilliseconds
