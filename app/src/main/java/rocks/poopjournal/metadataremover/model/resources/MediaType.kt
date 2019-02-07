/*
 * Copyright (C) 2011 The Guava Authors, 2018 Jan Heinrich Reimer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package rocks.poopjournal.metadataremover.model.resources

import rocks.poopjournal.metadataremover.util.Logger
import java.nio.charset.Charset

/**
 * Represents an [Internet Media Type](http://en.wikipedia.org/wiki/Internet_media_type)
 * (also known as a MIME Type or Content Type). This class also supports the concept of media ranges
 * [defined by HTTP/1.1](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1).
 * As such, the `*` character is treated as a wildcard and is used to represent any acceptable
 * type or subtype value. A media type may not have wildcard type with a declared subtype. The
 * `*` character has no special meaning as part of a parameter. All values for type, subtype,
 * parameter attributes or parameter values must be valid according
 * to RFCs [2045](http://www.ietf.org/rfc/rfc2045.txt)
 * and [2046](http://www.ietf.org/rfc/rfc2046.txt).
 *
 *
 * All portions of the media type that are case-insensitive (type, subtype, parameter attributes)
 * are normalized to lowercase. The value of the `charset` parameter is normalized to
 * lowercase, but all others are left as-is.
 *
 *
 * Note that this specifically does **not** represent the value of the MIME `Content-Type` header
 * and as such has no support for header-specific considerations such as line
 * folding and comments.
 *
 *
 * For media types that take a charset the predefined constants default to UTF-8 and have a
 * "_UTF_8" suffix. To get a version without a character set, use [.withoutParameters].
 *
 * This file is a modified copy of Guava's
 * [MediaType](https://github.com/google/guava/blob/master/guava/src/com/google/common/net/MediaType.java)
 * which is released under the Apache License v.2.0.
 */
