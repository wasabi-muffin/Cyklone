buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", "1.5.30"))
    }
}

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
    implementation("com.android.tools.build:gradle:7.1.0-beta03")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.2.0")
    implementation(kotlin("script-runtime"))
}
