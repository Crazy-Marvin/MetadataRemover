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

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude

// Android
inline val DependencyHandlerScope.androidBuildToolsPlugin
    // https://mvnrepository.com/artifact/com.android.tools.build/gradle
    get() = classpath("com.android.tools.build", "gradle", Versions.androidBuildTools)

inline val DependencyHandlerScope.androidJetifierProcessor
    // https://mvnrepository.com/artifact/com.android.tools.build.jetifier/jetifier-processor
    get() = classpath("com.android.tools.build.jetifier", "jetifier-processor", Versions.androidJetifierProcessor)


// Kotlin
inline val DependencyHandlerScope.kotlinPlugin
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
    get() = classpath(kotlin("gradle-plugin", Versions.kotlin))

inline val DependencyHandlerScope.kotlinStandardLibraryJdk8
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    get() = implementation(kotlin("stdlib-jdk8", Versions.kotlin))

inline val DependencyHandlerScope.kotlinReflect
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    get() = implementation(kotlin("reflect", Versions.kotlin))


// Kotlin coroutines
inline val DependencyHandlerScope.kotlinxCoroutinesCore
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    get() = implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Versions.kotlinxCoroutines)

inline val DependencyHandlerScope.kotlinxCoroutinesAndroid
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-android
    get() = implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Versions.kotlinxCoroutines)


// Google Material design
inline val DependencyHandlerScope.googleAndroidMaterial
    // https://mvnrepository.com/artifact/com.google.android.material/material
    get() = implementation("com.google.android.material", "material", Versions.googleAndroidMaterial)


// Support libraries
inline val DependencyHandlerScope.androidxDatabinding
    // https://mvnrepository.com/artifact/androidx.databinding/databinding-compiler
    get() = annotationProcessor("androidx.databinding", "databinding-compiler", Versions.androidBuildTools)

inline val DependencyHandlerScope.androidxAppCompat
    // https://mvnrepository.com/artifact/androidx.appcompat/appcompat
    get() = implementation("androidx.appcompat", "appcompat", Versions.androidxAppCompat)

inline val DependencyHandlerScope.androidxCoreKtx
    // https://mvnrepository.com/artifact/androidx.core/core-ktx
    get() = implementation("androidx.core", "core-ktx", Versions.androidxCoreKtx)

inline val DependencyHandlerScope.androidxConstraintLayout
    // https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
    get() = implementation("androidx.constraintlayout", "constraintlayout", Versions.androidxConstraintLayout)

inline val DependencyHandlerScope.androidxRecyclerView
    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
    get() = implementation("androidx.recyclerview", "recyclerview", Versions.androidxRecyclerView)

inline val DependencyHandlerScope.androidxCardView
    // https://mvnrepository.com/artifact/androidx.cardview/cardview
    get() = implementation("androidx.cardview", "cardview", Versions.androidxCardView)

inline val DependencyHandlerScope.androidxAnnotation
    // https://mvnrepository.com/artifact/androidx.annotation/annotation
    get() = implementation("androidx.annotation", "annotation", Versions.androidxAnnotation)

inline val DependencyHandlerScope.androidxExifInterface
    // https://mvnrepository.com/artifact/androidx.exifinterface/exifinterface
    get() = implementation("androidx.exifinterface", "exifinterface", Versions.androidxExifInterface)

inline val DependencyHandlerScope.androidxCollectionKtx
    // https://mvnrepository.com/artifact/androidx.collection/collection-ktx
    get() = implementation("androidx.collection", "collection-ktx", Versions.androidxCollectionKtx)


// Architecture components
inline val DependencyHandlerScope.androidxLifecycleExtensions
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-extensions
    get() = implementation("androidx.lifecycle", "lifecycle-extensions", Versions.androidxLifecycle)

inline val DependencyHandlerScope.androidxLifecycleViewModelKtx
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-ktx
    get() = implementation("androidx.lifecycle", "lifecycle-viewmodel-ktx", Versions.androidxLifecycle)

inline val DependencyHandlerScope.androidxLifecycleRuntime
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-runtime
    get() = implementation("androidx.lifecycle", "lifecycle-runtime", Versions.androidxLifecycle)

inline val DependencyHandlerScope.androidxLifecycleCompiler
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-compiler
    get() = kapt("androidx.lifecycle", "lifecycle-compiler", Versions.androidxLifecycle)

inline val DependencyHandlerScope.androidxArchCoreTesting
    // https://mvnrepository.com/artifact/androidx.arch.core/core-testing
    get() = testImplementation("androidx.arch.core", "core-testing", Versions.androidxArchCoreTesting)


