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

package rocks.poopjournal.metadataremover

/**
 * Semantic version number.
 *
 * @property build Bump for dogfood builds, public betas, etc.
 * @property buildType Build type, e.g. alpha, beta, rc.
 */
data class Version(
        val major: Int,
        val minor: Int = 0,
        val patch: Int = 0,
        val build: Int = 0,
        val buildType: String? = null
) {
    init {
        check(major >= 0)
        check(minor in 0 until MAX_MINOR_COUNT)
        check(patch in 0 until MAX_PATCH_COUNT)
        check(build in 0 until MAX_BUILD_COUNT)
    }

    val code = ((major * MAX_MINOR_COUNT +
            minor) * MAX_PATCH_COUNT +
            patch) * MAX_BUILD_COUNT +
            build

    val name = toString(
            buildVisibility = Visibility.NON_ZERO
    )

    val shortName = toString(
            patchVisibility = Visibility.NON_ZERO,
            buildVisibility = Visibility.NON_ZERO
    )

    val fullName = toString(
            minorVisibility = Visibility.ALWAYS,
            patchVisibility = Visibility.ALWAYS,
            buildVisibility = Visibility.ALWAYS
    )

    fun toString(
            minorDelimiter: Char = '.',
            minorVisibility: Visibility = Visibility.ALWAYS,
            patchDelimiter: Char = '.',
            patchVisibility: Visibility = Visibility.ALWAYS,
            buildDelimiter: Char = '-',
            buildVisibility: Visibility = Visibility.NEVER,
            buildMinDigits: Int = 1
    ): String {
        val showMinor = minorVisibility == Visibility.ALWAYS ||
                (minorVisibility == Visibility.NON_ZERO && minor > 0)
        val showPatch = patchVisibility == Visibility.ALWAYS ||
                (patchVisibility == Visibility.NON_ZERO && patch > 0)
        val showBuild = buildVisibility == Visibility.ALWAYS ||
                (buildVisibility == Visibility.NON_ZERO && build > 0)

        return StringBuilder()
                .apply {
                    append(major)
                    if (showMinor) {
                        append(minorDelimiter)
                        append(minor)
                        if (showPatch) {
                            append(patchDelimiter)
                            append(patch)
                            if (showBuild) {
                                append(buildDelimiter)
                                if (buildType != null) append(buildType)
                                append(build.toString(buildMinDigits))
                            }
                        }
                    }
                }
                .toString()
    }

    private fun Int.toString(minDigits: Int = 1): String {
        return toString()
                .let { string ->
                    val missingDigits = (minDigits - string.length)
                            .coerceAtLeast(0)
                    "0".repeat(missingDigits) + string
                }
    }

    override fun toString() = name

    enum class Visibility {
        ALWAYS,
        NON_ZERO,
        NEVER
    }

    private companion object {
        /**
         * Max [minor] versions before the [major] version must be incremented.
         *
         * This limit is needed to make the [code] unambiguous
         * and is checked when constructing a [Version].
         */
        private const val MAX_MINOR_COUNT = 10
        /**
         * Max [patch] versions before the [minor] version must be incremented.
         *
         * This limit is needed to make the [code] unambiguous
         * and is checked when constructing a [Version].
         */
        private const val MAX_PATCH_COUNT = 10
        /**
         * Max [build] versions before the [patch] version must be incremented.
         *
         * This limit is needed to make the [code] unambiguous
         * and is checked when constructing a [Version].
         */
        private const val MAX_BUILD_COUNT = 100
    }
}
