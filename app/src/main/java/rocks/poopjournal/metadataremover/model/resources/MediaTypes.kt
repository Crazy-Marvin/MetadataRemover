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

import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.APPLICATION
import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.AUDIO
import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.IMAGE
import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.TEXT
import rocks.poopjournal.metadataremover.model.resources.MediaType.KnownType.VIDEO
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

    fun getAlternatives(type: MediaType): Set<MediaType> = ALL.getOrElse(type) { setOf(type) }
    operator fun get(type: MediaType): Set<MediaType> = getAlternatives(type)

    @JvmField
    val ANY = MediaType(MediaType.WILDCARD, MediaType.WILDCARD)
    @JvmField
    val ANY_TEXT = MediaType(TEXT, MediaType.WILDCARD)
    @JvmField
    val ANY_IMAGE = MediaType(IMAGE, MediaType.WILDCARD)
    @JvmField
    val ANY_AUDIO = MediaType(AUDIO, MediaType.WILDCARD)
    @JvmField
    val ANY_VIDEO = MediaType(VIDEO, MediaType.WILDCARD)
    @JvmField
    val ANY_APPLICATION = MediaType(APPLICATION, MediaType.WILDCARD)


    /* Text types: */
    @JvmField
    val CACHE_MANIFEST_UTF_8 = MediaType(TEXT, "cache-manifest", charset = UTF_8)
    @JvmField
    val CSS_UTF_8 = MediaType(TEXT, "css", charset = UTF_8)
    @JvmField
    val CSV_UTF_8 = MediaType(TEXT, "csv", charset = UTF_8)
    @JvmField
    val HTML_UTF_8 = MediaType(TEXT, "html", charset = UTF_8)
    @JvmField
    val I_CALENDAR_UTF_8 = MediaType(TEXT, "calendar", charset = UTF_8)
    @JvmField
    val PLAIN_TEXT_UTF_8 = MediaType(TEXT, "plain", charset = UTF_8)
    /**
     * [RFC 4329](http://www.rfc-editor.org/rfc/rfc4329.txt) declares [JAVASCRIPT_UTF_8]
     * to be the correct media type for JavaScript, but this
     * may be necessary in certain situations for compatibility.
     */
    @JvmField
    val TEXT_JAVASCRIPT_UTF_8 = MediaType(TEXT, "javascript", charset = UTF_8)
    /**
     * [Tab separated values](http://www.iana.org/assignments/media-types/text/tab-separated-values).
     */
    @JvmField
    val TSV_UTF_8 = MediaType(TEXT, "tab-separated-values", charset = UTF_8)
    @JvmField
    val V_CARD_UTF_8 = MediaType(TEXT, "vcard", charset = UTF_8)
    @JvmField
    val WML_UTF_8 = MediaType(TEXT, "vnd.wap.wml", charset = UTF_8)
    /**
     * As described in [RFC 3023](http://www.ietf.org/rfc/rfc3023.txt), this type is used
     * for XML documents that are "readable by casual users."
     * [APPLICATION_XML_UTF_8] is provided for documents that are intended for applications.
     */
    @JvmField
    val XML_UTF_8 = MediaType(TEXT, "xml", charset = UTF_8)
    /**
     * As described in [the VTT spec](https://w3c.github.io/webvtt/#iana-text-vtt), this is
     * used for Web Video Text Tracks (WebVTT) files, used with the HTML5 track element.
     */
    @JvmField
    val VTT_UTF_8 = MediaType(TEXT, "vtt", charset = UTF_8)


    /* Image types: */
    @JvmField
    val ARW = MediaType(IMAGE, "arw")

    init {
        add(
                ARW,
                MediaType(IMAGE, "x-sony-arw")
        )
    }

    @JvmField
    val BMP = MediaType(IMAGE, "bmp")

    init {
        add(
                BMP,
                MediaType(IMAGE, "x-bmp"),
                MediaType(IMAGE, "x-bitmap"),
                MediaType(IMAGE, "x-xbitmap"),
                MediaType(IMAGE, "x-win-bitmap"),
                MediaType(IMAGE, "x-windows-bmp"),
                MediaType(IMAGE, "ms-bmp"),
                MediaType(IMAGE, "x-ms-bmp"),
                MediaType(APPLICATION, "bmp"),
                MediaType(APPLICATION, "x-bmp"),
                MediaType(APPLICATION, "x-win-bitmap")
        )
    }

    @JvmField
    val CR2 = MediaType(IMAGE, "cr2")

    init {
        add(
                CR2,
                MediaType(IMAGE, "x-canon-cr2"),
                MediaType(IMAGE, "x-dcraw")
        )
    }

    /**
     * The media type for the [Canon Image File Format](http://en.wikipedia.org/wiki/Camera_Image_File_Format) (`crw` files),
     * a widely-used "raw image" format for cameras. It is found in `/etc/mime.types`,
     * e.g. in [Debian 3.48-1](http://anonscm.debian.org/gitweb/?p=collab-maint/mime-support.git;a=blob;f=mime.types;hb=HEAD).
     */
    @JvmField
    val CRW = MediaType(IMAGE, "x-canon-crw")
    @JvmField
    val DCX = MediaType(IMAGE, "dcx")

    init {
        add(
                DCX,
                MediaType(IMAGE, "x-dcx")
        )
    }

    @JvmField
    val DNG = MediaType(IMAGE, "dng")

    init {
        add(
                DNG,
                MediaType(IMAGE, "x-adobe-dng")
        )
    }

    @JvmField
    val GIF = MediaType(IMAGE, "gif")
    @JvmField
    val ICO = MediaType(IMAGE, "vnd.microsoft.icon")

    init {
        add(
                ICO,
                MediaType(IMAGE, "ico"),
                MediaType(IMAGE, "x-icon"),
                MediaType(APPLICATION, "ico"),
                MediaType(APPLICATION, "x-icon")
        )
    }

    @JvmField
    val JPEG = MediaType(IMAGE, "jpeg")

    init {
        add(
                JPEG,
                MediaType(IMAGE, "jpg"),
                MediaType(APPLICATION, "jpg"),
                MediaType(APPLICATION, "x-jpg"),
                MediaType(IMAGE, "pjpeg"),
                MediaType(IMAGE, "pipeg"),
                MediaType(IMAGE, "vnd.swiftview-jpeg")
        )
    }

    @JvmField
    val NEF = MediaType(IMAGE, "nef")

    init {
        add(
                NEF,
                MediaType(IMAGE, "x-nikon-nef")
        )
    }

    @JvmField
    val NRW = MediaType(IMAGE, "nrw")

    init {
        add(
                NRW,
                MediaType(IMAGE, "x-nikon-nrw"),
                *get(NEF).toTypedArray()
        )
    }

    @JvmField
    val ORF = MediaType(IMAGE, "orf")

    init {
        add(
                ORF,
                MediaType(IMAGE, "x-olympus-orf")
        )
    }

    @JvmField
    val PCX = MediaType(IMAGE, "pcx")

    init {
        add(
                PCX,
                MediaType(IMAGE, "x-pcx"),
                MediaType(IMAGE, "x-pc-paintbrush"),
                MediaType(IMAGE, "vnd.swiftview-pcx"),
                MediaType(APPLICATION, "pcx"),
                MediaType(APPLICATION, "x-pcx"),
                MediaType("zz-application", "zz-winassoc-pcx")
        )
    }

    @JvmField
    val PEF = MediaType(IMAGE, "pef")

    init {
        add(
                PEF,
                MediaType(IMAGE, "x-pentax-pef")
        )
    }

    @JvmField
    val PNG = MediaType(IMAGE, "png")

    init {
        add(
                PNG,
                MediaType(APPLICATION, "png"),
                MediaType(APPLICATION, "x-png")
        )
    }

    @JvmField
    val PNM = MediaType(IMAGE, "x-portable-anymap")

    init {
        add(
                PNM,
                MediaType(IMAGE, "pbm")
        )
    }

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
    @JvmField
    val PSD = MediaType(IMAGE, "vnd.adobe.photoshop")

    init {
        add(
                PSD,
                MediaType(IMAGE, "photoshop"),
                MediaType(IMAGE, "x-photoshop"),
                MediaType(IMAGE, "psd"),
                MediaType(APPLICATION, "photoshop"),
                MediaType(APPLICATION, "psd"),
                MediaType("zz-application", "zz-winassoc-psd")
        )
    }

    @JvmField
    val RAF = MediaType(IMAGE, "raf")

    init {
        add(
                RAF,
                MediaType(IMAGE, "x-fuji-raf")
        )
    }

    @JvmField
    val RW2 = MediaType(IMAGE, "rw2")

    init {
        add(
                RW2,
                MediaType(IMAGE, "x-panasonic-rw2"),
                MediaType(IMAGE, "x-panasonic-raw")
        )
    }

    @JvmField
    val SRW = MediaType(IMAGE, "srw")

    init {
        add(
                SRW,
                MediaType(IMAGE, "x-samsung-srw"),
                MediaType(APPLICATION, "octet-stream")
        )
    }

    @JvmField
    val SVG_UTF_8 = MediaType(IMAGE, "svg+xml", charset = UTF_8)
    @JvmField
    val TIFF = MediaType(IMAGE, "tiff")
    @JvmField
    val WBMP = setOf("image/vnd.wap.wbmp")
    @JvmField
    val WEBP = MediaType(IMAGE, "webp")
    @JvmField
    val XBM = MediaType(IMAGE, "x-xbitmap")
    @JvmField
    val XPM = MediaType(IMAGE, "x-xpixmap")

    init {
        add(
                XPM,
                MediaType(IMAGE, "x-xbitmap"),
                MediaType(IMAGE, "xpm"),
                MediaType(IMAGE, "x-xpm")
        )
    }


    /* Audio types: */
    @JvmField
    val MP4_AUDIO = MediaType(AUDIO, "mp4")
    @JvmField
    val MPEG_AUDIO = MediaType(AUDIO, "mpeg")
    @JvmField
    val OGG_AUDIO = MediaType(AUDIO, "ogg")
    @JvmField
    val WEBM_AUDIO = MediaType(AUDIO, "webm")
    /**
     * Media type for L16 audio, as defined by [RFC 2586](https://tools.ietf.org/html/rfc2586).
     */
    @JvmField
    val L16_AUDIO = MediaType(AUDIO, "l16")
    /**
     * Media type for L24 audio, as defined by [RFC 3190](https://tools.ietf.org/html/rfc3190).
     */
    @JvmField
    val L24_AUDIO = MediaType(AUDIO, "l24")
    /**
     * Media type for Basic Audio, as defined by [RFC 2046](http://tools.ietf.org/html/rfc2046#section-4.3).
     */
    @JvmField
    val BASIC_AUDIO = MediaType(AUDIO, "basic")
    /**
     * Media type for Advanced Audio Coding. For more information,
     * see [Advanced Audio Coding](https://en.wikipedia.org/wiki/Advanced_Audio_Coding).
     */
    @JvmField
    val AAC_AUDIO = MediaType(AUDIO, "aac")
    /**
     * Media type for Vorbis Audio, as defined by [RFC 5215](http://tools.ietf.org/html/rfc5215).
     */
    @JvmField
    val VORBIS_AUDIO = MediaType(AUDIO, "vorbis")
    /**
     * Media type for Windows Media Audio. For more information,
     * see [file name extensions for Windows Media metafiles](https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx).
     */
    @JvmField
    val WMA_AUDIO = MediaType(AUDIO, "x-ms-wma")
    /**
     * Media type for Windows Media metafiles. For more information,
     * see [file name extensions for Windows Media metafiles](https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx).
     */
    @JvmField
    val WAX_AUDIO = MediaType(AUDIO, "x-ms-wax")
    /**
     * Media type for Real Audio. For more information,
     * see [this link](http://service.real.com/help/faq/rp8/configrp8win.html).
     */
    @JvmField
    val VND_REAL_AUDIO = MediaType(AUDIO, "vnd.rn-realaudio")
    /**
     * Media type for WAVE format, as defined by [RFC 2361](https://tools.ietf.org/html/rfc2361).
     */
    @JvmField
    val VND_WAVE_AUDIO = MediaType(AUDIO, "vnd.wave")


    /* Video types: */
    @JvmField
    val MP4_VIDEO = MediaType(VIDEO, "mp4")
    @JvmField
    val MPEG_VIDEO = MediaType(VIDEO, "mpeg")
    @JvmField
    val OGG_VIDEO = MediaType(VIDEO, "ogg")
    @JvmField
    val QUICKTIME = MediaType(VIDEO, "quicktime")
    @JvmField
    val WEBM_VIDEO = MediaType(VIDEO, "webm")
    @JvmField
    val WMV = MediaType(VIDEO, "x-ms-wmv")
    /**
     * Media type for Flash video. For more information,
     * see [this link](http://help.adobe.com/en_US/ActionScript/3.0_ProgrammingAS3/WS5b3ccc516d4fbf351e63e3d118a9b90204-7d48.html).
     */
    @JvmField
    val FLV_VIDEO = MediaType(VIDEO, "x-flv")
    /**
     * Media type for the 3GP multimedia container format. For more information,
     * see [3GPP TS 26.244](ftp://www.3gpp.org/tsg_sa/TSG_SA/TSGS_23/Docs/PDF/SP-040065.pdf#page=10).
     */
    @JvmField
    val THREE_GPP_VIDEO = MediaType(VIDEO, "3gpp")
    /**
     * Media type for the 3G2 multimedia container format. For more information,
     * see [3GPP2 C.S0050-B](http://www.3gpp2.org/Public_html/specs/C.S0050-B_v1.0_070521.pdf#page=16).
     */
    @JvmField
    val THREE_GPP2_VIDEO = MediaType(VIDEO, "3gpp2")


    /* Application types: */
    /**
     * As described in [RFC 3023](http://www.ietf.org/rfc/rfc3023.txt), this type is used
     * for XML documents that are "unreadable by casual users."
     * [XML_UTF_8] is provided for documents that may be read by users.
     */
    @JvmField
    val APPLICATION_XML_UTF_8 = MediaType(APPLICATION, "xml", charset = UTF_8)
    @JvmField
    val ATOM_UTF_8 = MediaType(APPLICATION, "atom+xml", charset = UTF_8)
    @JvmField
    val BZIP2 = MediaType(APPLICATION, "x-bzip2")
    /**
     * Media type for [dart files](https://www.dartlang.org/articles/embedding-in-html/).
     */
    @JvmField
    val DART_UTF_8 = MediaType(APPLICATION, "dart", charset = UTF_8)
    /**
     * Media type for [Apple Passbook](https://goo.gl/2QoMvg).
     */
    @JvmField
    val APPLE_PASSBOOK = MediaType(APPLICATION, "vnd.apple.pkpass")
    /**
     * Media type for [Embedded OpenType](http://en.wikipedia.org/wiki/Embedded_OpenType)
     * fonts. This is [registered](http://www.iana.org/assignments/media-types/application/vnd.ms-fontobject)
     * with the IANA.
     */
    @JvmField
    val EOT = MediaType(APPLICATION, "vnd.ms-fontobject")
    /**
     * As described in the [International Digital Publishing Forum](http://idpf.org/epub)
     * EPUB is the distribution and interchange format standard for digital publications and
     * documents. This media type is defined in the [EPUB Open Container Format](http://www.idpf.org/epub/30/spec/epub30-ocf.html)
     * specification.
     */
    @JvmField
    val EPUB = MediaType(APPLICATION, "epub+zip")
    @JvmField
    val FORM_DATA = MediaType(APPLICATION, "x-www-form-urlencoded")
    /**
     * As described in [PKCS #12: Personal Information Exchange Syntax Standard](https://www.rsa.com/rsalabs/node.asp?id=2138),
     * PKCS #12 defines an archive file format for storing
     * many cryptography objects as a single file.
     */
    @JvmField
    val KEY_ARCHIVE = MediaType(APPLICATION, "pkcs12")
    /**
     * This is a non-standard media type, but is commonly used in serving hosted binary files
     * as it is [known not to trigger content sniffing in current browsers](http://code.google.com/p/browsersec/wiki/Part2#Survey_of_content_sniffing_behaviors).
     * It *should not* be used in other situations as it is not specified by any RFC
     * and does not appear in the [/IANA MIME Media Types](http://www.iana.org/assignments/media-types) list.
     * Consider [OCTET_STREAM] for binary data that is not being served to a browser.
     */
    @JvmField
    val APPLICATION_BINARY = MediaType(APPLICATION, "binary")
    @JvmField
    val GZIP = MediaType(APPLICATION, "x-gzip")
    /**
     * Media type for the [JSON Hypertext Application Language (HAL) documents](https://tools.ietf.org/html/draft-kelly-json-hal-08#section-3).
     */
    @JvmField
    val HAL_JSON = MediaType(APPLICATION, "hal+json")
    /**
     * [RFC 4329](http://www.rfc-editor.org/rfc/rfc4329.txt) declares this to be the
     * correct media type for JavaScript, but [text/javascript][TEXT_JAVASCRIPT_UTF_8] may be
     * necessary in certain situations for compatibility.
     */
    @JvmField
    val JAVASCRIPT_UTF_8 = MediaType(APPLICATION, "javascript", charset = UTF_8)
    @JvmField
    val JSON_UTF_8 = MediaType(APPLICATION, "json", charset = UTF_8)
    /**
     * Media type for the [Manifest for a web application](http://www.w3.org/TR/appmanifest/).
     */
    @JvmField
    val MANIFEST_JSON_UTF_8 = MediaType(APPLICATION, "manifest+json", charset = UTF_8)
    /**
     * Media type for [OGC KML (Keyhole Markup Language)](http://www.opengeospatial.org/standards/kml/).
     */
    @JvmField
    val KML = MediaType(APPLICATION, "vnd.google-earth.kml+xml")
    /**
     * Media type for [OGC KML (Keyhole Markup Language)](http://www.opengeospatial.org/standards/kml/),
     * compressed using the ZIP format into KMZ archives.
     */
    @JvmField
    val KMZ = MediaType(APPLICATION, "vnd.google-earth.kmz")
    /** Media type for the [mbox database format](https://tools.ietf.org/html/rfc4155).  */
    @JvmField
    val MBOX = MediaType(APPLICATION, "mbox")
    /**
     * Media type for [Apple over-the-air mobile configuration profiles](http://goo.gl/1pGBFm).
     */
    @JvmField
    val APPLE_MOBILE_CONFIG = MediaType(APPLICATION, "x-apple-aspen-config")
    @JvmField
    val MICROSOFT_EXCEL = MediaType(APPLICATION, "vnd.ms-excel")
    @JvmField
    val MICROSOFT_POWERPOINT = MediaType(APPLICATION, "vnd.ms-powerpoint")
    @JvmField
    val MICROSOFT_WORD = MediaType(APPLICATION, "msword")
    /**
     * Media type for WASM applications. For more information see [the Web Assembly overview](https://webassembly.org/).
     */
    @JvmField
    val WASM_APPLICATION = MediaType(APPLICATION, "wasm")
    /**
     * Media type for NaCl applications. For more information see [the Developer Guide for Native Client Application Structure](https://developer.chrome.com/native-client/devguide/coding/application-structure).
     */
    @JvmField
    val NACL_APPLICATION = MediaType(APPLICATION, "x-nacl")
    /**
     * Media type for NaCl portable applications. For more information
     * see [the Developer Guide for Native Client Application Structure](https://developer.chrome.com/native-client/devguide/coding/application-structure).
     */
    @JvmField
    val NACL_PORTABLE_APPLICATION = MediaType(APPLICATION, "x-pnacl")
    @JvmField
    val OCTET_STREAM = MediaType(APPLICATION, "octet-stream")
    @JvmField
    val OGG_CONTAINER = MediaType(APPLICATION, "ogg")
    @JvmField
    val OOXML_DOCUMENT = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    @JvmField
    val OOXML_PRESENTATION = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.presentationml.presentation"
    )
    @JvmField
    val OOXML_SHEET = MediaType(
            APPLICATION,
            "vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @JvmField
    val OPENDOCUMENT_GRAPHICS = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.graphics"
    )
    @JvmField
    val OPENDOCUMENT_PRESENTATION = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.presentation"
    )
    @JvmField
    val OPENDOCUMENT_SPREADSHEET = MediaType(
            APPLICATION,
            "vnd.oasis.opendocument.spreadsheet"
    )
    @JvmField
    val OPENDOCUMENT_TEXT = MediaType(APPLICATION, "vnd.oasis.opendocument.text")
    @JvmField
    val PDF = MediaType(APPLICATION, "pdf")
    @JvmField
    val POSTSCRIPT = MediaType(APPLICATION, "postscript")
    /**
     * [Protocol buffers](http://tools.ietf.org/html/draft-rfernando-protocol-buffers-00)
     */
    @JvmField
    val PROTOBUF = MediaType(APPLICATION, "protobuf")
    @JvmField
    val RDF_XML_UTF_8 = MediaType(APPLICATION, "rdf+xml", charset = UTF_8)
    @JvmField
    val RTF_UTF_8 = MediaType(APPLICATION, "rtf", charset = UTF_8)
    /**
     * Media type for SFNT fonts (which includes [TrueType](http://en.wikipedia.org/wiki/TrueType/)
     * and [OpenType](http://en.wikipedia.org/wiki/OpenType/) fonts).
     * This is [registered](http://www.iana.org/assignments/media-types/application/font-sfnt)
     * with the IANA.
     */
    @JvmField
    val SFNT = MediaType(APPLICATION, "font-sfnt")
    @JvmField
    val SHOCKWAVE_FLASH = MediaType(APPLICATION, "x-shockwave-flash")
    @JvmField
    val SKETCHUP = MediaType(APPLICATION, "vnd.sketchup.skp")
    /**
     * As described in [RFC 3902](http://www.ietf.org/rfc/rfc3902.txt), this constant
     * (`application/soap+xml`) is used to identify SOAP 1.2 message envelopes that have been
     * serialized with XML 1.0.
     *
     * For SOAP 1.1 messages, see `XML_UTF_8`
     * per [W3C Note on Simple Object Access Protocol (SOAP) 1.1](http://www.w3.org/TR/2000/NOTE-SOAP-20000508/)
     */
    @JvmField
    val SOAP_XML_UTF_8 = MediaType(APPLICATION, "soap+xml", charset = UTF_8)
    @JvmField
    val TAR = MediaType(APPLICATION, "x-tar")
    /**
     * Media type for the [Web Open Font Format](http://en.wikipedia.org/wiki/Web_Open_Font_Format) (WOFF)
     * [defined](http://www.w3.org/TR/WOFF/) by the W3C.
     * This is [registered](http://www.iana.org/assignments/media-types/application/font-woff) with
     * the IANA.
     */
    @JvmField
    val WOFF = MediaType(APPLICATION, "font-woff")
    /**
     * Media type for the [Web Open Font Format](http://en.wikipedia.org/wiki/Web_Open_Font_Format) (WOFF)
     * version 2 [defined](https://www.w3.org/TR/WOFF2/) by the W3C.
     */
    @JvmField
    val WOFF2 = MediaType(APPLICATION, "font-woff2")
    @JvmField
    val XHTML_UTF_8 = MediaType(APPLICATION, "xhtml+xml", charset = UTF_8)
    /**
     * Media type for Extensible Resource Descriptors. This is not yet registered
     * with the IANA, but it is specified by OASIS
     * in the [XRD definition](http://docs.oasis-open.org/xri/xrd/v1.0/cd02/xrd-1.0-cd02.html)
     * and implemented in projects such as [WebFinger](http://code.google.com/p/webfinger/).
     */
    @JvmField
    val XRD_UTF_8 = MediaType(APPLICATION, "xrd+xml", charset = UTF_8)
    @JvmField
    val ZIP = MediaType(APPLICATION, "zip")
}
