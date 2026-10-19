import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("booknest.kmp.library")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "com.nps.booknest.feature.explore.domain"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
