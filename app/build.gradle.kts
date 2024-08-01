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
    ksp
    daggerHilt
    id("com.mikepenz.aboutlibraries.plugin")
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

        versionCode = 20030
        versionName = "2.0.3"

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

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
    }

    aboutLibraries {
        // Remove the "generated" timestamp to allow for reproducible builds
        excludeFields = arrayOf("generated")
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

    implementation("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Metadata Extractors
    implementation("com.drewnoakes:metadata-extractor:2.19.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("ar.com.hjg:pngj:2.1.0") {
        // Explicitly exclude the AWT library, as that is not available on Android.
        exclude(group = "java.awt.image")
    }

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    //CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    //About libraries
    implementation("com.mikepenz:aboutlibraries:11.2.2")

    //Dagger-hilt
    implementation ("com.google.dagger:hilt-android:2.51.1")
    ksp ("com.google.dagger:hilt-android-compiler:2.51.1")
    ksp ("androidx.hilt:hilt-compiler:1.2.0")

    //ffmpeg
    implementation ("com.arthenica:smart-exception-java:0.2.1")
    implementation( files("../libs/ffmpeg-kit.aar"))

    //Apache POI
    implementation ("org.apache.poi:poi:5.3.0")
    implementation ("org.apache.poi:poi-ooxml:5.3.0")
    implementation ("org.apache.poi:poi-scratchpad:5.2.2")
    implementation ("org.apache.odftoolkit:simple-odf:0.8.2-incubating")
    implementation ("com.itextpdf:itext7-core:8.0.5")
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
