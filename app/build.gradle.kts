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
import groovy.lang.Closure

plugins {
    androidApplication
    kotlinAndroid
    kotlinAndroidExtensions
    kotlinKapt
    googlePlayPublishing
    onNonCiBuild { fDroidPublishing }
    jacocoAndroid
//    spoon
}

android {
    compileSdk = Versions.sdk.compile

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdk = Versions.sdk.min
        targetSdk = Versions.sdk.target

        versionCode = Versions.app.code
        versionName = Versions.app.shortName

        // The default test runner for Android instrumentation tests.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    includeInstrumentationCoverageInMergedReport = true
}

val jacocoTestReport by tasks.registering {
    group = "reporting"
    dependsOn("jacocoTestReportDebug", "jacocoTestReportRelease")
}

//spoon {
//    // Disable animations.
//    noAnimations = true
//
//    // ADB timeout in minutes.
//    adbTimeout = 10
//
//    // Grant all runtime permissions during installation.
//    grantAll = true
//
//    // Execute tests in parallel on 10 shards.
//    shard = true
//    numShards = 10
//}

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
    val credentialsFile = project
            .localProperties["googleplay.credentials"]
            ?.let(::file)

    if (credentialsFile == null) {
        System.err.println("Could not find Google Play credentials.")
        commit = false
    } else {
        serviceAccountCredentials = credentialsFile
    }

    track = "internal"
    resolutionStrategy = "fail"

    if (isCiBuild) {
        commit = false
    }
}

repositories(Repositories.app)
dependencies(Dependencies.app)


inline var BaseExtension.compileSdk: Int
    get() = compileSdkVersion.removePrefix("android-").toInt()
    set(value) = compileSdkVersion(value)

inline var DefaultProductFlavor.minSdk: Int
    get() = minSdkVersion!!.apiLevel
    set(value) {
        minSdkVersion = DefaultApiVersion(value)
    }

inline var DefaultProductFlavor.targetSdk: Int
    get() = targetSdkVersion!!.apiLevel
    set(value) {
        targetSdkVersion = DefaultApiVersion(value)
    }

@Suppress("unchecked_cast")
fun TestOptions.UnitTestOptions.all(action: Test.() -> Unit) =
        all(closureOf(action) as Closure<Test>)
