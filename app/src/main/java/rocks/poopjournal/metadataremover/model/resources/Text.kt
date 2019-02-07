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

package rocks.poopjournal.metadataremover.model.resources

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import rocks.poopjournal.metadataremover.model.resources.Text.Type.*
import java.util.*

/**
 * An umbrella container for several text representations, including [CharSequence]s,
 * [CharArray]s, and [string resources][StringRes].
 *
 * This class is a simplified copy of [android.graphics.drawable.Icon] which is released under
 * the Apache License 2.0, but customized for texts.
 */
class Text private constructor(
        /**
         * This text's type as in [Text.Type].
         */
        private val type: Type,

        /**
         * [CharSequence], [CharArray] or [Text] prefix, depending on the [type].
         */
        private val object1: Any? = null,

        /**
         * [Text] suffix, depending on the [type].
         */
        private val object2: Any? = null,

        /**
         * [StringRes] ID or the [CharArray]'s start offset, depending on the [type].
         */
        private val int1: Int? = null,

        /**
         * The [CharArray]'s length, depending on the [type].
         */
        private val int2: Int? = null,

        /**
         * The resource's format arguments.
         */
        private val formatArguments: Array<out Any> = emptyArray()
) {
    /**
     * Text source type.
     */
    enum class Type {
        SEQUENCE,
        RESOURCE,
        CHARACTERS,
        GLUE
    }

    /**
     * Throws an exception if this text's type is not [allowed][allowedTypes].
     */
    private fun checkType(vararg allowedTypes: Type) {
        if (type !in allowedTypes) {
            throw IllegalStateException("The text type was expected to be " +
                    "one of ${allowedTypes.joinToString()} but was $type instead.")
        }
    }

    /**
     * The text's [CharSequence] source.
     *
     * Note: This property is only available to [SEQUENCE] images.
     */
    private val sequence: CharSequence
        get() {
            checkType(SEQUENCE)
            check(object1 is CharSequence)
            return object1
        }

    /**
     * The text's [StringRes] ID source.
     *
     * Note: This property is only available to [RESOURCE] images.
     */
    private val resId: Int
        @StringRes get() {
            checkType(RESOURCE)
            check(int1 is Int)
            return int1
        }

    /**
     * The text's [CharArray] source.
     *
     * Note: This property is only available to [CHARACTERS] images.
     */
    private val characters: CharArray
        get() {
            checkType(CHARACTERS)
            check(object1 is CharArray)
            return object1
        }
    /**
     * The [characters]'s start offset.
     *
     * Note: This property is only available to [CHARACTERS] images.
     */
    private val charactersStartOffset: Int
        @IntRange(from = 0) get() {
            checkType(CHARACTERS)
            check(int1 is Int)
            return int1
        }
    /**
     * The [characters]'s length.
     *
     * Note: This property is only available to [CHARACTERS] images.
     */
    private val charactersLength: Int
        @IntRange(from = 0) get() {
            checkType(CHARACTERS)
            check(int2 is Int)
            return int2
        }
    /**
     * The prefix [Text] which is glued together with the [suffix].
     *
     * Note: This property is only available to [GLUE] images.
     */
    private val prefix: Text
        get() {
            checkType(GLUE)
            check(object1 is Text)
            return object1
        }
    /**
     * The suffix [Text] which is glued together with the [prefix].
     *
     * Note: This property is only available to [GLUE] images.
     */
    private val suffix: Text
        get() {
            checkType(GLUE)
            check(object2 is Text)
            return object2
        }

    /**
     * Create a [Text] from a [CharSequence].
     *
     * @param characters Characters to create the text from.
     * @param formatArguments Arguments referenced by the format specifiers in the format string.
     *
     * @see String.format
     */
    constructor(
            characters: CharSequence,
            vararg formatArguments: Any
    ) : this(
            type = Type.SEQUENCE,
            object1 = characters,
            formatArguments = formatArguments
    )

    /**
     * Create a [Text] pointing to a string resource.
     *
     * @param resId ID of the string resource.
     * @param formatArguments Arguments referenced by the format specifiers in the format string.
     *
     * @see String.format
     */
    constructor(
            @StringRes resId: Int,
            vararg formatArguments: Any
    ) : this(
            type = Type.RESOURCE,
            int1 = resId,
            formatArguments = formatArguments
    )

    /**
     * Create a [Text] pointing to a character array.
     *
     * @param characters Character array storing the text.
     * @param startOffset Offset into [characters] at which the text starts or `null` if
     * no offset is specified aka. the text starts at the first position.
     * @param length Length of the text or `null` if no length is specified aka. the text
     * continues to the end of the array.
     * @param formatArguments Arguments referenced by the format specifiers in the format string.
     *
     * @see String.format
     */
    constructor(
            characters: CharArray,
            @IntRange(from = 0) startOffset: Int = 0,
            @IntRange(from = 0) length: Int = characters.size - startOffset,
            vararg formatArguments: Any
    ) : this(
            type = Type.CHARACTERS,
            object1 = characters,
            int1 = startOffset,
            int2 = length,
            formatArguments = formatArguments
    )

    /**
     * Create a [Text] with the [suffix] appended to the [prefix].
     */
    private constructor(
            prefix: Text,
            suffix: Text
    ) : this(
            type = Type.GLUE,
            object1 = prefix,
            object2 = suffix
    )

    /**
     * Load a [CharSequence] containing this text.
     *
     * Note: If format argument were defined,
     * the resulting sequence will be transformed to a [String].
     */
    fun load(context: Context): CharSequence {
        val sequence = when (type) {
            SEQUENCE -> {
                sequence
            }
            RESOURCE -> {
                context.resources.getText(resId)
            }
            CHARACTERS -> {
                String(characters, charactersStartOffset, charactersLength)
            }
            GLUE -> {
                SpannableStringBuilder(prefix.load(context))
                        .append(suffix.load(context))
            }
        }

        if (formatArguments.isNotEmpty() && type != GLUE) {
            // Resolve other Text objects before passing them to String.format().
            val resolvedArguments = formatArguments
                    .map { argument ->
                        if (argument is Text) {
                            argument.load(context)
                        } else argument
                    }
                    .toTypedArray()
            return String.format(
                    Locale.getDefault(),
                    sequence.toString(),
                    *resolvedArguments
            )
        }

        return sequence
    }

    /**
     * @see load
     */
    operator fun invoke(context: Context) = load(context)

    /**
     * Join two texts.
     */
    fun join(text: Text) = Text(this, text)

    /**
     * @see join
     */
    operator fun plus(text: Text) = join(text)

    /**
     * Join this text with a [CharSequence].
     */
    fun join(characters: CharSequence) = Text(this, characters.toText())

    /**
     * @see join
     */
    operator fun plus(characters: CharSequence) = join(characters)

    /**
     * Join this text with a [StringRes].
     */
    fun join(@StringRes resId: Int) = Text(this, resId.toText())

    /**
     * @see join
     */
    operator fun plus(@StringRes resId: Int) = join(resId)

    /**
     * Join this text with a [CharArray].
     */
    fun join(characters: CharArray) = Text(this, characters.toText())

    /**
     * @see join
     */
    operator fun plus(characters: CharArray) = join(characters)

    companion object {
        fun CharSequence.toText() = Text(this)
        fun @receiver:StringRes Int.toText() = Text(this)
        fun CharArray.toText() = Text(this)
    }
}