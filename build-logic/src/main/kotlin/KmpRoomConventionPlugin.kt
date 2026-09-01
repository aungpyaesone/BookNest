import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target.pluginManager) {
        apply("com.google.devtools.ksp")
        apply("androidx.room")
    }
}
