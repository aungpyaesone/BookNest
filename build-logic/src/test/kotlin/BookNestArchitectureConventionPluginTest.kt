import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookNestArchitectureConventionPluginTest {
    @TempDir
    lateinit var projectDirectory: Path

    @Test
    fun `moduleNamingCheck passes for lowercase and kebab-case modules`() {
        createFixture(":core:design-system", ":feature:library:presentation")

        val result = runner().build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":moduleNamingCheck")?.outcome)
    }

    @Test
    fun `moduleNamingCheck fails for uppercase module segment`() {
        createFixture(":feature:BookShelf")

        val result = runner().buildAndFail()

        assertTrue(result.output.contains("Invalid module names: [BookShelf]"))
    }

    private fun createFixture(vararg modules: String) {
        projectDirectory.resolve("settings.gradle.kts").writeText(
            buildString {
                appendLine("rootProject.name = \"fixture\"")
                modules.forEach { module -> appendLine("include(\"$module\")") }
            },
        )
        projectDirectory.resolve("build.gradle.kts").writeText(
            "plugins { id(\"booknest.architecture\") }\n",
        )
        modules.forEach { module ->
            val moduleDirectory = projectDirectory.resolve(module.removePrefix(":").replace(':', '/'))
            Files.createDirectories(moduleDirectory)
            moduleDirectory.resolve("build.gradle.kts").writeText("")
        }
    }

    private fun runner(): GradleRunner = GradleRunner.create()
        .withProjectDir(projectDirectory.toFile())
        .withArguments("moduleNamingCheck", "--stacktrace")
        .withPluginClasspath()
}
