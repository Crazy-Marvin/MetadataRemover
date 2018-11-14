package rocks.poopjournal.metadataremover.util

import android.graphics.BitmapFactory
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.R.drawable
import rocks.poopjournal.metadataremover.R.string
import rocks.poopjournal.metadataremover.model.metadata.Metadata.Attribute
import rocks.poopjournal.metadataremover.model.resources.Image
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.extensions.format
import java.io.File

class ImageFile(val file: File) : File(file.path) {
    private val bitmapOptions =
            BitmapFactory.Options()
                    .apply { inJustDecodeBounds = true }
                    .also { BitmapFactory.decodeFile(path, it) }
    val resolutionX: Long = bitmapOptions.outWidth.toLong()
    val resolutionY: Long = bitmapOptions.outHeight.toLong()
    val resolution: Long = resolutionX * resolutionY

    private val resolutionLabel: Text = resolution.let { pixels ->
        if (pixels > 1_000_000) Text(R.string.description_attribute_image_resolution_mega_pixels,
                (pixels / 1_000_000.0).format(2))
        else Text(R.string.description_attribute_image_resolution_pixels, pixels)
    }
    private val resolutionXyLabel: Text = Text(R.string.description_attribute_image_resolution_xy,
            resolutionX, resolutionY)

    val resolutionAttribute = Attribute(
            Text(string.label_attribute_image_resolution),
            Image(drawable.ic_crop_free),
            resolutionLabel,
            resolutionXyLabel
    )
}