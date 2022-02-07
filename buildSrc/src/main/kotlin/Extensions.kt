import com.android.build.gradle.BaseExtension
import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType<KotlinMultiplatformExtension>().block()
}

fun Project.android(block: BaseExtension.() -> Unit) {
    extensions.getByType<BaseExtension>().block()
}

fun KotlinMultiplatformExtension.sourceSets(block: SourceSets.() -> Unit) {
    sourceSets.block()
}

fun getProperty(key: String, default: String): String = getProperty(key) ?: default

fun getProperty(key: String): String? = System.getProperty(key) ?: System.getenv(key)

fun getPropertyFromFile(filename: String, key: String): String? = Properties().apply { load(FileInputStream(filename)) }.getProperty(key)
