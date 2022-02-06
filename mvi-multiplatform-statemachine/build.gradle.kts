setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                project(Module.Core)
                implementation(Dependencies.Kotlinx.coroutinesCore) {
                    version { strictly(Versions.kotlinCoroutines) }
                }
                implementation(Dependencies.Logger.kermit)
            }
        }

        commonTest {
            dependencies {
                project(Module.Test)
            }
        }
    }
}
