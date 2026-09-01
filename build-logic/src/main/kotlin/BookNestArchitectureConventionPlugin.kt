import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class BookNestArchitectureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            require(this == rootProject) { "booknest.architecture must be applied to the root project." }

            tasks.register("moduleNamingCheck") {
                group = "verification"
                description = "Checks that Gradle module path segments use lowercase or kebab-case names."
                notCompatibleWithConfigurationCache("Reads the complete live Gradle project hierarchy.")

                doLast {
                    val moduleName = Regex("[a-z][a-z0-9-]*")
                    val invalidModules = rootProject.subprojects
                        .flatMap { project -> project.path.removePrefix(":").split(':') }
                        .distinct()
                        .filterNot { module -> moduleName.matches(module) || module == "androidApp" }

                    if (invalidModules.isNotEmpty()) {
                        throw GradleException(
                            "Invalid module names: ${invalidModules.sorted()}. " +
                                "Use lowercase names and kebab-case for multiword segments.",
                        )
                    }
                }
            }
        }
    }
}
