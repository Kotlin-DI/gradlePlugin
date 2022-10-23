import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    java
    `kotlin-dsl`
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.jetbrains.dokka") version "1.6.21"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "io.github.Kotlin-DI"
version = "0.0.2"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")
    mavenLocal()
}

val kotlinVersion: String by project
val kspVersion: String by project

dependencies {
    implementation("com.github.kotlin_di:common:main-SNAPSHOT")
    implementation("com.github.kotlin_di:ioc:main-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kotlinVersion-$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
            jvmTarget = "11"
        }
        dependsOn("ktlintFormat")
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register<Copy>("copyGitHooks") {
        description = "Copy git hooks from scripts/git-hooks"
        group = "git-hooks"
        from("$rootDir/scripts/git-hooks/") {
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
    }

    register<Exec>("installGitHooks") {
        description = "Installs the pre-commit git hooks from scripts/git-hooks."
        group = "git-hooks"
        onlyIf {
            !org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
        }
        dependsOn(named("copyGitHooks"))

        workingDir(rootDir)
        commandLine("chmod")
        args("-R", "+x", ".git/hooks/")

        doLast {
            logger.info("Git hooks installed successfully.")
        }
    }

    register<Delete>("deleteGitHooks") {
        group = "git-hooks"
        description = "Delete the pre-commit git hooks."
        delete(fileTree(".git/hooks/"))
    }

    afterEvaluate {
        tasks["clean"].dependsOn(tasks.named("installGitHooks"))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            from(components["java"])
        }
    }
}

pluginBundle {
    website = "https://Kotlin-DI.github.io"
    vcsUrl = "https://github.com/Kotlin-DI/gradle-plugin.git"
    tags = listOf("dependency-injection", "annotation-processing")
}

gradlePlugin {

    plugins {

        findByName("com.github.kotlin_di.subplugin")?.apply {
            id = "io.github.Kotlin-DI.subplugin"
            displayName = "Kotlin DI subplugin"
            description = "pre-compiled plugin"
        }

        create("plugin") {
            id = "io.github.Kotlin-DI.plugin"
            displayName = "Kotlin DI plugin"
            description = "Applies all of the required libraries"
            implementationClass = "com.github.kotlin_di.gradle_plugin.DependencyPlugin"
        }
    }
}
