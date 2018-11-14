package rocks.poopjournal.metadataremover.model.resources

@Suppress("UNUSED")
sealed class Resource<in T : Any> {
    class Empty<in T : Any> : Resource<T>()
    class Loading<in T : Any> : Resource<T>()
    data class Success<T : Any>(val value: T) : Resource<T>()
}