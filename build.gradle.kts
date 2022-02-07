buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Dependencies.Gradle.kotlin)
    }
}

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
    id("org.jmailen.kotlinter") version "3.7.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

allprojects {
    group = "io.github.gmvalentino8"
    version = getProperty("VERSION", "0.0.0")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
    intellijEngineVersion.set("1.0.656")
}

kotlinter {
    ignoreFailures = false
    indentSize = 4
    reporters = arrayOf("checkstyle", "plain", "html")
    experimentalRules = false
    disabledRules = emptyArray()
}

ktlint {
    verbose.set(true)
    ignoreFailures.set(true)
    coloredOutput.set(true)
    outputColorName.set("RED")
    additionalEditorconfigFile.set(File("${rootDir.absolutePath}/.editorconfig"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
        include("**/*.kts")
    }
}
