import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

val Project.localProperties: Properties
    get() {
        return Properties()
                .apply {
                    File(rootDir, "local.properties")
                            .takeIf(File::exists)
                            ?.inputStream()
                            ?.use { load(it) }
                }
    }


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
