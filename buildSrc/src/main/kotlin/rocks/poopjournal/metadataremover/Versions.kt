import rocks.poopjournal.metadataremover.Version

object Versions {

    // Gradle plugin versions
    const val gradlePluginVersion = "3.2.1"
    const val kotlinPluginVersion = "1.3.11"

    // App version
    // Note: The app version should always be determined based on semantic versioning rules http://semver.org/
    val app = Version(
            major = 0,
            minor = 1,
            patch = 0,
            build = 0
    )

    // SDK/tools versions
    const val minSdkVersion = 19
    const val targetSdkVersion = 28
    const val compileSdkVersion = 28
}