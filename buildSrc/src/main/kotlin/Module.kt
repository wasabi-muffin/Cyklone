import org.gradle.api.artifacts.Dependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

enum class Module(val path: String) {
    Test(":test-internal"),
    Core(":cyklone"),
    StateMachine(":cyklone-statemachine"),
    ;
}

fun KotlinDependencyHandler.project(module: Module): Dependency? = implementation(project(module.path))
fun KotlinDependencyHandler.projectApi(module: Module): Dependency? = api(project(module.path))
