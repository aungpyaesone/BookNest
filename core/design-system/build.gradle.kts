import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("booknest.kmp.library")
    id("booknest.kmp.compose")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "com.nps.booknest.core.designsystem"
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
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
            implementation(libs.coil.compose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiTooling)
        }
    }
}

compose.resources {
    packageOfResClass = "com.nps.booknest.core.designsystem.generated.resources"
}
