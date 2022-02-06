object Versions {
    const val androidMinSdk = 24
    const val androidCompileSdk = 30
    const val androidTargetSdk = androidCompileSdk
    const val kotlin = "1.5.31"
    const val gradle = "7.0.0-beta05"
    const val kotlinCoroutines = "1.5.2-native-mt"
    const val androidxTest = "1.4.0"
    const val androidxJunit = "1.1.3"
    const val turbine = "0.7.0"
    const val kotest = "4.6.3"
    const val kotlinterGradle = "3.4.5"
    const val kermit = "1.0.0"
}

object Dependencies {
    object Gradle {
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinter = "org.jmailen.gradle:kotlinter-gradle:${Versions.kotlinterGradle}"
    }

    object Kotlinx {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    }

    object Logger {
        const val kermit = "co.touchlab:kermit:${Versions.kermit}"
    }

    object Test {
        const val androidxJunit = "androidx.test.ext:junit:${Versions.androidxJunit}"
        const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTest}"
        const val androidxTestRules = "androidx.test:rules:${Versions.androidxTest}"
        const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
        const val kotest = "io.kotest:kotest-assertions-core:${Versions.kotest}"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
    }
}
