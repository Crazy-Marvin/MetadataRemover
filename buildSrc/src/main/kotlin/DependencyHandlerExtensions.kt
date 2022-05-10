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

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.kotlin

const val CLASSPATH_CONFIGURATION = ScriptHandler.CLASSPATH_CONFIGURATION
const val IMPLEMENTATION_CONFIGURATION = "implementation"
const val TEST_IMPLEMENTATION_CONFIGURATION = "testImplementation"
const val ANDROID_TEST_IMPLEMENTATION_CONFIGURATION = "androidTestImplementation"
const val KAPT_CONFIGURATION = "kapt"
const val ANNOTATION_PROCESSOR_CONFIGURATION = "annotationProcessor"

fun DependencyHandlerScope.classpath(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = CLASSPATH_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.implementation(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = IMPLEMENTATION_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.testImplementation(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = TEST_IMPLEMENTATION_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.androidTestImplementation(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = ANDROID_TEST_IMPLEMENTATION_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.kapt(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = KAPT_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.annotationProcessor(
        group: String,
        name: String,
        version: String? = null,
        configuration: String? = null,
        classifier: String? = null,
        ext: String? = null,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = ANNOTATION_PROCESSOR_CONFIGURATION(
        group,
        name,
        version,
        configuration,
        classifier,
        ext,
        dependencyConfiguration
)

fun DependencyHandlerScope.classpath(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = CLASSPATH_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandlerScope.implementation(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = IMPLEMENTATION_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandlerScope.testImplementation(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = TEST_IMPLEMENTATION_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandlerScope.androidTestImplementation(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = ANDROID_TEST_IMPLEMENTATION_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandlerScope.kapt(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = KAPT_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandlerScope.annotationProcessor(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit = {}
): ExternalModuleDependency = ANNOTATION_PROCESSOR_CONFIGURATION(
        dependencyNotation,
        dependencyConfiguration
)

fun DependencyHandler.kotlin(module: String, version: String? = null): String {
    val dependencyNotation: Any = kotlin(module, version)
    check(dependencyNotation is String)
    return dependencyNotation
}