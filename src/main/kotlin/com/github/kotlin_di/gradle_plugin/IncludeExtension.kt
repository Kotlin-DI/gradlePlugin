package com.github.kotlin_di.gradle_plugin

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency

class IncludeExtension(private val container: ConfigurationContainer) : (Dependency?) -> Dependency? {

    val compile = container.getByName("compileClasspath").dependencies
    val runtime = container.getByName("runtimeClasspath").dependencies
    val test = container.getByName("testCompileClasspath").dependencies
    val testRuntime = container.getByName("testRuntimeClasspath").dependencies

    override fun invoke(dependency: Dependency?): Dependency? {
        if (dependency != null) {
            compile.add(dependency)
            runtime.add(dependency)
            test.add(dependency)
            testRuntime.add(dependency)
        }
        return dependency
    }
}
