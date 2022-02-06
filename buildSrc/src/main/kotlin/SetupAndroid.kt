import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun Project.setupAndroid() {
    android {
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
