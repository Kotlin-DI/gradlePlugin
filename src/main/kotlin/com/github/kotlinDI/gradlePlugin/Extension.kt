package com.github.kotlinDI.gradlePlugin

import org.gradle.api.provider.Property

interface Extension {
    val common: Property<String>
    val processor: Property<String>
    val keysFile: Property<String>
    val pluginFile: Property<String>
}