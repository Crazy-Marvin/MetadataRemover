package rocks.poopjournal.metadataremover.util.extensions.android

import android.location.Address
import android.location.Geocoder
import androidx.exifinterface.media.ExifInterface
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.model.coordinates.Latitude
import rocks.poopjournal.metadataremover.model.coordinates.Longitude
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.extensions.attributeDateFormat
import rocks.poopjournal.metadataremover.util.extensions.attributeTimeFormat
import rocks.poopjournal.metadataremover.util.extensions.format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.experimental.and

object ExifTags {

    private const val TAG_SUB_IFD_POINTER = "SubIFDPointer"
    private const val TAG_EXIF_IFD_POINTER = "ExifIFDPointer"
    private const val TAG_GPS_INFO_IFD_POINTER = "GPSInfoIFDPointer"
    private const val TAG_INTEROPERABILITY_IFD_POINTER = "InteroperabilityIFDPointer"
    private const val TAG_ORF_CAMERA_SETTINGS_IFD_POINTER = "CameraSettingsIFDPointer"
    private const val TAG_ORF_IMAGE_PROCESSING_IFD_POINTER = "ImageProcessingIFDPointer"

    private val IFD_TIFF_TAGS = arrayOf(
            ExifInterface.TAG_NEW_SUBFILE_TYPE,
            ExifInterface.TAG_SUBFILE_TYPE,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_COMPRESSION,
            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
            ExifInterface.TAG_IMAGE_DESCRIPTION,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_STRIP_OFFSETS,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_SAMPLES_PER_PIXEL,
            ExifInterface.TAG_ROWS_PER_STRIP,
            ExifInterface.TAG_STRIP_BYTE_COUNTS,
            ExifInterface.TAG_X_RESOLUTION,
            ExifInterface.TAG_Y_RESOLUTION,
            ExifInterface.TAG_PLANAR_CONFIGURATION,
            ExifInterface.TAG_RESOLUTION_UNIT,
            ExifInterface.TAG_TRANSFER_FUNCTION,
            ExifInterface.TAG_SOFTWARE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_WHITE_POINT,
            ExifInterface.TAG_PRIMARY_CHROMATICITIES,
            TAG_SUB_IFD_POINTER,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
            ExifInterface.TAG_Y_CB_CR_COEFFICIENTS,
            ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING,
            ExifInterface.TAG_Y_CB_CR_POSITIONING,
            ExifInterface.TAG_REFERENCE_BLACK_WHITE,
            ExifInterface.TAG_COPYRIGHT,
            TAG_EXIF_IFD_POINTER,
            TAG_GPS_INFO_IFD_POINTER,
            ExifInterface.TAG_RW2_SENSOR_TOP_BORDER,
            ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER,
            ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER,
            ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER,
            ExifInterface.TAG_RW2_ISO,
            ExifInterface.TAG_RW2_JPG_FROM_RAW
    )

