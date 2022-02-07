import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

fun Project.setupMavenPublication() {
    plugins.apply("maven-publish")
    plugins.apply("signing")

    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        val publishing = extensions.getByType<PublishingExtension>().apply {
            publications {
                repositories {
                    maven("${rootProject.rootDir}/artifacts/maven")
                    maven {
                        name = "sonatype"
                        val releaseUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotUrl else releaseUrl
                        credentials {
                            username = getPropertyFromFile("publish.properties", "SONATYPE_USERNAME")
                            password = getPropertyFromFile("publish.properties", "SONATYPE_PASSWORD")
                        }
                    }
                }
                withType<MavenPublication> {
                    artifact(javadocJar)
                    pom {
                        name.set("Cyklone")
                        description.set("MVI Framework for Kotlin Multiplatform")
                        licenses {
                            license {
                                name.set("MIT")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }
                        url.set("https://github.com/gmvalentino8/Cyklone")
                        issueManagement {
                            system.set("Github")
                            url.set("https://github.com/gmvalentino8/Cyklone/issues")
                        }
                        scm {
                            connection.set("https://github.com/gmvalentino8/Cyklone.git")
                            url.set("https://github.com/gmvalentino8/Cyklone")
                        }
                        developers {
                            developer {
                                name.set("Marco Valentino")
                                email.set("gmvalentino8@gmail.com")
                            }
                        }
                    }
                }
            }
        }

        extensions.getByType<SigningExtension>().run {
            val keyId = getPropertyFromFile("publish.properties", "GPG_KEY_ID")
            val key = getPropertyFromFile("publish.properties", "GPG_KEY")
            val password = getPropertyFromFile("publish.properties", "GPG_PASSWORD")
            useInMemoryPgpKeys(keyId, key, password)
            sign(publishing.publications)
        }
    }
}
