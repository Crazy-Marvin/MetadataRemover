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

import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.*
import kotlin.text.Charsets.UTF_8


/**
 * The following constants are grouped by their type and ordered alphabetically by the constant
 * name within that type. The constant name should be a sensible identifier that is closest to the
 * "common name" of the media. This is often, but not necessarily the same as the subtype.
 *
 * Be sure to declare all constants with the type and subtype in all lowercase. For types that
 * take a charset (e.g. all text types), default to UTF-8 and suffix the constant name with
 * `_UTF_8`.
 */
@Suppress("SpellCheckingInspection", "UNUSED")
object MediaTypes {

    private val ALL = mutableMapOf<MediaType, Set<MediaType>>()
    private fun add(primary: MediaType, vararg secondary: MediaType) {
        ALL[primary] = get(primary) + secondary
    }

    operator fun get(type: MediaType): Set<MediaType> = ALL.getOrElse(type) { setOf(type) }

    private operator fun MediaType.plus(type: MediaType): MediaType {
        add(this, type)
        return this
    }

    private operator fun MediaType.plus(types: Collection<MediaType>): MediaType {
        add(this, *types.toTypedArray())
        return this
    }

    fun wildcardOf(): MediaType = wildcardOf(MediaType.WILDCARD)

    fun wildcardOf(type: String): MediaType = MediaType(type, MediaType.WILDCARD)

    fun wildcardOf(knownType: MediaType.KnownType): MediaType = wildcardOf(knownType.type)

    /* Wildcard types: */

    val ANY = wildcardOf()

    val ANY_TEXT = wildcardOf(TEXT)

    val ANY_IMAGE = wildcardOf(IMAGE)

    val ANY_AUDIO = wildcardOf(AUDIO)

    val ANY_VIDEO = wildcardOf(VIDEO)

    val ANY_APPLICATION = wildcardOf(APPLICATION)


    /* Text types: */

    val CACHE_MANIFEST_UTF_8 = MediaType(TEXT, "cache-manifest", charset = UTF_8)

    val CSS_UTF_8 = MediaType(TEXT, "css", charset = UTF_8)

    val CSV_UTF_8 = MediaType(TEXT, "csv", charset = UTF_8)

    val HTML_UTF_8 = MediaType(TEXT, "html", charset = UTF_8)

    val I_CALENDAR_UTF_8 = MediaType(TEXT, "calendar", charset = UTF_8)

    val PLAIN_TEXT_UTF_8 = MediaType(TEXT, "plain", charset = UTF_8)

    /**
     * [RFC 4329](http://www.rfc-editor.org/rfc/rfc4329.txt) declares [JAVASCRIPT_UTF_8]
     * to be the correct media type for JavaScript, but this
     * may be necessary in certain situations for compatibility.
     */
    val TEXT_JAVASCRIPT_UTF_8 = MediaType(TEXT, "javascript", charset = UTF_8)

    /**
     * [Tab separated values](http://www.iana.org/assignments/media-types/text/tab-separated-values).
     */
    val TSV_UTF_8 = MediaType(TEXT, "tab-separated-values", charset = UTF_8)

    val V_CARD_UTF_8 = MediaType(TEXT, "vcard", charset = UTF_8)

    val WML_UTF_8 = MediaType(TEXT, "vnd.wap.wml", charset = UTF_8)

    /**
     * As described in [RFC 3023](http://www.ietf.org/rfc/rfc3023.txt), this type is used
     * for XML documents that are "readable by casual users."
     * [APPLICATION_XML_UTF_8] is provided for documents that are intended for applications.
     */
    val XML_UTF_8 = MediaType(TEXT, "xml", charset = UTF_8)

    /**
     * As described in [the VTT spec](https://w3c.github.io/webvtt/#iana-text-vtt), this is
     * used for Web Video Text Tracks (WebVTT) files, used with the HTML5 track element.
     */
    val VTT_UTF_8 = MediaType(TEXT, "vtt", charset = UTF_8)


