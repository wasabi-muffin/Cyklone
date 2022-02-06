import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven

fun Project.setupMavenPublication() {
    plugins.apply("maven-publish")

    extensions.getByType<PublishingExtension>().run {
        repositories {
            maven("${rootProject.rootDir}/artifacts/maven")
        }
    }
}
