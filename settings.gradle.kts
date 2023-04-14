rootProject.name = "gradlePlugin"
pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://jitpack.io")
        mavenLocal()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.dokka") version kotlinVersion
    }
}