import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.setupMultiplatform() {
    plugins.apply("kotlin-multiplatform")

    doIfBuildTargetAvailable<BuildTarget.Android> {
        plugins.apply("com.android.library")

        setupAndroidSdkVersions()
    }

    repositories {
        google()
        mavenCentral()
    }

    kotlin {
        doIfBuildTargetAvailable<BuildTarget.Js> {
            js(IR) {
                useCommonJs()
                browser()
            }
        }

        doIfBuildTargetAvailable<BuildTarget.Android> {
            android {
                // publishLibraryVariants("release", "debug")
            }
        }

        doIfBuildTargetAvailable<BuildTarget.Jvm> {
            jvm()
        }

        doIfBuildTargetAvailable<BuildTarget.Ios> {
            val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
            if (onPhone) {
                iosArm64("ios")
            } else {
                iosX64("ios")
            }
        }

        doIfBuildTargetAvailable<BuildTarget.MacOsX64> {
            macosX64()
        }

        sourceSets {
            all {
                languageSettings.apply {
                    optIn("kotlin.RequiresOptIn")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }

            commonMain {
                dependencies {
                    projectDependencies.forEach { implementation(project(it.path)) }
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

fun Project.setupAndroidSdkVersions() {
    androidConfig {

        compileSdkVersion(Versions.androidCompileSdk)

        defaultConfig {
            targetSdk = Versions.androidTargetSdk
            minSdk = Versions.androidMinSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }

        testOptions {
            unitTests {
                // robolectricをjava8で動かすための設定
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        packagingOptions {
            resources.excludes.run {
                remove("**/*.kotlin_metadata")
                add("META-INF/AL2.0")
                add("META-INF/LGPL2.1")
                add("META-INF/licenses/ASM")
                add("win32-x86/**")
                add("win32-x86-64/**")
            }
        }
    }
}

fun Project.setupCocoapods(framework: String = name.capitalize()) {
    kotlin {
        plugins.apply("org.jetbrains.kotlin.native.cocoapods")

        cocoapodsConfig {
            summary = "MVIMultiplatform"
            homepage = "https://github.com/gmvalentino/mvi-multiplatform"
            frameworkName = framework
        }
    }
}

fun KotlinMultiplatformExtension.cocoapodsConfig(block: CocoapodsExtension.() -> Unit) {
    (this as ExtensionAware).extensions.getByType<CocoapodsExtension>()
        .block()
}

fun Project.androidConfig(block: BaseExtension.() -> Unit) {
    extensions.getByType<BaseExtension>().block()
}

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType<KotlinMultiplatformExtension>().block()
}

fun KotlinMultiplatformExtension.sourceSets(block: SourceSets.() -> Unit) {
    sourceSets.block()
}