    private val IFD_EXIF_TAGS = arrayOf(
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_EXPOSURE_PROGRAM,
            ExifInterface.TAG_SPECTRAL_SENSITIVITY,
            ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
            ExifInterface.TAG_OECF,
            ExifInterface.TAG_EXIF_VERSION,
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_DATETIME_DIGITIZED,
            ExifInterface.TAG_COMPONENTS_CONFIGURATION,
            ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL,
            ExifInterface.TAG_SHUTTER_SPEED_VALUE,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_BRIGHTNESS_VALUE,
            ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
            ExifInterface.TAG_MAX_APERTURE_VALUE,
            ExifInterface.TAG_SUBJECT_DISTANCE,
            ExifInterface.TAG_METERING_MODE,
            ExifInterface.TAG_LIGHT_SOURCE,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_SUBJECT_AREA,
            ExifInterface.TAG_MAKER_NOTE,
            ExifInterface.TAG_USER_COMMENT,
            ExifInterface.TAG_SUBSEC_TIME,
            ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
            ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
            ExifInterface.TAG_FLASHPIX_VERSION,
            ExifInterface.TAG_COLOR_SPACE,
            ExifInterface.TAG_PIXEL_X_DIMENSION,
            ExifInterface.TAG_PIXEL_Y_DIMENSION,
            ExifInterface.TAG_RELATED_SOUND_FILE,
            TAG_INTEROPERABILITY_IFD_POINTER,
            ExifInterface.TAG_FLASH_ENERGY,
            ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE,
            ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT,
            ExifInterface.TAG_SUBJECT_LOCATION,
            ExifInterface.TAG_EXPOSURE_INDEX,
            ExifInterface.TAG_SENSING_METHOD,
            ExifInterface.TAG_FILE_SOURCE,
            ExifInterface.TAG_SCENE_TYPE,
            ExifInterface.TAG_CFA_PATTERN,
            ExifInterface.TAG_CUSTOM_RENDERED,
            ExifInterface.TAG_EXPOSURE_MODE,
            ExifInterface.TAG_WHITE_BALANCE,
            ExifInterface.TAG_DIGITAL_ZOOM_RATIO,
            ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM,
            ExifInterface.TAG_SCENE_CAPTURE_TYPE,
            ExifInterface.TAG_GAIN_CONTROL,
            ExifInterface.TAG_CONTRAST,
            ExifInterface.TAG_SATURATION,
            ExifInterface.TAG_SHARPNESS,
            ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,
            ExifInterface.TAG_SUBJECT_DISTANCE_RANGE,
            ExifInterface.TAG_IMAGE_UNIQUE_ID,
            ExifInterface.TAG_DNG_VERSION,
            ExifInterface.TAG_DEFAULT_CROP_SIZE
    )

    private val IFD_GPS_TAGS = arrayOf(
            ExifInterface.TAG_GPS_VERSION_ID,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_GPS_SATELLITES,
            ExifInterface.TAG_GPS_STATUS,
            ExifInterface.TAG_GPS_MEASURE_MODE,
            ExifInterface.TAG_GPS_DOP,
            ExifInterface.TAG_GPS_SPEED_REF,
            ExifInterface.TAG_GPS_SPEED,
            ExifInterface.TAG_GPS_TRACK_REF,
            ExifInterface.TAG_GPS_TRACK,
            ExifInterface.TAG_GPS_IMG_DIRECTION_REF,
            ExifInterface.TAG_GPS_IMG_DIRECTION,
            ExifInterface.TAG_GPS_MAP_DATUM,
            ExifInterface.TAG_GPS_DEST_LATITUDE_REF,
            ExifInterface.TAG_GPS_DEST_LATITUDE,
            ExifInterface.TAG_GPS_DEST_LONGITUDE_REF,
            ExifInterface.TAG_GPS_DEST_LONGITUDE,
            ExifInterface.TAG_GPS_DEST_BEARING_REF,
            ExifInterface.TAG_GPS_DEST_BEARING,
            ExifInterface.TAG_GPS_DEST_DISTANCE_REF,
            ExifInterface.TAG_GPS_DEST_DISTANCE,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
            ExifInterface.TAG_GPS_AREA_INFORMATION,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_DIFFERENTIAL
    )

    private val IFD_INTEROPERABILITY_TAGS = arrayOf(
            ExifInterface.TAG_INTEROPERABILITY_INDEX
    )

