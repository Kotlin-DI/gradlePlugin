import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val commonVersion: String by project
val iocVersion: String by project
val kspVersion: String by project

plugins {
    kotlin("jvm")
    java
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("org.jetbrains.dokka") version "1.7.20"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "io.github.Kotlin-DI"
version = "0.0.2"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    implementation("com.github.Kotlin-DI:common:$commonVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kotlinVersion-$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
            jvmTarget = "17"
        }
        dependsOn("ktlintFormat")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            from(components["java"])
        }
    }
}

pluginBundle {
    website = "https://Kotlin-DI.github.io"
    vcsUrl = "https://github.com/Kotlin-DI/gradle-plugin.git"
    tags = listOf("dependency-injection", "annotation-processing")
}

gradlePlugin {

    plugins {
        create("plugin") {
            id = "io.github.Kotlin-DI.plugin"
            displayName = "Kotlin DI plugin"
            description = "Applies all of the required libraries"
            implementationClass = "com.github.kotlin_di.gradle_plugin.DependencyPlugin"
        }
    }
}