class MediaType(
        /** The top-level media type. For example, "text" in "text/plain". */
        type: String,
        /** The media subtype. For example, "plain" in "text/plain". */
        subtype: String,
        /** A map containing the parameters of this media type. */
        parameters: Map<String, String> = emptyMap(),
        charset: Charset? = null
) {

    init {
        check(WILDCARD != type || WILDCARD == subtype) {
            "A wildcard type cannot be used with a non-wildcard subtype"
        }
    }

    /** The top-level media type. For example, "text" in "text/plain". */
    val type: String = type.normalizeToken().apply { checkType() }

    /** The media subtype. For example, "plain" in "text/plain". */
    val subtype: String = subtype.normalizeToken().apply { checkType() }

    /** A map containing the parameters of this media type. */
    val parameters: Map<String, String> = parameters
            .run {
                // Add the charset to the parameter list.
                if (charset != null) {
                    this + (CHARSET_ATTRIBUTE to charset.name().toLowerCase())
                } else this
            }
            .normalizeParameters()
            .apply { checkParameters() }

    private val charsetAttribute = parameters[CHARSET_ATTRIBUTE]
    val charset = charsetAttribute
            ?.let(Charset::forName)

    private val hasWildcardType = WILDCARD == type
    private val hasWildcardSubtype = WILDCARD == subtype
    /** Returns `true` if either the type or subtype is the wildcard.  */
    val hasWildcard = hasWildcardType || hasWildcardSubtype

    constructor(
            /** The top-level media type. For example, "text" in "text/plain". */
            knownType: KnownType,
            /** The media subtype. For example, "plain" in "text/plain". */
            subtype: String,
            /** A map containing the parameters of this media type. */
            parameters: Map<String, String> = emptyMap(),
            charset: Charset? = null
    ) : this(
            knownType.type,
            subtype,
            parameters,
            charset
    )

    /**
     * Returns `true` if the [element] falls within this types range (as defined by
     * [the HTTP Accept header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html)) given
     * by the argument according to three criteria:
     *
     *  1. The type of the argument is the wildcard or equal to the type of this instance.
     *  2. The subtype of the argument is the wildcard or equal to the subtype of this instance.
     *  3. All of the parameters present in the argument are present in this instance.
     *
     * For example:
     *
     * ```kotlin
     * PLAIN_TEXT_UTF_8 in PLAIN_TEXT_UTF_8 // true
     * HTML_UTF_8 in PLAIN_TEXT_UTF_8 // false
     * ANY in PLAIN_TEXT_UTF_8 // false
     * ANY_TEXT in PLAIN_TEXT_UTF_8 // false
     * ANY_IMAGE in PLAIN_TEXT_UTF_8 // false
     * ```
     */
    operator fun contains(element: MediaType) =
            ((hasWildcardType || type == element.type)
                    && (hasWildcardSubtype || subtype == element.subtype)
                    && element.parameters.entries.containsAll(parameters.entries))

    /**
     * Joins two media types and generates wildcards if necessary.
     *
     * Note: Only parameters in both media types will be kept.
     */
    operator fun times(other: MediaType): MediaType {
        val newParameters = parameters.filter { (key, value) ->
            other.parameters[key] == value
        }
        return if (type == other.type) {
            if (subtype == other.subtype) {
                MediaType(type, subtype, newParameters)
            } else MediaType(type, WILDCARD, newParameters)
        } else MediaType(WILDCARD, WILDCARD, newParameters)
    }

    /**
     * Returns the string representation of this media type in the format described in [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt).
     */
    override fun toString(): String {
        val builder = StringBuilder()
                .append(type)
                .append(SUBTYPE_DELIMITER)
                .append(subtype)
        parameters.entries
                .takeIf { it.isNotEmpty() }
                ?.joinTo(
                        builder,
                        prefix = "$PARAMETER_DELIMITER",
                        separator = "$PARAMETER_DELIMITER"
                ) { (key, value) ->
                    key + PARAMETER_VALUE_DELIMITER + value
                }
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaType) return false

        if (type != other.type) return false
        if (subtype != other.subtype) return false
        if (parameters != other.parameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + subtype.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    companion object {

        private const val WILDCARD_SYMBOL = '*'
        private const val SUBTYPE_DELIMITER = '/'
        private const val PARAMETER_DELIMITER = ';'
        private const val PARAMETER_VALUE_DELIMITER = '='

        const val WILDCARD = "$WILDCARD_SYMBOL"

        private const val CHARSET_ATTRIBUTE = "charset"

        /**
         * Parse a [MediaType] from its [String] representation.
         *
         * @throws IllegalStateException if the input is not parsable.
         */
        fun parse(input: String): MediaType? {
            Logger.w("Parsing mime type of '$input'.")
            val typeParameterStrings = input
                    .split(PARAMETER_DELIMITER, limit = 2)
                    .takeIf { it.isNotEmpty() }
                    ?: return null
            val typeString = typeParameterStrings[0]
            val parametersString = typeParameterStrings.getOrNull(1)
            Logger.w("typeString: $typeString")
            Logger.w("parametersString: $parametersString")
            val (type, subtype) = typeString
                    .split(SUBTYPE_DELIMITER, limit = 2)
                    .takeIf { it.size == 2 }
                    ?.map(String::trim)
                    ?: return null
            if (type.isEmpty() || subtype.isEmpty()) return null
            val parameters = parametersString
                    ?.split(PARAMETER_DELIMITER)
                    ?.filter { it.isNotBlank() }
                    ?.mapNotNull { parameterString ->
                        parameterString
                                .split(PARAMETER_VALUE_DELIMITER, limit = 2)
                                .takeIf { it.size == 2 }
                                ?.map(String::trim)
                                ?.takeUnless { (key, value) -> key.isEmpty() || value.isEmpty() }
                                ?.let { (key, value) -> key to value }
                    }
                    ?.takeIf { it.isNotEmpty() }
                    ?.toMap()
            if (parameters != null) return MediaType(type, subtype, parameters)
            return MediaType(type, subtype)
        }
    }

    private fun String.normalizeToken() = toLowerCase()

    private fun String.checkToken() {
        check(none {
            it == PARAMETER_DELIMITER || it == SUBTYPE_DELIMITER || it == PARAMETER_VALUE_DELIMITER
        })
    }

    private fun String.checkType() {
        checkToken()
        check(WILDCARD_SYMBOL !in this || this == WILDCARD)
    }

    private fun Map<String, String>.normalizeParameters() =
            map { (key, value) ->
                key.normalizeToken() to value.normalizeToken()
            }.toMap()

    private fun Map<String, String>.checkParameters() =
            forEach { (key, value) ->
                key.checkToken() to value.checkToken()
            }

    enum class KnownType(val type: String) {
        APPLICATION("application"),
        AUDIO("audio"),
        IMAGE("image"),
        TEXT("text"),
        VIDEO("video")


    }
}
