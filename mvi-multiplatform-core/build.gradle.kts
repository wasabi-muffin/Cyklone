buildTargets = setOf(BuildTarget.Android, BuildTarget.Ios)

setupMultiplatform()
setupLinter()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Kotlinx.coroutinesCore)
                api(Deps.Log.kermit)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-internal"))
            }
        }
    }
}