    /* Image types: */

    val ARW = MediaType(IMAGE, "arw") +
            MediaType(IMAGE, "x-sony-arw")

    val BMP = MediaType(IMAGE, "bmp") +
            MediaType(IMAGE, "x-bmp") +
            MediaType(IMAGE, "x-bitmap") +
            MediaType(IMAGE, "x-xbitmap") +
            MediaType(IMAGE, "x-win-bitmap") +
            MediaType(IMAGE, "x-windows-bmp") +
            MediaType(IMAGE, "ms-bmp") +
            MediaType(IMAGE, "x-ms-bmp") +
            MediaType(APPLICATION, "bmp") +
            MediaType(APPLICATION, "x-bmp") +
            MediaType(APPLICATION, "x-win-bitmap")

    val CR2 = MediaType(IMAGE, "cr2") +
            MediaType(IMAGE, "x-canon-cr2") +
            MediaType(IMAGE, "x-dcraw")

    /**
     * The media type for the [Canon Image File Format](http://en.wikipedia.org/wiki/Camera_Image_File_Format) (`crw` files),
     * a widely-used "raw image" format for cameras. It is found in `/etc/mime.types`,
     * e.g. in [Debian 3.48-1](http://anonscm.debian.org/gitweb/?p=collab-maint/mime-support.git;a=blob;f=mime.types;hb=HEAD).
     */
    val CRW = MediaType(IMAGE, "x-canon-crw")

    val DCX = MediaType(IMAGE, "dcx") +
            MediaType(IMAGE, "x-dcx")

    val DNG = MediaType(IMAGE, "dng") +
            MediaType(IMAGE, "x-adobe-dng")

    val GIF = MediaType(IMAGE, "gif")

    val ICO = MediaType(IMAGE, "vnd.microsoft.icon") +
            MediaType(IMAGE, "ico") +
            MediaType(IMAGE, "x-icon") +
            MediaType(APPLICATION, "ico") +
            MediaType(APPLICATION, "x-icon")

    val JPEG = MediaType(IMAGE, "jpeg") +
            MediaType(IMAGE, "jpg") +
            MediaType(APPLICATION, "jpg") +
            MediaType(APPLICATION, "x-jpg") +
            MediaType(IMAGE, "pjpeg") +
            MediaType(IMAGE, "pipeg") +
            MediaType(IMAGE, "vnd.swiftview-jpeg")

    val NEF = MediaType(IMAGE, "nef") +
            MediaType(IMAGE, "x-nikon-nef")

    val NRW = MediaType(IMAGE, "nrw") +
            MediaType(IMAGE, "x-nikon-nrw") +
            get(NEF)

    val ORF = MediaType(IMAGE, "orf") +
            MediaType(IMAGE, "x-olympus-orf")

    val PCX = MediaType(IMAGE, "pcx") +
            MediaType(IMAGE, "x-pcx") +
            MediaType(IMAGE, "x-pc-paintbrush") +
            MediaType(IMAGE, "vnd.swiftview-pcx") +
            MediaType(APPLICATION, "pcx") +
            MediaType(APPLICATION, "x-pcx") +
            MediaType("zz-application", "zz-winassoc-pcx")

    val PEF = MediaType(IMAGE, "pef") +
            MediaType(IMAGE, "x-pentax-pef")

    val PNG = MediaType(IMAGE, "png") +
            MediaType(APPLICATION, "png") +
            MediaType(APPLICATION, "x-png")

    val PNM = MediaType(IMAGE, "x-portable-anymap") +
            MediaType(IMAGE, "pbm")

