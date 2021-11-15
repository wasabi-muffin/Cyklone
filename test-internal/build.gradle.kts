buildTargets = setOf(BuildTarget.Android, BuildTarget.Ios)

setupMultiplatform()
setupLinter()

androidConfig {
    packagingOptions {
        resources.excludes.add("**/*")
    }
}

kotlin {
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs +
                    "-Xallow-result-return-type" +
                    "-Xopt-in=kotlin.RequiresOptIn"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("test"))
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
                api(Deps.Test.kotest)
                api(Deps.Test.turbine)
                api(Deps.Kotlinx.coroutinesCore) {
                    version { strictly(Versions.kotlinCoroutines) }
                }
            }
        }

        androidMain {
            dependencies {
                api(kotlin("test-junit"))
                api(Deps.Test.coroutinesTest)
                api(Deps.Test.androidxJunit)
                api(Deps.Test.androidxTestRunner)
                api(Deps.Test.androidxTestRules)
            }
        }
    }
}
