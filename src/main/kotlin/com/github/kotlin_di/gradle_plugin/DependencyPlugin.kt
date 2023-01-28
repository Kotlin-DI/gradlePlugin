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

    val commonVersion: String = "0.1.4"

    private val imports = mutableListOf<String>()
    private val includes = mutableListOf<Dependency>()
    override fun apply(project: Project) {
        val config = project.extensions.create(
            "kotlin_di",
            Extension::class.java
        )

        project.plugins.apply("com.google.devtools.ksp")
        project.dependencies.add("implementation", "com.github.Kotlin-DI:common:$commonVersion")
        project.dependencies.add("ksp", "com.github.kotlin_di:annotation-processor:main-SNAPSHOT")

//        val include = project.configurations.create("include")

        project.extensions.extraProperties.set(
            "imports",
            imports
        )

        val jar = project.tasks.findByName("jar")

        (jar as Jar).apply {
            manifest.attributes(
                mapOf(
                    "Implementation-Title" to "${project.group}.${project.name}",
                    "Implementation-Version" to project.version,
                    "Dependencies" to (imports).fold("") { acc, s ->
                        "$acc$s;"
                    },
                    "Main-Class" to "${project.group}.generated.${project.name}",
                )
            )
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//            from(
//                include.map {
//                    if (it.isDirectory) it else project.zipTree(it)
//                }
//            )
        }

        project.afterEvaluate {
            println("after eval")
            ((it as ExtensionAware).extensions.getByName("ksp") as KspExtension).apply {
                arg("project.group", "${it.group}")
                arg("project.name", "${it.name}")
                arg("project.version", "${it.version}")
                arg("keysFile", config.keysFile.orNull ?: "Keys")
                arg("pluginFile", config.pluginFile.orNull ?: "Plugin")
            }
        }

        project.dependencies.extensions.add(
            DependsOnExtension::class.java,
            "dependsOn",
            DependsOnExtension(imports)
        )
        project.dependencies.extensions.add(
            IncludeExtension::class.java,
            "include",
            IncludeExtension(project.configurations)
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
    }
}
