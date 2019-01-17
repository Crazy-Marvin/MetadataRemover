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

package rocks.poopjournal.metadataremover.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rocks.poopjournal.metadataremover.BuildConfig
import rocks.poopjournal.metadataremover.R
import rocks.poopjournal.metadataremover.metadata.handlers.CombiningMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.DummyMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.ExifMetadataHandler
import rocks.poopjournal.metadataremover.metadata.handlers.NopMetadataHandler
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.ui.AboutActivity
import rocks.poopjournal.metadataremover.util.ActivityLauncher
import rocks.poopjournal.metadataremover.util.ActivityResultLauncher
import rocks.poopjournal.metadataremover.util.FilePicker
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.MimeType
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.applicationContext
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.mutableLiveDataOf
import rocks.poopjournal.metadataremover.util.extensions.android.architecture.singleLiveEventOf
import rocks.poopjournal.metadataremover.util.extensions.dequeOf
import rocks.poopjournal.metadataremover.util.extensions.parseUri
import rocks.poopjournal.metadataremover.util.extensions.startActivity
import java.io.File

class MainViewModel(application: Application) :
        AndroidViewModel(application), MetadataViewModel, ActivityLauncherViewModel,
        SnackbarViewModel, ActivityResultLauncherViewModel, ActivityResultLauncher {

    data class WrongMimeTypeFileSelectedHint(val mimeType: MimeType, val allowedMimeTypes: Set<MimeType>)

    override val metadata = mutableLiveDataOf<Metadata?>(null)
    val wrongMimeTypeFileSelectedHint = singleLiveEventOf<WrongMimeTypeFileSelectedHint>()
    override val activityLaunchInfo = singleLiveEventOf<ActivityLauncher.LaunchInfo>()
    override val activityResultLaunchInfo = singleLiveEventOf<ActivityResultLauncher.LaunchInfo>()
    override val snackbarMessage = singleLiveEventOf<SnackbarViewModel.SnackbarMessage>()

    private val metadataHandler = ROOT_METADATA_HANDLER
    // For now w'll restrict file opening to mime types we can write to.
    // Later we might add support for just reading metadata for formats
    // we can't write to.
    private val allowedMimeTypes
        get() = metadataHandler.writableMimeTypes

    private var filePicker = FilePicker(
            applicationContext,
            this,
            ::openFile,
            ::onNoFileChooserInstalled,
            ::onWrongMimeTypeFileSelected)

    private var inputFile: File? = null
    private var inputMimeType: MimeType? = null
    private var outputFile: File? = null

    private fun onNoFileChooserInstalled() {
        // Search the Play store for a file explorer.
        val searchText = applicationContext
                .getString(R.string.search_text_file_explorer)
        val searchUri = "https://play.google.com/store/search"
                .parseUri()
                .buildUpon()
                .appendQueryParameter("c", "apps")
                .appendQueryParameter("q", searchText)
                .build()
        val searchIntent = Intent(Intent.ACTION_VIEW)
                .setData(searchUri)

        startActivity(searchIntent)
    }

    private fun onWrongMimeTypeFileSelected(mimeType: MimeType, allowedMimeTypes: Set<MimeType>) {
        wrongMimeTypeFileSelectedHint.value =
                WrongMimeTypeFileSelectedHint(mimeType, allowedMimeTypes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        filePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun startActivityForResult(launchInfo: ActivityResultLauncher.LaunchInfo) {
        activityResultLaunchInfo.value = launchInfo
    }

    override fun startActivity(launchInfo: ActivityLauncher.LaunchInfo) {
        activityLaunchInfo.value = launchInfo
    }

    fun openAboutScreen() {
        val aboutIntent = Intent(getApplication(), AboutActivity::class.java)
        startActivity(aboutIntent)
    }

    fun openFile() {
        filePicker.pickFile(allowedMimeTypes)
    }

    private fun openFile(inputFile: File, inputMimeType: MimeType) {
        Logger.d("Opening file '${inputFile.name}' to display metadata...")

        this.inputFile = inputFile
        this.inputMimeType = inputMimeType

        loadMetadata()
    }

    private fun loadMetadata() {
        val inputFile = inputFile ?: return
        val inputMimeType = inputMimeType ?: return

        GlobalScope.launch(Dispatchers.Main) {
            val inputMetadata = metadataHandler.loadMetadata(inputMimeType, inputFile)
            if (inputMetadata == null) {
                Logger.d("Couldn't load metadata for file '${inputFile.name}'. " +
                        "Maybe the file extension '${inputFile.extension}' is not supported.")
                // If the file couldn't be read, immediately close it.
                closeFile()
                // TODO("Show a message that no metadata could be found.")
                return@launch
            }
            metadata.value = inputMetadata
                    .copy(title = inputMetadata.title ?: Text(inputFile.nameWithoutExtension))
        }
    }

    fun closeFile() {
        metadata.value = null

        inputFile = null
        inputMimeType = null

        // Delete temp file.
        outputFile?.delete()
        outputFile = null
    }

    fun removeMetadata() {
        if (outputFile != null) {
            shareOutputFile()
            return
        }

        val inputFile = inputFile ?: return
        val inputMimeType = inputMimeType ?: return


        val sharedImagesDir = applicationContext.filesDir
                .resolve("images")
                .apply { mkdirs() }
        val outputFile = sharedImagesDir.resolve(
                "${inputFile.nameWithoutExtension}_no_metadata.${inputFile.extension}")
                .apply {
                    createNewFile()
                    deleteOnExit()
                }
        this.outputFile = outputFile

        GlobalScope.launch(Dispatchers.Main) {
            metadataHandler.removeMetadata(inputMimeType, inputFile, outputFile)
            shareOutputFile()
        }
    }

    private fun shareOutputFile() {
        val inputFile = inputFile ?: return
        val outputFile = outputFile ?: return

        val fileUri =
                try {
                    Logger.d("Sharing '${outputFile.absolutePath}'.")
                    FileProvider.getUriForFile(
                            applicationContext,
                            "${BuildConfig.APPLICATION_ID}.files",
                            outputFile)
                } catch (e: IllegalArgumentException) {
                    Logger.e(e, "The selected file can't be shared: ${outputFile.name}")
                    null
                }

        if (fileUri != null) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            sendIntent.setDataAndType(fileUri, applicationContext.contentResolver.getType(fileUri))

            val chooserIntent = Intent.createChooser(
                    sendIntent,
                    applicationContext.getString(
                            R.string.title_intent_chooser_share_without_metadata,
                            inputFile.name))

            startActivity(chooserIntent)
        }
    }

    companion object {
        val ROOT_METADATA_HANDLER = CombiningMetadataHandler(dequeOf(
                NopMetadataHandler() // For testing purposes only. TODO Remove after testing.
        ))
    }
}