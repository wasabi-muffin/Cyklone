setupMultiplatform(publish = false)

android {
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
                api(Dependencies.Test.kotest)
                api(Dependencies.Test.turbine)
                api(Dependencies.Kotlinx.coroutinesCore) {
                    version { strictly(Versions.kotlinCoroutines) }
                }
            }
        }

        androidMain {
            dependencies {
                api(kotlin("test-junit"))
                api(Dependencies.Test.coroutinesTest)
                api(Dependencies.Test.androidxJunit)
                api(Dependencies.Test.androidxTestRunner)
                api(Dependencies.Test.androidxTestRules)
            }
        }
    }
}
