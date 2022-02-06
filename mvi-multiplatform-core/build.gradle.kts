setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Dependencies.Kotlinx.coroutinesCore) {
                    version { strictly(Versions.kotlinCoroutines) }
                }
            }
        }

        commonTest {
            dependencies {
                project(Module.Test)
            }
        }
    }
}
