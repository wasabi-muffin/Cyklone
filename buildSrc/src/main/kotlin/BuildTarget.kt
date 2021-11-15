import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra

enum class BuildType {
    ALL, METADATA, NON_NATIVE, ANDROID, JVM, JS, LINUX, IOS, MAC_OS
}

val ExtensionAware.buildType: BuildType
    get() =
        find("build_type")
            ?.toString()
            ?.let(BuildType::valueOf)
            ?: BuildType.ALL

private fun ExtensionAware.find(key: String) =
    if (extra.has(key)) extra.get(key) else null

interface BuildTarget {

    interface NonNative : BuildTarget

    interface Native : BuildTarget

    interface Darwin : Native


    interface Linux : Native

    object Android : NonNative
    object Jvm : NonNative
    object Js : NonNative
    object Ios : Darwin
    object MacOsX64 : Darwin
    object LinuxX64 : Linux
}

private val ALL_BUILD_TARGETS =
    setOf(
        BuildTarget.Android,
        BuildTarget.Jvm,
        BuildTarget.Js,
        BuildTarget.Ios,
        BuildTarget.MacOsX64,
        BuildTarget.LinuxX64
    )

private val BUILD_TYPE_TO_BUILD_TARGETS: Map<BuildType, Set<BuildTarget>> =
    mapOf(
        BuildType.ALL to ALL_BUILD_TARGETS,
        BuildType.METADATA to ALL_BUILD_TARGETS,
        BuildType.NON_NATIVE to setOf(BuildTarget.Android, BuildTarget.Jvm, BuildTarget.Js),
        BuildType.ANDROID to setOf(BuildTarget.Android),
        BuildType.JVM to setOf(BuildTarget.Jvm),
        BuildType.JS to setOf(BuildTarget.Js),
        BuildType.LINUX to setOf(BuildTarget.LinuxX64),
        BuildType.IOS to setOf(BuildTarget.Ios),
        BuildType.MAC_OS to setOf(BuildTarget.MacOsX64)
    )

val BuildType.buildTargets: Set<BuildTarget> get() = requireNotNull(BUILD_TYPE_TO_BUILD_TARGETS[this])

@Suppress("UNCHECKED_CAST")
var ExtensionAware.buildTargets: Set<BuildTarget>
    get() = if (extra.has("project_build_targets")) extra["project_build_targets"] as Set<BuildTarget> else ALL_BUILD_TARGETS
    set(value) {
        extra["project_build_targets"] = value
    }

inline fun <reified T : BuildTarget> ExtensionAware.isBuildTargetAvailable(): Boolean =
    buildType.buildTargets.any { it is T } && buildTargets.any { it is T }

inline fun <reified T : BuildTarget> ExtensionAware.doIfBuildTargetAvailable(block: () -> Unit) {
    if (isBuildTargetAvailable<T>()) {
        block()
    }
}

fun BuildTarget.setup(project: ExtensionAware, block: () -> Unit) {
    if (project.buildType.buildTargets.contains(this)) {
        block()
    }
}