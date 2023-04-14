package com.github.kotlinDI.gradlePlugin

import org.gradle.api.provider.Property

interface Extension {
    val keysFile: Property<String>
    val pluginFile: Property<String>
}