    /**
     * The media type for the Photoshop File Format (`psd` files) as defined
     * by [IANA](http://www.iana.org/assignments/media-types/image/vnd.adobe.photoshop), and
     * found in [`/etc/mime.types`](http://svn.apache.org/repos/asf/httpd/httpd/branches/1.3.x/conf/mime.types)
     * of the Apache [HTTPD project](http://httpd.apache.org/); for the specification,
     * see [Adobe Photoshop Document Format](http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/PhotoshopFileFormats.htm)
     * and [Wikipedia](http://en.wikipedia.org/wiki/Adobe_Photoshop#File_format); this is the
     * regular output/input of Photoshop (which can also export to various image formats;
     * note that files with extension "PSB" are in a distinct but related format).
     *
     * This is a more recent replacement for the older, experimental type
     * `x-photoshop`: [RFC-2046.6](http://tools.ietf.org/html/rfc2046#section-6).
     */
    val PSD = MediaType(IMAGE, "vnd.adobe.photoshop") +
            MediaType(IMAGE, "photoshop") +
            MediaType(IMAGE, "x-photoshop") +
            MediaType(IMAGE, "psd") +
            MediaType(APPLICATION, "photoshop") +
            MediaType(APPLICATION, "psd") +
            MediaType("zz-application", "zz-winassoc-psd")

    val RAF = MediaType(IMAGE, "raf") +
            MediaType(IMAGE, "x-fuji-raf")

    val RW2 = MediaType(IMAGE, "rw2") +
            MediaType(IMAGE, "x-panasonic-rw2") +
            MediaType(IMAGE, "x-panasonic-raw")

    val SRW = MediaType(IMAGE, "srw") +
            MediaType(IMAGE, "x-samsung-srw") +
            MediaType(APPLICATION, "octet-stream")

    val SVG_UTF_8 = MediaType(IMAGE, "svg+xml", charset = UTF_8)

    val TIFF = MediaType(IMAGE, "tiff")

    val WBMP = MediaType(IMAGE, "vnd.wap.wbmp")

    val WEBP = MediaType(IMAGE, "webp")

    val XBM = MediaType(IMAGE, "x-xbitmap")

    val XPM = MediaType(IMAGE, "x-xpixmap") +
            MediaType(IMAGE, "x-xbitmap") +
            MediaType(IMAGE, "xpm") +
            MediaType(IMAGE, "x-xpm")


    /* Audio types: */

    val MP4_AUDIO = MediaType(AUDIO, "mp4")

    val MPEG_AUDIO = MediaType(AUDIO, "mpeg")

    val OGG_AUDIO = MediaType(AUDIO, "ogg")

    val WEBM_AUDIO = MediaType(AUDIO, "webm")

    /**
     * Media type for L16 audio, as defined by [RFC 2586](https://tools.ietf.org/html/rfc2586).
     */
    val L16_AUDIO = MediaType(AUDIO, "l16")

    /**
     * Media type for L24 audio, as defined by [RFC 3190](https://tools.ietf.org/html/rfc3190).
     */
    val L24_AUDIO = MediaType(AUDIO, "l24")

    /**
     * Media type for Basic Audio, as defined by [RFC 2046](http://tools.ietf.org/html/rfc2046#section-4.3).
     */
    val BASIC_AUDIO = MediaType(AUDIO, "basic")

    /**
     * Media type for Advanced Audio Coding. For more information,
     * see [Advanced Audio Coding](https://en.wikipedia.org/wiki/Advanced_Audio_Coding).
     */
    val AAC_AUDIO = MediaType(AUDIO, "aac")

    /**
     * Media type for Vorbis Audio, as defined by [RFC 5215](http://tools.ietf.org/html/rfc5215).
     */
    val VORBIS_AUDIO = MediaType(AUDIO, "vorbis")

    /**
     * Media type for Windows Media Audio. For more information,
     * see [file name extensions for Windows Media metafiles](https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx).
     */
    val WMA_AUDIO = MediaType(AUDIO, "x-ms-wma")

    /**
     * Media type for Windows Media metafiles. For more information,
     * see [file name extensions for Windows Media metafiles](https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx).
     */
    val WAX_AUDIO = MediaType(AUDIO, "x-ms-wax")

