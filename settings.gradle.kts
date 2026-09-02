rootProject.name = "BookNest"

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":core:database")
include(":core:design-system")
include(":core:domain")
include(":core:presentation")
include(":core:settings")
include(":feature:library:data")
include(":feature:library:domain")
include(":feature:library:presentation")
include(":feature:home:presentation")
include(":feature:settings:presentation")
include(":shared")