    private val IFD_THUMBNAIL_TAGS = arrayOf(
            ExifInterface.TAG_NEW_SUBFILE_TYPE,
            ExifInterface.TAG_SUBFILE_TYPE,
            ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH,
            ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_COMPRESSION,
            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
            ExifInterface.TAG_IMAGE_DESCRIPTION,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_STRIP_OFFSETS,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_SAMPLES_PER_PIXEL,
            ExifInterface.TAG_ROWS_PER_STRIP,
            ExifInterface.TAG_STRIP_BYTE_COUNTS,
            ExifInterface.TAG_X_RESOLUTION,
            ExifInterface.TAG_Y_RESOLUTION,
            ExifInterface.TAG_PLANAR_CONFIGURATION,
            ExifInterface.TAG_RESOLUTION_UNIT,
            ExifInterface.TAG_TRANSFER_FUNCTION,
            ExifInterface.TAG_SOFTWARE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_WHITE_POINT,
            ExifInterface.TAG_PRIMARY_CHROMATICITIES,
            TAG_SUB_IFD_POINTER,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
            ExifInterface.TAG_Y_CB_CR_COEFFICIENTS,
            ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING,
            ExifInterface.TAG_Y_CB_CR_POSITIONING,
            ExifInterface.TAG_REFERENCE_BLACK_WHITE,
            ExifInterface.TAG_COPYRIGHT,
            TAG_EXIF_IFD_POINTER,
            TAG_GPS_INFO_IFD_POINTER,
            ExifInterface.TAG_DNG_VERSION,
            ExifInterface.TAG_DEFAULT_CROP_SIZE
    )

    private val ORF_MAKER_NOTE_TAGS = arrayOf(
            ExifInterface.TAG_ORF_THUMBNAIL_IMAGE,
            TAG_ORF_CAMERA_SETTINGS_IFD_POINTER,
            TAG_ORF_IMAGE_PROCESSING_IFD_POINTER
    )

    private val ORF_CAMERA_SETTINGS_TAGS = arrayOf(
            ExifInterface.TAG_ORF_PREVIEW_IMAGE_START,
            ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH
    )

    private val ORF_IMAGE_PROCESSING_TAGS = arrayOf(
            ExifInterface.TAG_ORF_ASPECT_FRAME
    )

    private val PEF_TAGS = arrayOf(
            ExifInterface.TAG_COLOR_SPACE
    )

    val TAGS = IFD_TIFF_TAGS + IFD_EXIF_TAGS + IFD_GPS_TAGS +
            IFD_INTEROPERABILITY_TAGS + IFD_THUMBNAIL_TAGS + IFD_TIFF_TAGS +
            ORF_MAKER_NOTE_TAGS + ORF_CAMERA_SETTINGS_TAGS + ORF_IMAGE_PROCESSING_TAGS +
            PEF_TAGS
}

fun ExifInterface.clearAttribute(tag: String): ExifInterface {
    setAttribute(tag, null)
    return this
}

fun ExifInterface.clearAttributes(vararg tags: String): ExifInterface {
    tags.forEach { clearAttribute(it) }
    return this
}

fun ExifInterface.clearAllAttributes() = clearAttributes(*ExifTags.TAGS)


val exifDateFormat = SimpleDateFormat("YYYY:MM:DD HH:MM:SS", Locale.US)
val ExifInterface.creationDate: Date?
    get() {
        val dateTimeString = getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: return null
        return try {
            exifDateFormat.parse(dateTimeString); } catch (e: ParseException) {
            null
        }
    }
val ExifInterface.creationAttribute: Metadata.Attribute?
    get() {
        val creationDate = creationDate ?: return null
        return Metadata.Attribute(
                label = Text(R.string.label_attribute_file_creation_date_time),
                icon = Image(R.drawable.ic_event),
                primaryValue = Text(attributeDateFormat.format(creationDate)),
                secondaryValue = Text(attributeTimeFormat.format(creationDate))
        )
    }

