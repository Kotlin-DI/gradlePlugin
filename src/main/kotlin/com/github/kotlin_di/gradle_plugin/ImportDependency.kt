package com.github.kotlin_di.gradle_plugin

import org.gradle.api.artifacts.Dependency

open class ImportDependency(private val imports: MutableList<String>) : (Dependency?) -> Dependency? {

    override fun invoke(dependency: Dependency?): Dependency? {
        if (dependency != null) {
            imports.add("${dependency.group}.${dependency.name}:${dependency.version}")
        }
        return dependency
    }
}
