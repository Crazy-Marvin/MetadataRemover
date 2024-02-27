package rocks.poopjournal.metadataremover.viewmodel


import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rocks.poopjournal.metadataremover.model.metadata.ClearedFile
import rocks.poopjournal.metadataremover.model.metadata.Metadata
import rocks.poopjournal.metadataremover.model.resources.MediaType
import rocks.poopjournal.metadataremover.model.resources.Resource
import rocks.poopjournal.metadataremover.model.resources.Text
import rocks.poopjournal.metadataremover.util.Logger
import rocks.poopjournal.metadataremover.util.extensions.android.copyFrom
import rocks.poopjournal.metadataremover.util.extensions.nameAttribute
import rocks.poopjournal.metadataremover.util.extensions.nextLong
import rocks.poopjournal.metadataremover.util.extensions.size
import rocks.poopjournal.metadataremover.util.extensions.sizeAttribute
import rocks.poopjournal.metadataremover.viewmodel.usecases.GetDescriptor
import rocks.poopjournal.metadataremover.viewmodel.usecases.GetFileUri
import rocks.poopjournal.metadataremover.viewmodel.usecases.MetadataHandler
import rocks.poopjournal.metadataremover.viewmodel.usecases.SaveImages
import rocks.poopjournal.metadataremover.viewmodel.usecases.SharedImages
import java.io.Closeable
import java.security.SecureRandom
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDescriptor: GetDescriptor,
    private val metadata: MetadataHandler,
    private val sharedImages: SharedImages,
    private val saveImage: SaveImages,
    private val fileProvider: GetFileUri
) : ViewModel() {

    companion object {
        val random = SecureRandom("75rgu86gr59ht86".toByteArray(Charsets.UTF_8))
        const val FILE_NAME_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        const val FILE_NAME_LENGTH = 10
    }

    private val fileViews: ArrayList<FileView?> = arrayListOf()

    private val _outputMetadata = MutableLiveData<List<Resource<Metadata>>>()
    val outputMetadata: LiveData<List<Resource<Metadata>>>
        get() = _outputMetadata

    private val _clearedFile = MutableLiveData<ClearedFile>()
    val clearedFile: LiveData<ClearedFile>
        get() = _clearedFile

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String>
        get() = _toast

    fun getPickedImageUris(uris: List<Uri>){

        uris.forEach {
            getDescriptor.openFile(it, onResult = {file, displayName, mediaType ->
                fileViews.add(FileView(file, displayName, mediaType))
            })
        }

        fileViews.forEachIndexed { index, fileView ->
            viewModelScope.launch {
                loadMetadata(index, fileView!!)
            }
        }
    }

    private suspend fun loadMetadata(index: Int, fileView: FileView) {
        val inputMetadata = metadata.handler.loadMetadata(fileView.mediaType, fileView.original)

        if (inputMetadata == null) {
            Logger.d("Couldn't load metadata for file '${fileView.name}'. " +
                    "Maybe the file extension '${fileView.original.extension}' is not supported.")
            // If the file couldn't be read, immediately close it.

            fileViews[index]?.close()
            fileViews[index] = null

            val currentList = _outputMetadata.value.orEmpty().toMutableList()
            currentList.add(Resource.Empty())
            _outputMetadata.value = currentList
            return
        }

        val fileSystemAttributes = listOfNotNull(
            fileView.original.nameAttribute,
//                fileView.original.lastModifiedAttribute, // TODO Remove this.
            fileView.original.sizeAttribute
        )

        val output = inputMetadata.copy(
            title = inputMetadata.title ?: Text(fileView.nameWithoutExtension),
            attributes = inputMetadata.attributes + fileSystemAttributes
        )


        val currentList = _outputMetadata.value.orEmpty().toMutableList()
        currentList.add(Resource.Success(output))
        _outputMetadata.value = currentList
    }

    fun removeMetadata(index: Int, saveToDevice: Boolean = false) {
        val fileView = fileViews[index] ?: return

        if (fileView.isMetadataRemoved) {
            handleClearedFile(index, saveToDevice)
            return
        }

        viewModelScope.launch {
            metadata.handler.removeMetadata(
                fileView.mediaType,
                fileView.original,
                fileView.output
            )
            fileView.output.setLastModified(random.nextLong(System.currentTimeMillis()))
            handleClearedFile(index, saveToDevice)
        }
    }

    private fun handleClearedFile(index: Int, saveToDevice: Boolean) {
        val fileView = fileViews[index] ?: return
        if (!fileView.isMetadataRemoved) return

        val fileUri = fileProvider.getUri(fileView.output)

        if (fileUri != null) {
            if (saveToDevice){
                saveImage.toDevice(fileView.name, fileUri){message ->
                    _toast.value = message
                }
            } else {
                _clearedFile.value = ClearedFile(fileUri, fileView.name)
            }
        }
    }

    private fun generateRandomFilename(@IntRange(from = 0) length: Int = FILE_NAME_LENGTH): String {
        return StringBuilder(length)
            .apply {
                while (this.length < length) {
                    append(
                        FILE_NAME_ALPHABET[random.nextInt(
                            FILE_NAME_ALPHABET.length)])
                }
            }
            .toString()
    }

    private inner class FileView(
        val descriptor: AssetFileDescriptor,
        val name: String,
        val mediaType: MediaType
    ) : Closeable {

        val original = sharedImages.dir
            .resolve(name)
            .apply {
                createNewFile()
                copyFrom(descriptor)
            }

        val nameWithoutExtension = original.nameWithoutExtension

        val output = sharedImages.dir
            .resolve(generateRandomFilename() +
                    if (original.extension.isNotEmpty()) ".${original.extension.lowercase(Locale.getDefault())}" else "")
            .apply {
                createNewFile()
            }

        val isMetadataRemoved
            get() = output.size != 0L

        override fun close() {
            descriptor.close()

            // Delete temp files.
            original.delete()
            output.delete()
        }
    }
}