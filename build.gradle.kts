import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val commonVersion: String by project
val iocVersion: String by project
val kspVersion: String by project

plugins {
    kotlin("jvm")
    java
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("org.jetbrains.dokka")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.1.0"
    id("me.qoomon.git-versioning") version "6.3.0"
}

group = "com.github.Kotlin-DI"
version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    refs {
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }

    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

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
//    implementation("com.github.Kotlin-DI:common:$commonVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kotlinVersion-$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}

ktlint {
    version.set("0.48.2")
    outputToConsole.set(true)
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

gradlePlugin {
    website.set("https://Kotlin-DI.github.io")
    vcsUrl.set("https://github.com/Kotlin-DI/gradlePlugin.git")

    plugins {
        create("plugin") {
            id = "$group.${project.name}"
            displayName = "Kotlin DI plugin"
            description = "Applies all of the required libraries"
            tags.set(listOf("dependency-injection", "annotation-processing"))
            implementationClass = "com.github.kotlinDI.gradlePlugin.DependencyPlugin"
        }
    }
}