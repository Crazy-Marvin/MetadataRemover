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
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.mikepenz.aboutlibraries.plugin")
    id("io.sentry.kotlin.compiler.gradle") version "6.3.0"
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
    compileSdk = 36

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdk = 26
        targetSdk = 36

        versionCode = 30000
        versionName = "3.0.0"

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
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = releaseSigning
            isCrunchPngs = false
        }
    }


    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
        }
    }

    androidResources{
        generateLocaleConfig = true
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
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



//dependencies(Dependencies.app)

dependencies {

    implementation("androidx.core:core-ktx:1.17.0")
    implementation ("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    //Metadata Extractors
    implementation("com.drewnoakes:metadata-extractor:2.19.0")
    implementation("androidx.exifinterface:exifinterface:1.4.2")
    implementation("ar.com.hjg:pngj:2.1.0") {
        // Explicitly exclude the AWT library, as that is not available on Android.
        exclude(group = "java.awt.image")
    }

    //Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.androidx.activity)
    ksp("com.github.bumptech.glide:compiler:5.0.5")

    //CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    //About libraries
    implementation("com.mikepenz:aboutlibraries:13.1.0")

    //Dagger-hilt
    implementation ("com.google.dagger:hilt-android:2.58")
    ksp ("com.google.dagger:hilt-android-compiler:2.58")
    ksp ("androidx.hilt:hilt-compiler:1.2.0")

    //ffmpeg
    implementation ("com.arthenica:smart-exception-java:0.2.1")
    implementation( files("../libs/ffmpeg-kit.aar"))

    //Apache POI
    implementation ("org.apache.poi:poi:5.5.1")
    implementation ("org.apache.poi:poi-ooxml:5.5.1")
    implementation ("org.apache.poi:poi-scratchpad:5.5.1")
    implementation ("org.apache.odftoolkit:simple-odf:0.8.2-incubating")
    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")

    //sentry
    implementation("io.sentry:sentry-android:8.32.0")

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