    val WAV_AUDIO = MediaType(AUDIO, "x-wav")

    /**
     * Media type for Real Audio. For more information,
     * see [this link](http://service.real.com/help/faq/rp8/configrp8win.html).
     */
    val VND_REAL_AUDIO = MediaType(AUDIO, "vnd.rn-realaudio")

    /**
     * Media type for WAVE format, as defined by [RFC 2361](https://tools.ietf.org/html/rfc2361).
     */
    val VND_WAVE_AUDIO = MediaType(AUDIO, "vnd.wave")


    /* Video types: */

    val AVI_VIDEO = MediaType(VIDEO, "x-msvideo")

    val MP4_VIDEO = MediaType(VIDEO, "mp4")

    val MPEG_VIDEO = MediaType(VIDEO, "mpeg")

    val OGG_VIDEO = MediaType(VIDEO, "ogg")

    val QUICKTIME = MediaType(VIDEO, "quicktime")

    val WEBM_VIDEO = MediaType(VIDEO, "webm")

    val WMV = MediaType(VIDEO, "x-ms-wmv")

    /**
     * Media type for Flash video. For more information,
     * see [this link](http://help.adobe.com/en_US/ActionScript/3.0_ProgrammingAS3/WS5b3ccc516d4fbf351e63e3d118a9b90204-7d48.html).
     */
    val FLV_VIDEO = MediaType(VIDEO, "x-flv")

    /**
     * Media type for the 3GP multimedia container format. For more information,
     * see [3GPP TS 26.244](ftp://www.3gpp.org/tsg_sa/TSG_SA/TSGS_23/Docs/PDF/SP-040065.pdf#page=10).
     */
    val THREE_GPP_VIDEO = MediaType(VIDEO, "3gpp")

    /**
     * Media type for the 3G2 multimedia container format. For more information,
     * see [3GPP2 C.S0050-B](http://www.3gpp2.org/Public_html/specs/C.S0050-B_v1.0_070521.pdf#page=16).
     */
    val THREE_GPP2_VIDEO = MediaType(VIDEO, "3gpp2")


    /* Application types: */

    /**
     * As described in [RFC 3023](http://www.ietf.org/rfc/rfc3023.txt), this type is used
     * for XML documents that are "unreadable by casual users."
     * [XML_UTF_8] is provided for documents that may be read by users.
     */
    val APPLICATION_XML_UTF_8 = MediaType(APPLICATION, "xml", charset = UTF_8)

    val ATOM_UTF_8 = MediaType(APPLICATION, "atom+xml", charset = UTF_8)

    val BZIP2 = MediaType(APPLICATION, "x-bzip2")

    /**
     * Media type for [dart files](https://www.dartlang.org/articles/embedding-in-html/).
     */
    val DART_UTF_8 = MediaType(APPLICATION, "dart", charset = UTF_8)

    /**
     * Media type for [Apple Passbook](https://goo.gl/2QoMvg).
     */
    val APPLE_PASSBOOK = MediaType(APPLICATION, "vnd.apple.pkpass")

    /**
     * Media type for [Embedded OpenType](http://en.wikipedia.org/wiki/Embedded_OpenType)
     * fonts. This is [registered](http://www.iana.org/assignments/media-types/application/vnd.ms-fontobject)
     * with the IANA.
     */
    val EOT = MediaType(APPLICATION, "vnd.ms-fontobject")

    /**
     * As described in the [International Digital Publishing Forum](http://idpf.org/epub)
     * EPUB is the distribution and interchange format standard for digital publications and
     * documents. This media type is defined in the [EPUB Open Container Format](http://www.idpf.org/epub/30/spec/epub30-ocf.html)
     * specification.
     */
    val EPUB = MediaType(APPLICATION, "epub+zip")

    val FORM_DATA = MediaType(APPLICATION, "x-www-form-urlencoded")

