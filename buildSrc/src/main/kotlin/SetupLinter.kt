import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

fun Project.setupLinter() {
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