fun ExifInterface.getLocationAttribute(geocoder: Geocoder? = null): Metadata.Attribute? {
    val latitudeLongitude = latLong ?: return null
    val latitude = Latitude(latitudeLongitude[0])
    val latitudeDirection = Text(
            if (latitude.value < 0) {
                R.string.description_attribute_file_creation_location_direction_west
            } else {
                R.string.description_attribute_file_creation_location_direction_east
            }
    )
    val longitude = Longitude(latitudeLongitude[1])
    val longitudeDirection = Text(
            if (longitude.value < 0) {
                R.string.description_attribute_file_creation_location_direction_south
            } else {
                R.string.description_attribute_file_creation_location_direction_north
            }
    )
    val coordinates = Text(
            R.string.description_attribute_file_creation_location_coordinates,
            latitudeDirection,
            latitude.hours,
            latitude.minutes,
            latitude.seconds,
            longitudeDirection,
            longitude.hours,
            longitude.minutes,
            longitude.seconds
    )

    val address: Address? = geocoder
            ?.takeIf { Geocoder.isPresent() }
            ?.getFromLocation(latitude.value, longitude.value, 1)
            ?.firstOrNull()
            ?.takeIf { it.maxAddressLineIndex >= 1 }

    if (address == null) {
        return Metadata.Attribute(
                Text(R.string.label_attribute_file_creation_location),
                Image(R.drawable.ic_location),
                coordinates
        )
    } else {
        val firstLine: String = address.getAddressLine(1)
        val secondLine: String? = address.getAddressLine(2)
        val addressLine =
                if (secondLine == null) firstLine
                else "$firstLine, $secondLine"
        return Metadata.Attribute(
                Text(R.string.label_attribute_file_creation_location),
                Image(R.drawable.ic_location),
                Text(addressLine),
                coordinates
        )
    }
}

val ExifInterface.cameraAttribute: Metadata.Attribute?
    get() {
        val model: String = getAttribute(ExifInterface.TAG_MODEL) ?: return null
        val manufacturer: String? = getAttribute(ExifInterface.TAG_MAKE)
        val serialNumber: String? = getAttribute(ExifInterface.TAG_BODY_SERIAL_NUMBER)
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_creation_camera),
                Image(R.drawable.ic_camera),
                if (serialNumber == null) {
                    Text(model)
                } else {
                    Text(R.string.description_attribute_image_creation_model_serial_number, model,
                            serialNumber)
                },
                manufacturer?.let { Text(it) }
        )
    }

val ExifInterface.exposureAttribute: Metadata.Attribute?
    get() {
        val fStop = getAttribute(ExifInterface.TAG_F_NUMBER)
                ?.let { Text(R.string.description_attribute_image_exposure_f_stop, it) }
        val exposureTime = getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
                ?.let { Text(R.string.description_attribute_image_exposure_exposure_time_s, it) }
        val focalLengthMm = getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
                ?.split('/')
                ?.map(String::toInt)
                ?.let { (dividend, divisor) -> dividend / divisor.toDouble() }
                ?.let { it.format(2) }
                ?.let { Text(R.string.description_attribute_image_exposure_focal_length_mm, it) }
        val isoSpeed = getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
                ?.let { Text(R.string.description_attribute_image_exposure_iso_speed, it) }
        val items = listOfNotNull(fStop, exposureTime, focalLengthMm, isoSpeed)
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_exposure),
                Image(R.drawable.ic_exposure),
                Text(
                        when (items.size) {
                            1 -> R.string.description_attribute_image_exposure_1_value
                            2 -> R.string.description_attribute_image_exposure_2_value
                            3 -> R.string.description_attribute_image_exposure_3_value
                            else -> R.string.description_attribute_image_exposure_4_value
                        },
                        *items.toTypedArray()
                )
        )
    }