    /**
     * As described in [PKCS #12: Personal Information Exchange Syntax Standard](https://www.rsa.com/rsalabs/node.asp?id=2138),
     * PKCS #12 defines an archive file format for storing
     * many cryptography objects as a single file.
     */
    val KEY_ARCHIVE = MediaType(APPLICATION, "pkcs12")

    /**
     * This is a non-standard media type, but is commonly used in serving hosted binary files
     * as it is [known not to trigger content sniffing in current browsers](http://code.google.com/p/browsersec/wiki/Part2#Survey_of_content_sniffing_behaviors).
     * It *should not* be used in other situations as it is not specified by any RFC
     * and does not appear in the [/IANA MIME Media Types](http://www.iana.org/assignments/media-types) list.
     * Consider [OCTET_STREAM] for binary data that is not being served to a browser.
     */
    val APPLICATION_BINARY = MediaType(APPLICATION, "binary")

    val GZIP = MediaType(APPLICATION, "x-gzip")

    /**
     * Media type for the [JSON Hypertext Application Language (HAL) documents](https://tools.ietf.org/html/draft-kelly-json-hal-08#section-3).
     */
    val HAL_JSON = MediaType(APPLICATION, "hal+json")

    val ICC = MediaType(APPLICATION, "vnd.iccprofile")

    /**
     * [RFC 4329](http://www.rfc-editor.org/rfc/rfc4329.txt) declares this to be the
     * correct media type for JavaScript, but [text/javascript][TEXT_JAVASCRIPT_UTF_8] may be
     * necessary in certain situations for compatibility.
     */
    val JAVASCRIPT_UTF_8 = MediaType(APPLICATION, "javascript", charset = UTF_8)

    val JSON_UTF_8 = MediaType(APPLICATION, "json", charset = UTF_8)

    /**
     * Media type for the [Manifest for a web application](http://www.w3.org/TR/appmanifest/).
     */
    val MANIFEST_JSON_UTF_8 = MediaType(APPLICATION, "manifest+json", charset = UTF_8)

    /**
     * Media type for [OGC KML (Keyhole Markup Language)](http://www.opengeospatial.org/standards/kml/).
     */
    val KML = MediaType(APPLICATION, "vnd.google-earth.kml+xml")

    /**
     * Media type for [OGC KML (Keyhole Markup Language)](http://www.opengeospatial.org/standards/kml/),
     * compressed using the ZIP format into KMZ archives.
     */
    val KMZ = MediaType(APPLICATION, "vnd.google-earth.kmz")

    /**
     * Media type for the [mbox database format](https://tools.ietf.org/html/rfc4155).
     */
    val MBOX = MediaType(APPLICATION, "mbox")

    /**
     * Media type for [Apple over-the-air mobile configuration profiles](http://goo.gl/1pGBFm).
     */
    val APPLE_MOBILE_CONFIG = MediaType(APPLICATION, "x-apple-aspen-config")

    val MICROSOFT_EXCEL = MediaType(APPLICATION, "vnd.ms-excel")

    val MICROSOFT_POWERPOINT = MediaType(APPLICATION, "vnd.ms-powerpoint")

    val MICROSOFT_WORD = MediaType(APPLICATION, "msword")

    /**
     * Media type for WASM applications. For more information see [the Web Assembly overview](https://webassembly.org/).
     */
    val WASM_APPLICATION = MediaType(APPLICATION, "wasm")

    /**
     * Media type for NaCl applications. For more information see [the Developer Guide for Native Client Application Structure](https://developer.chrome.com/native-client/devguide/coding/application-structure).
     */
    val NACL_APPLICATION = MediaType(APPLICATION, "x-nacl")

    /**
     * Media type for NaCl portable applications. For more information
     * see [the Developer Guide for Native Client Application Structure](https://developer.chrome.com/native-client/devguide/coding/application-structure).
     */
    val NACL_PORTABLE_APPLICATION = MediaType(APPLICATION, "x-pnacl")

