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
    get()  {
    val output = ByteArrayOutputStream()

    providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get()

    return output.toString().trim()
    }
