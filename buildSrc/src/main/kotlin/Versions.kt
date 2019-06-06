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

import org.gradle.api.JavaVersion

object Versions {

    // App version
    // Note: The app version should always be determined based on semantic versioning rules http://semver.org/
    val app = Version(
            major = 1,
            patch = 1
    )

    val sdk = Sdk

    object Sdk {
        const val compile = 28
        const val min = 19
        const val target = 28
    }

    val jvm = JavaVersion.VERSION_1_8

    // Plugins
    const val androidBuildTools = "3.3.2"
    const val androidJetifierProcessor = "1.0.0-beta04"

    // Kotlin
    const val kotlin = "1.3.30"

    // Kotlin coroutines
    const val kotlinxCoroutines = "1.1.0"

    // Google Material design
    const val googleAndroidMaterial = "1.0.0"

    // Support libraries
    const val androidxCoreKtx = "1.0.1"
    const val androidxAppCompat = "1.0.2"
    const val androidxConstraintLayout = "1.1.3"
    const val androidxRecyclerView = "1.0.0"
    const val androidxCardView = "1.0.0"
    const val androidxAnnotation = "1.0.1"
    const val androidxExifInterface = "1.0.0"
    const val androidxCollectionKtx = "1.0.0"

    // Architecture components
    const val androidxLifecycle = "2.0.0"
    const val androidxArchCoreTesting = "2.0.0"

    // Logging
    const val timber = "4.7.1"

    // Testing
    const val jUnit = "5.3.2"
    const val androidxTestRunner = "1.1.1"
    const val androidxTestExtJUnit = "1.1.0"
    const val androidxTestEspressoCore = "3.1.1"
    const val spoon = "2.0.0-SNAPSHOT"
    const val spoonPlugin = "1.5.0"
    const val jacocoAndroidPlugin = "0.1.4"
    const val kluent = "1.46"

    // Data
    const val okio = "2.2.1"
    const val glide = "4.9.0"
    const val apacheCommonsImaging = "1.0-R1401825"
    const val tika = "1.17"
    const val metadataExtractor = "2.11.0"
    const val pngJ = "2.1.0"

    // User interface
    const val tapTargetView = "1.9.1"
    const val slidingUpPanel = "3.4.0"
    const val expandIcon = "1.2.1"
    const val aboutLibraries = "6.2.0-rc01"

    // Publishing
    const val googlePlayPublishingPlugin = "2.1.0"
    const val fDroidPublishingPlugin = "0.2"
    const val githubReleasePlugin = "2.2.8"
    const val circleImageView = "3.0.0"

    // Utilities
    const val canIDropJetifierPlugin = "0.4"

}