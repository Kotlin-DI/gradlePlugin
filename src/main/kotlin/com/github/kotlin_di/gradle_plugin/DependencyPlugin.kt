package com.github.kotlin_di.gradle_plugin

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

open class DependencyPlugin : Plugin<Project> {

    private val commonVersion: String = "0.1.4"

    private val imports = mutableListOf<String>()
    private val includes = mutableListOf<Dependency>()
    override fun apply(project: Project) {
        val config = project.extensions.create(
            "kotlin_di",
            Extension::class.java
        )

        project.plugins.apply("com.google.devtools.ksp")
        project.dependencies.add("implementation", "com.github.kotlin_di:common:develop-SNAPSHOT")
        project.dependencies.add("ksp", "com.github.kotlin_di:annotation-processor:develop-SNAPSHOT")

        project.extensions.extraProperties.set(
            "imports",
            imports
        )

        project.dependencies.extensions.add(
            DependsOnExtension::class.java,
            "dependsOn",
            DependsOnExtension(imports)
        )

        ((project as ExtensionAware).extensions.getByName("kotlin") as KotlinJvmProjectExtension).apply {
            sourceSets.getByName("main") {
                it.kotlin.srcDir("build/generated/ksp/main/kotlin")
            }

            sourceSets.getByName("test") {
                it.kotlin.srcDir("build/generated/ksp/test/kotlin")
            }
        }

        ((project as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer).apply {
            getByName("main") {
                it.java.srcDir("build/generated/ksp/main/kotlin")
            }
            getByName("test") {
                it.java.srcDir("build/generated/ksp/test/kotlin")
            }
        }

        val include = project.configurations.create("includeClasspath") {
            it.isCanBeResolved = true
            it.isCanBeConsumed = false
        }
        project.dependencies.extensions.add(
            IncludeExtension::class.java,
            "include",
            IncludeExtension(include)
        )

        project.afterEvaluate {
            val keysFile = config.keysFile.orNull ?: "Keys"
            val pluginFile = config.pluginFile.orNull ?: "Plugin"
            ((it as ExtensionAware).extensions.getByName("ksp") as KspExtension).apply {
                arg("project.group", "${it.group}")
                arg("project.name", it.name)
                arg("project.version", "${it.version}")
                arg("keysFile", keysFile)
                arg("pluginFile", pluginFile)
            }

            val includes = it.configurations.getByName("includeClasspath")
            val jar = it.tasks.findByName("jar")
            (jar as Jar).apply {
                manifest.attributes(
                    mapOf(
                        "Implementation-Title" to "${it.group}.${it.name}",
                        "Implementation-Version" to it.version,
                        "Dependencies" to (imports).fold("") { acc, s ->
                            "$acc$s;"
                        },
                        "Main-Class" to "${it.group}.${it.name}.$pluginFile",
                    )
                )
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                from(
                    includes.map { f ->
                        if (f.isDirectory) it else project.zipTree(f)
                    }
                )
            }
        }
    }
}
