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

package rocks.poopjournal.metadataremover.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.getFileName
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.getMediaType
import rocks.poopjournal.metadataremover.util.extensions.android.openAssetFileDescriptor
import rocks.poopjournal.metadataremover.util.extensions.startActivityForResult


/**
 * A file picker that can allow specific mime types.
 *
 * Modified version of [PicPicker](https://github.com/brunodles/PicPicker)
 * which is licensed under Apache License 2.0
 */
class FileOpener(
        private val context: Context,
        private val activityLauncher: ActivityResultLauncher,
        private val onResult: suspend (file: AssetFileDescriptor, displayName: String, mediaType: MediaType) -> Unit,
        private val onNoFileChooserInstalled: () -> Unit,
        private val onWrongMediaTypeFileSelected: (mediaType: MediaType, allowedMediaTypes: Set<MediaType>) -> Unit,
        val mode: String = "r"
) {

    private val requests: MutableMap<Int, Set<MediaType>> = mutableMapOf()

    fun openFile(allowedMediaTypes: Set<MediaType>) {
        if (allowedMediaTypes.isEmpty()) {
            Logger.i("No MIME type seems to bee allowed. Skipping file picker...")
            return
        }
        Logger.i("Opening a file using the system picker...")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(
                            Intent.EXTRA_MIME_TYPES,
                            allowedMediaTypes
                                    .map(MediaType::toString)
                                    .toTypedArray())
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                }

        val requestCode = clipRequestCode(REQUEST_CODE_ATTACH_FILE + allowedMediaTypes.hashCode())
        requests[requestCode] = allowedMediaTypes

        if (intent.resolveActivity(context.packageManager) != null) {
            activityLauncher.startActivityForResult(intent, requestCode)
        } else {
            Logger.w("No Activity found that can select our specific MIME types " +
                    "(${allowedMediaTypes.joinToString(
                            separator = "', '",
                            prefix = "'",
                            postfix = "'")}).\n" +
                    "Trying again to open a file, this time without " +
                    "specifying MIME types explicitly.")
            intent.removeExtra(Intent.EXTRA_MIME_TYPES)
            intent.type = "*/*"

            if (intent.resolveActivity(context.packageManager) != null) {
                activityLauncher.startActivityForResult(intent, requestCode)
            } else {
                Logger.e("No Activity found that can select files.")
                onNoFileChooserInstalled()
            }
        }
    }

    /**
     * [Activities][Activity] using this image picker should proxy
     * their [Activity.onActivityResult] call through this method.
     */
    @SuppressLint("SimpleDateFormat")
    @Suppress("unused")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_CANCELED && requestCode in requests.keys) {
            // The user has cancelled a request.
            requests.remove(requestCode)
            return false
        }

        if (resultCode != Activity.RESULT_OK) return false
        if (requestCode !in requests.keys) return false

        GlobalScope.launch(Dispatchers.IO) {
            val uri = data?.data ?: return@launch
            // `ACTION_OPEN_DOCUMENT` should always return `content:` URIs
            if (uri.scheme != ContentResolver.SCHEME_CONTENT) return@launch


            val mediaType = uri.getMediaType(context)
            if (mediaType == null) {
                Logger.w("Could not guess MIME type from file '${uri.path}'.")
                return@launch
            }

            val allowedMediaTypes = requests[requestCode] ?: return@launch
            if (allowedMediaTypes.none { mediaType in it }) {
                onWrongMediaTypeFileSelected(mediaType, allowedMediaTypes)
                return@launch
            }

            val fileName = uri.getFileName(context)
            if (fileName == null) {
                Logger.w("Could not get name from file '$uri'.")
                return@launch
            }

            val descriptor: AssetFileDescriptor = uri.openAssetFileDescriptor(context, mode)
                    ?: return@launch
            onResult(descriptor, fileName, mediaType)
        }
        return true
    }

    private companion object {
        private const val REQUEST_CODE_ATTACH_FILE = 9123

        private fun clipRequestCode(requestCode: Int): Int {
            return (requestCode and 0x0000ffff)
        }
    }
}