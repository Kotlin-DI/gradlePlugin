package com.github.kotlin_di.gradle_plugin

import org.gradle.api.provider.Property

interface Extension {
    val keysFile: Property<String>
    val pluginFile: Property<String>
}
