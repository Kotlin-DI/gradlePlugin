package com.github.kotlin_di.gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

open class DependencyPlugin : Plugin<Project> {
    private val imports = mutableListOf<String>()
    override fun apply(project: Project) {
        project.extensions.extraProperties.set(
            "imports",
            imports
        )

        project.plugins.apply("com.github.Kotlin-DI.subplugin")
        project.dependencies.extensions.add(
            ImportDependency::class.java,
            "import",
            ImportDependency(imports)
        )
    }
}