val ExifInterface.lightAttribute: Metadata.Attribute?
    get() {
        val flashStatus: Short = getAttributeInt(ExifInterface.TAG_FLASH, -1)
                .takeIf { it >= 0 }
                ?.toShort()
                ?.takeIf { it and ExifInterface.FLAG_FLASH_NO_FLASH_FUNCTION != ExifInterface.FLAG_FLASH_NO_FLASH_FUNCTION }
                ?: return null
        val flashFired = flashStatus and ExifInterface.FLAG_FLASH_FIRED == ExifInterface.FLAG_FLASH_FIRED
        val lightSource = getAttributeInt(ExifInterface.TAG_LIGHT_SOURCE,
                ExifInterface.LIGHT_SOURCE_UNKNOWN.toInt()).toShort()
                .let {
                    when (it) {
                        ExifInterface.LIGHT_SOURCE_DAYLIGHT -> R.string.description_attribute_image_light_source_daylight
                        ExifInterface.LIGHT_SOURCE_FLUORESCENT -> R.string.description_attribute_image_light_source_fluorescent
                        ExifInterface.LIGHT_SOURCE_TUNGSTEN -> R.string.description_attribute_image_light_source_tungsten
                        ExifInterface.LIGHT_SOURCE_FLASH -> R.string.description_attribute_image_light_source_flash
                        ExifInterface.LIGHT_SOURCE_FINE_WEATHER -> R.string.description_attribute_image_light_source_fine_weather
                        ExifInterface.LIGHT_SOURCE_CLOUDY_WEATHER -> R.string.description_attribute_image_light_source_cloudy_weather
                        ExifInterface.LIGHT_SOURCE_SHADE -> R.string.description_attribute_image_light_source_shade
                        ExifInterface.LIGHT_SOURCE_DAYLIGHT_FLUORESCENT -> R.string.description_attribute_image_light_source_daylight_fluorescent
                        ExifInterface.LIGHT_SOURCE_DAY_WHITE_FLUORESCENT -> R.string.description_attribute_image_light_source_day_white_fluorescent
                        ExifInterface.LIGHT_SOURCE_COOL_WHITE_FLUORESCENT -> R.string.description_attribute_image_light_source_cool_white_fluorescent
                        ExifInterface.LIGHT_SOURCE_WHITE_FLUORESCENT -> R.string.description_attribute_image_light_source_white_fluorescent
                        ExifInterface.LIGHT_SOURCE_WARM_WHITE_FLUORESCENT -> R.string.description_attribute_image_light_source_warm_white_fluorescent
                        ExifInterface.LIGHT_SOURCE_STANDARD_LIGHT_A,
                        ExifInterface.LIGHT_SOURCE_STANDARD_LIGHT_B,
                        ExifInterface.LIGHT_SOURCE_STANDARD_LIGHT_C,
                        ExifInterface.LIGHT_SOURCE_D55,
                        ExifInterface.LIGHT_SOURCE_D65,
                        ExifInterface.LIGHT_SOURCE_D75,
                        ExifInterface.LIGHT_SOURCE_D50,
                        ExifInterface.LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN,
                        ExifInterface.LIGHT_SOURCE_OTHER,
                        ExifInterface.LIGHT_SOURCE_UNKNOWN -> null
                        else -> null
                    }
                }
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_flash_light_source),
                Image(R.drawable.ic_light),
                Text(if (flashFired) R.string.description_attribute_image_flash_on else R.string.description_attribute_image_flash_off),
                lightSource?.let { Text(it) }
        )
    }

val ExifInterface.ownerAttribute: Metadata.Attribute?
    get() {
        val owner: String = getAttribute(ExifInterface.TAG_CAMARA_OWNER_NAME) ?: return null
        return Metadata.Attribute(
                Text(R.string.label_attribute_file_identity),
                Image(R.drawable.ic_person),
                Text(owner)
        )
    }

val ExifInterface.lensAttribute: Metadata.Attribute?
    get() {
        val model: String = getAttribute(ExifInterface.TAG_LENS_MODEL) ?: return null
        val manufacturer: String? = getAttribute(ExifInterface.TAG_LENS_MAKE)
        val serialNumber: String? = getAttribute(ExifInterface.TAG_LENS_SERIAL_NUMBER)
        return Metadata.Attribute(
                Text(R.string.label_attribute_image_creation_lens),
                Image(R.drawable.ic_lens),
                if (serialNumber == null) {
                    Text(model)
                } else {
                    Text(R.string.description_attribute_image_creation_model_serial_number, model,
                            serialNumber)
                },
                manufacturer?.let { Text(it) }
        )
    }
