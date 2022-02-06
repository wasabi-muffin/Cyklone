rootProject.name = "Cyklone"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":cyklone")
include(":cyklone-statemachine")
include(":test-internal")
