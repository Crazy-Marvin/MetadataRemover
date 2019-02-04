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

import groovy.lang.Closure

plugins {
    androidApplication
    kotlinAndroid
    kotlinAndroidExtensions
    kotlinKapt
//    ifNotCi { googlePlayPublishing }
    ifNotCi { fDroidPublishing }
    jacocoAndroid
    spoon
}

android {
    compileSdkVersion(Versions.sdk.compile)

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdkVersion(Versions.sdk.min)
        targetSdkVersion(Versions.sdk.target)

        versionCode = Versions.app.code
        versionName = Versions.app.shortName

        // The default test runner for Android instrumentation tests.
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        // Debug builds
        getByName("debug") {
            // Append "DEBUG" to all debug build versions
            versionNameSuffix = " DEBUG"
            isTestCoverageEnabled = true
        }

        // Production builds
        getByName("release") {
            postprocessing {
                isRemoveUnusedCode = false
                isRemoveUnusedResources = false
                isObfuscate = false
                isOptimizeCode = false
                proguardFile("proguard-rules.pro")
            }
        }
    }

    // Enable Android data binding.
    dataBinding {
        isEnabled = true
    }

    // Always show the result of every unit test, even if it passes.
    testOptions.unitTests.apply {
        val closure = closureOf<Test> {
            testLogging {
                events("passed", "skipped", "failed", "standardOut", "standardError")
            }
        }
        @Suppress("unchecked_cast")
        all(closure as Closure<Test>)
    }
}

junitJacoco {
    includeInstrumentationCoverageInMergedReport = true
}

val jacocoTestReportDebug by tasks.registering
val jacocoTestReportRelease by tasks.registering
val jacocoTestReport by tasks.registering {
    group = "reporting"
    dependsOn(jacocoTestReportDebug, jacocoTestReportRelease)
}

spoon {
    // Disable animations.
    noAnimations = true

    // ADB timeout in minutes.
    adbTimeout = 10

    // Grant all runtime permissions during installation.
    grantAll = true

    // Execute tests in parallel on 10 shards.
    shard = true
    numShards = 10
}

//play {
//    println("local.properties: ${project.localProperties}")
//    val credentialsFile = project
//            .localProperties["googleplay.credentials"]
//            .also { println("googleplay.credentials: $it") }
//            ?.let(::file)
//    if (credentialsFile == null) {
//        System.err.println("Could not find Google Play credentials.")
//        commit = false
//    }
//    else {
//        serviceAccountCredentials = credentialsFile
//    }
//
//    track = "internal"
//    resolutionStrategy = "fail"
//}

repositories(Repositories.app)
dependencies(Dependencies.app)
