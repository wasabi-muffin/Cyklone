import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra

enum class Module(val path: String) {
    Core(":common:core"),
    Domain(":common:domain"),
    Api(":common:data:api"),
    Database(":common:data:database"),
    Models(":common:data:models"),
    Repository(":common:data:repository"),
    Settings(":common:data:settings"),
    MviCore(":common:presentation:mvicore"),
    Test(":common:presentation:test"),
    Overview(":common:presentation:overview"),
    AddTask(":common:presentation:addtask"),
    ;
}


@Suppress("UNCHECKED_CAST")
var ExtensionAware.projectDependencies: Set<Module>
    get() = if (extra.has("project_dependencies")) extra["project_dependencies"] as Set<Module> else emptySet()
    set(value) {
        extra["project_dependencies"] = value
    }