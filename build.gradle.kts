import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.artifacts.ProjectDependency
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    base
    id("booknest.architecture")
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.room) apply false
}

val composeRulesDetekt = libs.compose.rules.detekt

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<KtlintExtension> {
        version.set("1.8.0")
        android.set(true)
        outputToConsole.set(true)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude { sourceFile -> "/build/generated/" in sourceFile.file.invariantSeparatorsPath }
        }
    }

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        parallel = true
        source.setFrom(files("src"))
    }

    dependencies.add("detektPlugins", composeRulesDetekt)

    tasks.withType<Detekt>().configureEach {
        exclude("**/build/**", "**/generated/**")
        reports {
            html.required.set(true)
            sarif.required.set(true)
        }
    }
}

val allowedProjectDependencies = mapOf(
    ":core:domain" to emptySet(),
    ":core:database" to setOf(":core:domain"),
    ":core:presentation" to setOf(":core:domain"),
    ":core:design-system" to emptySet(),
    ":feature:library:domain" to setOf(":core:domain"),
    ":feature:library:data" to setOf(
        ":core:database",
        ":core:domain",
        ":feature:library:domain",
    ),
    ":feature:library:presentation" to setOf(
        ":core:design-system",
        ":core:domain",
        ":core:presentation",
        ":feature:library:domain",
    ),
    ":shared" to setOf(
        ":core:database",
        ":core:design-system",
        ":core:domain",
        ":core:presentation",
        ":feature:library:data",
        ":feature:library:domain",
        ":feature:library:presentation",
    ),
    ":androidApp" to setOf(":shared"),
)

val architectureCheck by tasks.registering {
    group = "verification"
    description = "Checks BookNest module boundaries, packages, and Navigation 3 policy."
    notCompatibleWithConfigurationCache("Reads the live Gradle project dependency graph.")

    doLast {
        allowedProjectDependencies.forEach { (projectPath, allowedDependencies) ->
            val checkedProject = project(projectPath)
            val projectDependencies = checkedProject.configurations
                .flatMap { configuration -> configuration.dependencies.withType(ProjectDependency::class.java) }
                .map { dependency -> dependency.path }
                .filterNot { dependencyPath -> dependencyPath == projectPath }
                .toSet()
            val forbiddenDependencies = projectDependencies - allowedDependencies
            check(forbiddenDependencies.isEmpty()) {
                "$projectPath has forbidden project dependencies: ${forbiddenDependencies.sorted()}"
            }
        }

        val forbiddenDomainImports = listOf(
            "android.",
            "androidx.compose.",
            "androidx.navigation",
            "androidx.room.",
            "org.koin.",
            "platform.",
        )
        listOf(project(":core:domain"), project(":feature:library:domain")).forEach { domainProject ->
            domainProject.fileTree("src") { include("**/*.kt") }.forEach { sourceFile ->
                val source = sourceFile.readText()
                forbiddenDomainImports.forEach { forbiddenImport ->
                    check("import $forbiddenImport" !in source) {
                        "${sourceFile.relativeTo(rootDir)} imports forbidden domain dependency $forbiddenImport"
                    }
                }
            }
        }

        rootDir.walkTopDown()
            .filter { file -> file.isFile && file.extension in setOf("kt", "kts") }
            .filterNot { file -> "/build/" in file.invariantSeparatorsPath }
            .filterNot { file -> file == rootProject.buildFile }
            .forEach { sourceFile ->
                val source = sourceFile.readText()
                check("androidx.navigation.compose" !in source) {
                    "${sourceFile.relativeTo(rootDir)} uses Navigation 2; BookNest requires Navigation 3."
                }
                check("org.jetbrains.androidx.navigation:navigation-compose" !in source) {
                    "${sourceFile.relativeTo(rootDir)} declares a Navigation 2 dependency."
                }
            }
    }
}

val resourceNamingCheck by tasks.registering {
    group = "verification"
    description = "Checks resource identifiers against the BookNest snake_case standard."
    notCompatibleWithConfigurationCache("Scans resource files created by all project modules.")

    doLast {
        val resourceName = Regex("(?<!:)\\bname=\"([^\"]+)\"")
        rootDir.walkTopDown()
            .filter { file ->
                val path = file.invariantSeparatorsPath
                file.isFile &&
                    file.extension == "xml" &&
                    "/build/" !in path &&
                    ("/src/" in path && ("/res/" in path || "/composeResources/" in path))
            }
            .forEach { resourceFile ->
                resourceName.findAll(resourceFile.readText()).forEach { match ->
                    val name = match.groupValues[1]
                    if (!name.startsWith("android:") && ':' !in name) {
                        check(name.matches(Regex("[a-z][a-z0-9_]*"))) {
                            "${resourceFile.relativeTo(rootDir)} contains invalid resource name '$name'."
                        }
                    }
                }
            }
    }
}

val codingStandardsCheck by tasks.registering {
    group = "verification"
    description = "Runs formatting, naming, and resource checks."
    dependsOn(resourceNamingCheck, "moduleNamingCheck")
    dependsOn(subprojects.map { subproject -> "${subproject.path}:ktlintCheck" })
    dependsOn(subprojects.map { subproject -> "${subproject.path}:detekt" })
}

tasks.check {
    dependsOn(architectureCheck, codingStandardsCheck)
}

tasks.register("formatCode") {
    group = "formatting"
    description = "Formats all Kotlin sources with ktlint."
    dependsOn(subprojects.map { subproject -> "${subproject.path}:ktlintFormat" })
}

tasks.register("verifyBookNest") {
    group = "verification"
    description = "Runs BookNest architecture, quality, tests, and platform compilation."
    dependsOn(tasks.check)
    dependsOn(gradle.includedBuild("build-logic").task(":test"))
    dependsOn(":core:domain:testAndroidHostTest")
    dependsOn(":feature:library:data:testAndroidHostTest")
    dependsOn(":feature:library:presentation:testAndroidHostTest")
    dependsOn(":feature:library:presentation:assembleAndroidTest")
    dependsOn(":androidApp:assembleDebug")
    dependsOn(":shared:compileKotlinIosSimulatorArm64")
}