    val OCTET_STREAM = MediaType(APPLICATION, "octet-stream")

    val OGG_CONTAINER = MediaType(APPLICATION, "ogg")

    val OOXML_DOCUMENT = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.wordprocessingml.document"
    )

    val OOXML_PRESENTATION = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.presentationml.presentation"
    )

    val OOXML_SHEET = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

    val OPENDOCUMENT_GRAPHICS = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.graphics"
    )

    val OPENDOCUMENT_PRESENTATION = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.presentation"
    )

    val OPENDOCUMENT_SPREADSHEET = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.spreadsheet"
    )

    val OPENDOCUMENT_TEXT = MediaType(APPLICATION, "vnd.oasis.opendocument.text")

    val PDF = MediaType(APPLICATION, "pdf")

    val POSTSCRIPT = MediaType(APPLICATION, "postscript")

    /**
     * [Protocol buffers](http://tools.ietf.org/html/draft-rfernando-protocol-buffers-00)
     */
    val PROTOBUF = MediaType(APPLICATION, "protobuf")

    val RDF_XML_UTF_8 = MediaType(APPLICATION, "rdf+xml", charset = UTF_8)

    val RTF_UTF_8 = MediaType(APPLICATION, "rtf", charset = UTF_8)

    /**
     * Media type for SFNT fonts (which includes [TrueType](http://en.wikipedia.org/wiki/TrueType/)
     * and [OpenType](http://en.wikipedia.org/wiki/OpenType/) fonts).
     * This is [registered](http://www.iana.org/assignments/media-types/application/font-sfnt)
     * with the IANA.
     */
    val SFNT = MediaType(APPLICATION, "font-sfnt")

    val SHOCKWAVE_FLASH = MediaType(APPLICATION, "x-shockwave-flash")

    val SKETCHUP = MediaType(APPLICATION, "vnd.sketchup.skp")

    /**
     * As described in [RFC 3902](http://www.ietf.org/rfc/rfc3902.txt), this constant
     * (`application/soap+xml`) is used to identify SOAP 1.2 message envelopes that have been
     * serialized with XML 1.0.
     *
     * For SOAP 1.1 messages, see `XML_UTF_8`
     * per [W3C Note on Simple Object Access Protocol (SOAP) 1.1](http://www.w3.org/TR/2000/NOTE-SOAP-20000508/)
     */
    val SOAP_XML_UTF_8 = MediaType(APPLICATION, "soap+xml", charset = UTF_8)

    val TAR = MediaType(APPLICATION, "x-tar")

    /**
     * Media type for the [Web Open Font Format](http://en.wikipedia.org/wiki/Web_Open_Font_Format) (WOFF)
     * [defined](http://www.w3.org/TR/WOFF/) by the W3C.
     * This is [registered](http://www.iana.org/assignments/media-types/application/font-woff) with
     * the IANA.
     */
    val WOFF = MediaType(APPLICATION, "font-woff")

    /**
     * Media type for the [Web Open Font Format](http://en.wikipedia.org/wiki/Web_Open_Font_Format) (WOFF)
     * version 2 [defined](https://www.w3.org/TR/WOFF2/) by the W3C.
     */
    val WOFF2 = MediaType(APPLICATION, "font-woff2")

    val XHTML_UTF_8 = MediaType(APPLICATION, "xhtml+xml", charset = UTF_8)

    /**
     * Media type for Extensible Resource Descriptors. This is not yet registered
     * with the IANA, but it is specified by OASIS
     * in the [XRD definition](http://docs.oasis-open.org/xri/xrd/v1.0/cd02/xrd-1.0-cd02.html)
     * and implemented in projects such as [WebFinger](http://code.google.com/p/webfinger/).
     */
    val XRD_UTF_8 = MediaType(APPLICATION, "xrd+xml", charset = UTF_8)

    val ZIP = MediaType(APPLICATION, "zip")
}
