import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

fun File.asProperties(): Properties {
    return Properties()
            .apply {
                this@asProperties.takeIf(File::exists)
                        ?.inputStream()
                        ?.use { load(it) }
            }
}

fun Project.properties(path: Any): Properties = file(path).asProperties()

val Project.localProperties: Properties
    get() = properties("local.properties")

fun Properties.asStringMap() =
        map { (key, value) -> key.toString() to value.toString() }.toMap()


val Project.latestCommitHash: String
    get() {
        return ByteArrayOutputStream()
                .also { stream ->
                    exec {
                        commandLine = listOf(
                                "git",
                                "rev-parse",
                                "--short",
                                "HEAD"
                        )
                        standardOutput = stream
                    }
                }
                .toString()
                .trim()
    }
