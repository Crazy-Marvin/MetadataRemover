package rocks.poopjournal.metadataremover.util

object MimeTypes {

    @JvmField
    val JPEG = setOf(
            "image/jpeg",
            "image/jpg",
            "application/jpg",
            "application/x-jpg",
            "image/pjpeg",
            "image/pipeg",
            "image/vnd.swiftview-jpeg"
    )

    @JvmField
    val DNG = setOf(
            "image/dng",
            "image/x-adobe-dng"
    )

    @JvmField
    val CR2 = setOf(
            "image/cr2",
            "image/x-canon-cr2",
            "image/x-dcraw"
    )

    @JvmField
    val NEF = setOf(
            "image/nef",
            "image/x-nikon-nef"
    )

    @JvmField
    val NRW = setOf(
            "image/nrw",
            "image/x-nikon-nrw"
    ) + NEF

    @JvmField
    val ARW = setOf(
            "image/arw",
            "image/x-sony-arw"
    )

    @JvmField
    val RW2 = setOf(
            "image/rw2",
            "image/x-panasonic-rw2",
            "image/x-panasonic-raw"
    )

    @JvmField
    val ORF = setOf(
            "image/orf",
            "image/x-olympus-orf"
    )

    @JvmField
    val PEF = setOf(
            "image/pef",
            "image/x-pentax-pef"
    )

    @JvmField
    val SRW = setOf(
            "image/srw",
            "image/x-samsung-srw",
            "application/octet-stream"
    )

    @JvmField
    val RAF = setOf(
            "image/raf",
            "image/x-fuji-raf"
    )

    @JvmField
    val BMP = setOf(
            "image/bmp",
            "image/x-bmp",
            "image/x-bitmap",
            "image/x-xbitmap",
            "image/x-win-bitmap",
            "image/x-windows-bmp",
            "image/ms-bmp",
            "image/x-ms-bmp",
            "application/bmp",
            "application/x-bmp",
            "application/x-win-bitmap"
    )

    @JvmField
    val DCX = setOf(
            "image/dcx",
            "image/x-dcx"
    )

    @JvmField
    val GIF = setOf("image/gif")

    @JvmField
    val ICO = setOf(
            "image/ico",
            "image/x-icon",
            "application/ico",
            "application/x-ico"
    )

    @JvmField
    val PCX = setOf(
            "application/pcx",
            "application/x-pcx",
            "image/pcx",
            "image/x-pc-paintbrush",
            "image/vnd.swiftview-pcx",
            "image/x-pcx",
            "zz-application/zz-winassoc-pcx"
    )

    @JvmField
    val PNG = setOf(
            "image/png",
            "application/png",
            "application/x-png"
    )

    @JvmField
    val PNM = setOf(
            "image/x-portable-anymap",
            "image/x-portable/anymap",
            "image/pbm"
    )

    @JvmField
    val PSD = setOf(
            "image/photoshop",
            "image/x-photoshop",
            "image/psd",
            "application/photoshop",
            "application/psd",
            "zz-application/zz-winassoc-psd"
    )

    @JvmField
    val TIFF = setOf("image/tiff")

    @JvmField
    val WEBP = setOf("image/webp")

    @JvmField
    val WBMP = setOf("image/vnd.wap.wbmp")

    @JvmField
    val XBM = setOf("image/x-xbitmap")

    @JvmField
    val XPM = setOf(
            "image/x-xpixmap",
            "image/x-xbitmap",
            "image/xpm",
            "image/x-xpm"
    )
}