import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.setupMultiplatform(
    buildTargets: List<BuildTarget> = listOf(BuildTarget.Ios, BuildTarget.Android),
    publish: Boolean = true,
) {
    plugins.apply("kotlin-multiplatform")

    setupLinter()

    if (publish) {
        setupMavenPublication()
    }

    buildTargets.setup<BuildTarget.Android> {
        plugins.apply("com.android.library")
        setupAndroid()
    }

    repositories {
        google()
        mavenCentral()
    }

    kotlin {
        buildTargets.setup<BuildTarget.Js> {
            js(IR) {
                useCommonJs()
                browser()
            }
        }

        buildTargets.setup<BuildTarget.Android> {
            android {
                publishLibraryVariants("release")
            }
        }

        buildTargets.setup<BuildTarget.Jvm> {
            jvm()
        }

        buildTargets.setup<BuildTarget.Ios> {
            ios()
        }

        buildTargets.setup<BuildTarget.MacOsX64> {
            macosX64()
        }

        sourceSets {
            all {
                languageSettings.apply {
                    optIn("kotlin.RequiresOptIn")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = listOf(
                "-Xskip-prerelease-check",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
            )
        }
    }
}
