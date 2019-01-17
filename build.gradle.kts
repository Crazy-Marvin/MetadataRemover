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

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    // Build script repositories being used by the dependencies (defined in `buildscript.dependencies`).
    repositories {
        // Default jCenter repository
        jcenter()
        // Gradle plugin repository
        maven("https://plugins.gradle.org/m2/")
        // Google Maven repository
        google()
    }

    // Build script dependencies.
    // Note: Do NOT place your application dependencies here.
    // They belong in the individual module `build.gradle.kts` files.
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradlePluginVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinPluginVersion}")
    }
}

allprojects {
    // Repositories for all projects' app dependencies.
    repositories {
        // Default jCenter repository
        jcenter()
        // Maven Central repository for libraries that aren't on jCenter
        mavenCentral()
        // Google Maven repository
        google()
        // JitPack repository
        // TODO Do we need this? If not then remove before release.
        maven("https://jitpack.io")
        // Adobe repository
        maven("https://repo.adobe.com/nexus/content/repositories/public/")
    }
}

tasks {
    /**
     * Task to clean the build directory.
     *
     * This might be useful when a build fails and one doesn't want the next build to depend
     * on possibly broken cached build files.
     */

    create("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}
