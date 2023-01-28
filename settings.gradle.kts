rootProject.name = "gradle-plugin"
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
//        id("org.gradle.kotlin.kotlin-dsl") version "4.0.2"
    }
}
