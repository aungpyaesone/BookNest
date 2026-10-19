import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("booknest.kmp.library")
    id("booknest.kmp.compose")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "com.nps.booknest.feature.explore.presentation"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources.enable = true
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:design-system"))
            implementation(project(":core:presentation"))
            implementation(project(":core:domain"))
            implementation(project(":feature:explore:domain"))
            implementation(libs.coil.compose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            api(libs.androidx.navigation3.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiTooling)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.compose.ui.testManifest)
            implementation(libs.compose.uiTest)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}

compose.resources {
    packageOfResClass = "com.nps.booknest.feature.explore.presentation.generated.resources"
}
