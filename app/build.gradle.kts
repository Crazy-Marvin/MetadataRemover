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

/*
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.TestOptions
import com.android.builder.core.DefaultApiVersion
import com.android.builder.core.DefaultProductFlavor
import org.gradle.internal.Cast.uncheckedCast
*/

plugins {
    androidApplication
    kotlinAndroid
    kotlinKapt
    daggerHilt
    //googlePlayPublishing
    //fDroidPublishing
    //jacocoAndroid
    //githubRelease
    //canIDropJetifier
}



val secretProperties = rootProject
        .file("secret/secret.properties")
        .asProperties()
        .asStringMap()


/*
override var version: Version
    set(value) {
        super.setVersion(value)
        field = value
    }


version = Versions.app
 */
android {
    compileSdk = Versions.Sdk.compile

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdk = Versions.Sdk.min
        targetSdk = Versions.Sdk.target

        versionCode = 20010
        versionName = "2.0.1"

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

        // The default test runner for Android instrumentation tests.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseSigning by signingConfigs.creating {
        storeFile = rootProject.file("secret/MetadataRemover.jks")
                .takeIf(File::exists)
        storePassword = secretProperties["keystore.password"]
        keyAlias = secretProperties["keystore.alias.googleplay.name"]
        keyPassword = secretProperties["keystore.alias.googleplay.password"]
    }

    buildTypes {

        // Debug builds
        val debug by existing {
            // Append "DEBUG" to all debug build versions
            versionNameSuffix = "-$latestCommitHash (debug)"
            isDebuggable = true
            isTestCoverageEnabled = true
        }

        // Production builds
        val release by existing {
            postprocessing {
                isRemoveUnusedCode = false
                isRemoveUnusedResources = false
                isObfuscate = false
                isOptimizeCode = false
                proguardFiles += getDefaultProguardFile("proguard-android.txt")
                proguardFiles += file("proguard-rules.pro")
            }
            signingConfig = releaseSigning
            isCrunchPngs = false
        }
    }


    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = Versions.jvm
        targetCompatibility = Versions.jvm
    }

    // Always show the result of every unit test, even if it passes.
    /*
    testOptions.unitTests.all {
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
        }
    }*/

    lintOptions {
        ignore("MissingTranslation")
    }
    namespace = "rocks.poopjournal.metadataremover"
}

kapt {
    javacOptions {
        /**
         * Increase the max error count that is displayed for annotation processors using kapt.
         * This is needed as errors in Annotation processors
         * (which will fail Android Data Binding) are not shown.
         * Refer to: https://github.com/google/dagger/issues/306
         */
        option("-Xmaxerrs", 500)
    }
}

//dependencies(Dependencies.app)

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Metadata Extractors
    implementation("com.drewnoakes:metadata-extractor:2.11.0")
    implementation("androidx.exifinterface:exifinterface:1.0.0")
    implementation("ar.com.hjg:pngj:2.1.0") {
        // Explicitly exclude the AWT library, as that is not available on Android.
        exclude(group = "java.awt.image")
    }

    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    //CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    //About libraries
    implementation("com.mikepenz:aboutlibraries:6.2.0-rc01")

    //Dagger-hilt
    implementation ("com.google.dagger:hilt-android:2.51.1")
    kapt ("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")

}


/*
play {
    serviceAccountCredentials = rootProject.file("secret/api-7281121051860956110-977812-57e7308358e6.json")
    track = "internal"
    releaseStatus = "draft"
    defaultToAppBundles = true
    resolutionStrategy = "fail"
}*/

/*
githubRelease {
    setToken(System.getenv("GITHUB_TOKEN") ?: secretProperties["github.credentials.token"])
    setOwner("Crazy-Marvin")
    setRepo("MetadataRemover")
    setTagName("v$version")
    setTargetCommitish("master")
    setReleaseName("Release $version")
    val commitHashLinePrefix = Regex("^(?<hash>[0-9a-f]{1,40}) (?<message>.*)$", RegexOption.IGNORE_CASE)
    setBody(provider {
        "## Full changelog\n${
        provider(changelog())
                .get()
                .lines()
                .joinToString(separator = "\n") { change ->
                    change.replace(commitHashLinePrefix, "- \${hash} \${message}")
                }
        }"
    })
    // Don't publish releases directly.
    // Instead create a draft and let maintainers approve it.
    setDraft(true)
    setPrerelease(version.build != 0)
    val releaseAssets = fileTree(buildDir)
            .apply {
                include("outputs/**/release/**.aab", "outputs/**/release/**.apk")
            }
    setReleaseAssets(releaseAssets)
    setOverwrite(true)

    afterEvaluate {
        val githubRelease by tasks.getting {
            val bundleRelease by tasks.getting
            val assembleRelease by tasks.getting {
                shouldRunAfter(bundleRelease)
            }
            dependsOn(bundleRelease, assembleRelease)
        }
    }
}
*/


// Lint F-Droid resources.
//tasks["lint"].dependsOn("fdroidLint")

val printVersionName by tasks.creating {
    doLast {
        println(version)
    }
}

jacoco {
    toolVersion = "0.8.3"
}

repositories(Repositories.app)
dependencies(Dependencies.app)


var BaseExtension.compileSdk: Int
    get() = compileSdkVersion.removePrefix("android-").toInt()
    set(value) = compileSdkVersion(value)

var DefaultProductFlavor.minSdk: Int
    get() = minSdkVersion!!.apiLevel
    set(value) {
        minSdkVersion = DefaultApiVersion(value)
    }

var DefaultProductFlavor.targetSdk: Int
    get() = targetSdkVersion!!.apiLevel
    set(value) {
        targetSdkVersion = DefaultApiVersion(value)
    }

fun TestOptions.UnitTestOptions.all(action: Test.() -> Unit) =
        all(uncheckedCast(closureOf(action)))
