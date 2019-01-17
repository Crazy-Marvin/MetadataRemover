package rocks.poopjournal.metadataremover

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

/**
 * jCenter repository
 */
val RepositoryHandler.jCenter
    get() = jcenter()

/**
 * Gradle plugin repository
 */
val RepositoryHandler.gradlePlugins
    get() = maven("https://plugins.gradle.org/m2/")

/**
 * Maven Central repository
 */
val RepositoryHandler.mavenCentral
    get() = mavenCentral()

/**
 * Google Maven repository
 */
val RepositoryHandler.google
    get() = google()

/**
 * JitPack Maven repository
 */
val RepositoryHandler.jitPack
    get() = maven("https://jitpack.io")

/**
 * Adobe Maven repository
 */
val RepositoryHandler.adobe
    get() = maven("https://repo.adobe.com/nexus/content/repositories/public/")