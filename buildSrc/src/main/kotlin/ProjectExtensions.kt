import org.gradle.api.Project
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
