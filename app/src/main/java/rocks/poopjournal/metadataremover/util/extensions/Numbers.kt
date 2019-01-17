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

import rocks.poopjournal.metadataremover.util.extensions.ScalePrefixes.SI_DOWNSCALE_PREFIXES
import rocks.poopjournal.metadataremover.util.extensions.ScalePrefixes.SI_SCALE_FACTOR
import rocks.poopjournal.metadataremover.util.extensions.ScalePrefixes.SI_UPSCALE_PREFIXES
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sign

private object ScalePrefixes {
    const val SI_SCALE_FACTOR = 1_000.0
    val SI_UPSCALE_PREFIXES = arrayOf("k", "M", "G", "T", "G", "P", "E", "Z", "Y")
    val SI_DOWNSCALE_PREFIXES = arrayOf("m", "µ", "n", "p", "f", "a", "z", "y")
}

/**
 * Format a number scaled down in [steps][scaleFactor] on a linear scale
 * with a label consisting of a [scale prefix][upscalePrefixes] and optional [unit].
 */
fun Double.formatScaled(
        digits: Int,
        unit: String = "",
        scaleFactor: Double = SI_SCALE_FACTOR,
        upscalePrefixes: Array<String> = SI_UPSCALE_PREFIXES,
        downscalePrefixes: Array<String> = SI_DOWNSCALE_PREFIXES
): String {
    return when {
        absoluteValue >= scaleFactor -> {
            var scaled = absoluteValue
            var prefixIndex = -1
            while (scaled >= scaleFactor && prefixIndex + 1 < upscalePrefixes.size) {
                scaled /= scaleFactor
                prefixIndex++
            }
            scaled *= sign
            "${scaled.format(digits)} ${upscalePrefixes[prefixIndex]}$unit"
        }
        absoluteValue < 1 / scaleFactor -> {
            var scaled = absoluteValue
            var prefixIndex = -1
            while (scaled < 1 / scaleFactor && prefixIndex + 1 < downscalePrefixes.size) {
                scaled *= scaleFactor
                prefixIndex++
            }
            scaled *= sign
            "${scaled.format(digits)} ${downscalePrefixes[prefixIndex]}$unit"
        }
        else -> {
            "${format(digits)} $unit"
        }
    }
}

/**
 * Format a number scaled down in [steps][scaleFactor] on a linear scale
 * with a label consisting of a [scale prefix][upscalePrefixes] and optional [unit].
 */
fun Number.formatScaled(
        digits: Int,
        unit: String = "",
        scaleFactor: Number = SI_SCALE_FACTOR,
        upscalePrefixes: Array<String> = SI_UPSCALE_PREFIXES,
        downscalePrefixes: Array<String> = SI_DOWNSCALE_PREFIXES
) = toDouble()
        .formatScaled(
                digits,
                unit,
                scaleFactor.toDouble(),
                upscalePrefixes,
                downscalePrefixes
        )

fun Number.format(digits: Int): String =
        DecimalFormat("#.${"#".repeat(digits)}")
                .apply { roundingMode = RoundingMode.HALF_UP }
                .format(this)

fun BigDecimal.format(format: DecimalFormat): String = format.format(this)

fun DecimalFormat.parseBigDecimal(source: String): BigDecimal? {
    isParseBigDecimal = true
    return try {
        parseObject(source) as? BigDecimal
    } catch (ignore: ParseException) {
        null
    }
}

val Locale.numberFormat: NumberFormat
    get() = NumberFormat.getInstance(this)
val Locale.decimalFormat: DecimalFormat
    get() = numberFormat as DecimalFormat