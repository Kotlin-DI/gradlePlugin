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
}