// Logging
inline val DependencyHandlerScope.timber
    // https://mvnrepository.com/artifact/com.jakewharton.timber/timber
    get() = implementation("com.jakewharton.timber", "timber", Versions.timber)


// Testing
inline val DependencyHandlerScope.jUnit
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    get() = testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.jUnit)

inline val DependencyHandlerScope.kotlinTest
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-test
    get() = testImplementation(kotlin("test", Versions.kotlin))

inline val DependencyHandlerScope.kotlinTestJUnit
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-test
    get() = testImplementation(kotlin("test-junit", Versions.kotlin))

inline val DependencyHandlerScope.androidxTestRunner
    // https://mvnrepository.com/artifact/androidx.test/runner
    get() = androidTestImplementation("androidx.test", "runner", Versions.androidxTestRunner)

inline val DependencyHandlerScope.androidxTestExtJUnit
    // https://mvnrepository.com/artifact/androidx.test.ext/junit
    get() = androidTestImplementation("androidx.test.ext", "junit", Versions.androidxTestExtJUnit)

inline val DependencyHandlerScope.androidxEspressoCore
    // https://mvnrepository.com/artifact/androidx.test.espresso/espresso-core
    get() = androidTestImplementation("androidx.test.espresso", "espresso-core", Versions.androidxTestEspressoCore) {
        exclude("androidx.annotation", "annotation")
    }

inline val DependencyHandlerScope.kluentAndroid
    // https://mvnrepository.com/artifact/org.amshove.kluent/kluent-android
    get() = androidTestImplementation("org.amshove.kluent", "kluent-android", Versions.kluent)

inline val DependencyHandlerScope.spoonClient
    // https://mvnrepository.com/artifact/com.squareup.spoon/spoon-client
    get() = androidTestImplementation("com.squareup.spoon", "spoon-client", Versions.spoon)


// Data
inline val DependencyHandlerScope.okio
    // https://mvnrepository.com/artifact/com.squareup.okio/okio
    get() = implementation("com.squareup.okio", "okio", Versions.okio)

inline val DependencyHandlerScope.glide
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    get() = implementation("com.github.bumptech.glide", "glide", Versions.glide)

inline val DependencyHandlerScope.glideCompiler
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/compiler
    get() = kapt("com.github.bumptech.glide", "compiler", Versions.glide)

inline val DependencyHandlerScope.commonsImaging
    // https://mvnrepository.com/artifact/org.apache.commons/commons-imaging
    get() = implementation("org.apache.commons", "commons-imaging", Versions.apacheCommonsImaging)

inline val DependencyHandlerScope.tikaCore
    // https://mvnrepository.com/artifact/org.apache.tika/tika-core
    get() = implementation("org.apache.tika", "tika-core", Versions.tika)

inline val DependencyHandlerScope.tikaParsers
    // https://mvnrepository.com/artifact/org.apache.tika/tika-parsers
    get() = implementation("org.apache.tika", "tika-parsers", Versions.tika)

inline val DependencyHandlerScope.metadataExtractor
    // https://mvnrepository.com/artifact/com.drewnoakes/metadata-extractor
    get() = implementation("com.drewnoakes", "metadata-extractor", Versions.metadataExtractor)

inline val DependencyHandlerScope.pngJ
    // https://mvnrepository.com/artifact/ar.com.hjg/pngj
    get() = implementation("ar.com.hjg", "pngj", Versions.pngJ) {
        // Explicitly exclude the AWT library,
        // as that is not available on Android.
        exclude(group = "java.awt.image")
    }


// User interface
inline val DependencyHandlerScope.tapTargetView
    // https://mvnrepository.com/artifact/com.getkeepsafe.taptargetview/taptargetview
    get() = implementation("com.getkeepsafe.taptargetview", "taptargetview", Versions.tapTargetView)

inline val DependencyHandlerScope.slidingUpPanel
    // https://mvnrepository.com/artifact/com.sothree.slidinguppanel/library
    get() = implementation("com.sothree.slidinguppanel", "library", Versions.slidingUpPanel)

inline val DependencyHandlerScope.expandIcon
    // https://mvnrepository.com/artifact/com.github.zagum/Android-ExpandIcon
    get() = implementation("com.github.zagum", "Android-ExpandIcon", Versions.expandIcon)

inline val DependencyHandlerScope.aboutLibraries
    // https://mvnrepository.com/artifact/com.mikepenz/aboutlibraries
    get() = implementation("com.mikepenz", "aboutlibraries", Versions.aboutLibraries)

inline val DependencyHandlerScope.circleImageView
    // https://mvnrepository.com/artifact/de.hdodenhof/circleimageview
    get() = implementation("de.hdodenhof", "circleimageview", Versions.circleImageView)
