package com.github.kotlin_di

import gradle.kotlin.dsl.accessors._8ccd1c8fde1157b819601e1727ca89f0.*
import gradle.kotlin.dsl.accessors._8ccd1c8fde1157b819601e1727ca89f0.kotlin
import org.gradle.kotlin.dsl.*

println("subplugin loaded")

plugins {
    kotlin("jvm")
    `java-library`
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

val include: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(include)
    runtimeClasspath.get().extendsFrom(include)

    testCompileClasspath.get().extendsFrom(include)
    testRuntimeClasspath.get().extendsFrom(include)
}

tasks {

    jar {
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to "${project.group}.${project.name}",
                    "Implementation-Version" to project.version,
                    "Dependencies" to (ext.properties["imports"] as MutableList<String>).fold("") { acc, s ->
                        "$acc$s;"
                    },
                    "Main-Class" to "${project.group}.generated.${project.name}",
                )
            )
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(
            configurations["include"].map {
                if (it.isDirectory) it else zipTree(it)
            }
        )
    }
}

ksp {
    arg("project.group", "${project.group}")
    arg("project.name", project.name)
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/ksp/main/kotlin")
        }
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
    implementation(kotlin("reflect"))
    ksp("com.github.Kotlin-DI:annotation-processor:0.0.3")
}
