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

package rocks.poopjournal.metadataremover.model.metadata

import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text

/**
 * Wrapper around a file with metadata.
 */
data class Metadata(

        /**
         * A title different from the file name.
         * This could be the document title for files that aren't necessarily named after their
         * title.
         *
         * Note: This property should only be used for files that have a dedicated title,
         * files that don't have a title other than the filename should not override this property.
         */
        val title: Text? = null,

        /**
         * An image thumbnail of this file.
         */
        val thumbnail: Image,

        /**
         * The metadata stored in this file.
         */
        val attributes: Set<Attribute>

) {
    /**
     * A single metadata attribute (e.g. file creation date).
     */
    data class Attribute(
            /**
             * Label describing this attribute's category (e.g. "Date").
             */
            val label: Text,

            /**
             * Icon describing this attribute's category.
             */
            val icon: Image,

            /**
             * The attribute's primary value (e.g. "October 12, 2017").
             */
            val primaryValue: Text,

            /**
             * The attribute's optional secondary value (e.g. "11:21 AM").
             */
            val secondaryValue: Text? = null
    )
}