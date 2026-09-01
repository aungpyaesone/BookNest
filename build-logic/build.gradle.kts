plugins {
    `kotlin-dsl`
}

group = "com.nps.booknest.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("androidx.room:room-gradle-plugin:2.8.4")
    implementation("com.android.tools.build:gradle:9.1.1")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.11")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.11.1")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.4.10")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.0")
}

gradlePlugin {
    plugins {
        register("bookNestArchitecture") {
            id = "booknest.architecture"
            implementationClass = "BookNestArchitectureConventionPlugin"
        }
        register("kmpLibrary") {
            id = "booknest.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "booknest.kmp.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpRoom") {
            id = "booknest.kmp.room"
            implementationClass = "KmpRoomConventionPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
