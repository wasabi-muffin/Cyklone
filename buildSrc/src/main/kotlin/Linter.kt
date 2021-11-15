import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.io.File
import org.gradle.kotlin.dsl.findByType

fun Project.setupLinter() {
    plugins.apply("org.jlleitschuh.gradle.ktlint")

    extensions.findByType<KtlintExtension>()?.apply {
        verbose.set(true)
        android.set(true)
        ignoreFailures.set(true)
        coloredOutput.set(true)
        outputColorName.set("RED")
        additionalEditorconfigFile.set(File("${rootDir.absolutePath}/.editorconfig"))
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    extensions.findByType<BaseExtension>()?.apply {
        lintOptions {
            lintConfig = rootProject.file("lint/lint.xml")
            isCheckDependencies = true
            xmlReport = true
            htmlReport = true
            isAbortOnError = false
            disable.plusAssign(
                setOf(
                    "WebViewApiAvailability",
                    "InlinedApi",
                    "ObsoleteSdkInt",
                    "Override",
                    "NewApi",
                    "UnusedAttribute"
                )
            )
        }
    }
}
