rootProject.name = "mvi-multiplatform"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":mvi-multiplatform-core")
include(":mvi-multiplatform-statemachine")
include(":test-internal")
