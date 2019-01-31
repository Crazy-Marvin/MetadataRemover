import groovy.lang.Closure

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

plugins {
    // Android app plugin
    id("com.android.application")
    // Kotlin plugin
    id("kotlin-android")
    // Kotlin convenience extensions for Android plugin
    id("kotlin-android-extensions")
    // Kotlin annotation processor plugin
    id("kotlin-kapt")
    // Google Play Store Publishing
//    id("com.github.triplet.play") version "2.1.0"
    // F-Droid Publishing
//    id("org.openintents.fdroid") version "0.2"
    // JaCoCo test coverage reports
    id("com.vanniktech.android.junit.jacoco") version "0.13.0"
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    defaultConfig {
        applicationId = "rocks.poopjournal.metadataremover"

        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)

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

val jacocoTestReport by tasks.creating {
    group = "reporting"
    dependsOn("jacocoTestReportDebug", "jacocoTestReportRelease")
}


//play {
//    val credentialsFile = project
//            .ext
//            .properties["googleplay.credentials"]
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

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinPluginVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinPluginVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0")

    // Material Design
    implementation("com.google.android.material:material:1.1.0-alpha03")

    // AppCompat support
    implementation("androidx.appcompat:appcompat:1.1.0-alpha01")
    implementation("androidx.core:core-ktx:1.1.0-alpha04")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha02")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.exifinterface:exifinterface:1.0.0")
    // Architecture components
    implementation("androidx.lifecycle:lifecycle-extensions:2.1.0-alpha02")
    kapt("androidx.lifecycle:lifecycle-compiler:2.1.0-alpha02")

    // Timber logging util
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Local unit test libraries
    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${Versions.kotlinPluginVersion}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinPluginVersion}")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")

    // Okio I/O
    implementation("com.squareup.okio:okio:2.2.1")
    implementation("com.github.bumptech.glide:glide:4.8.0")
    //implementation("org.apache.commons:commons-imaging:1.0-R1401825")
    //implementation("org.apache.tika:tika-core:1.17")
    //implementation("org.apache.tika:tika-parsers:1.17")
    implementation("com.drewnoakes:metadata-extractor:2.11.0")
    // https://mvnrepository.com/artifact/ar.com.hjg/pngj
    implementation("ar.com.hjg:pngj:2.1.0")

    //implementation("com.getkeepsafe.taptargetview:taptargetview:1.9.1")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    implementation("com.github.zagum:Android-ExpandIcon:1.2.1")
    implementation("com.mikepenz:aboutlibraries:6.2.0-rc01")
}
