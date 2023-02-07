package com.github.kotlin_di.gradle_plugin

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

class IncludeExtension(private val imports: Configuration) : (Dependency?) -> Dependency? {
    override fun invoke(dependency: Dependency?): Dependency? {
        if (dependency != null) {
            imports.dependencies.add(dependency)
        }
        return dependency
    }
}
