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
include(":core:network")
include(":core:presentation")
include(":core:settings")
include(":feature:library:data")
include(":feature:library:domain")
include(":feature:library:presentation")
include(":feature:home:domain")
include(":feature:home:data")
include(":feature:home:presentation")
include(":feature:explore:presentation")
include(":feature:explore:domain")
include(":feature:explore:data")
include(":feature:reader:domain")
include(":feature:reader:data")
include(":feature:reader:presentation")
include(":feature:settings:presentation")
include(":shared")

include(":feature:profile:presentation")
