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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.TestOptions
import com.android.builder.core.DefaultApiVersion
import com.android.builder.core.DefaultProductFlavor
import org.gradle.internal.Cast.uncheckedCast
import java.util.*

plugins {
    androidApplication
    kotlinAndroid
    kotlinAndroidExtensions
    kotlinKapt
    googlePlayPublishing
    fDroidPublishing
    jacocoAndroid
//    spoon
}

val localProperties: Map<String, String> = project
        .rootProject
        .file("local.properties")
        .inputStream()
        .let { stream ->
            Properties().apply { load(stream) }
        }
        .map { (key, value) -> key.toString() to value.toString() }
        .toMap()

android {
    compileSdk = Versions.sdk.compile

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdk = Versions.sdk.min
        targetSdk = Versions.sdk.target

        versionCode = Versions.app.code
        versionName = Versions.app.fullName

        // The default test runner for Android instrumentation tests.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseSigning by signingConfigs.creating {
        storeFile = rootProject.file("secret/MetadataRemover.jks")
                .takeIf(File::exists)
        storePassword = localProperties["keystore.password"]
                ?: System.getenv("keystore_password")
        keyAlias = localProperties["keystore.alias.googleplay.name"]
                ?: System.getenv("keystore_alias_googleplay_name")
        keyPassword = localProperties["keystore.alias.googleplay.password"]
                ?: System.getenv("keystore_alias_googleplay_password")
    }

    buildTypes {

        // Debug builds
        val debug by existing {
            // Append "DEBUG" to all debug build versions
            versionNameSuffix = " (debug)"
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
        }
    }

    // Enable Android data binding.
    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = Versions.jvm
        targetCompatibility = Versions.jvm
    }

    // Always show the result of every unit test, even if it passes.
    testOptions.unitTests.all {
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
        }
    }
}

junitJacoco {
    includeInstrumentationCoverageInMergedReport = false
}

val jacocoTestReport by tasks.registering {
    group = "reporting"
    dependsOn("jacocoTestReportDebug", "jacocoTestReportRelease")
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

play {
    serviceAccountCredentials = rootProject
            .file("secret/api-7281121051860956110-977812-57e7308358e6.json")
            .also { file ->
                check(file.exists()) {
                    "Could not find Google Play credentials."
                }
            }

    track = "internal"
    resolutionStrategy = "fail"
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
