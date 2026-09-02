package com.nps.booknest.navigation

object AppNavigationTestTags {
    const val APP_SCAFFOLD = "app_scaffold"
    const val BOTTOM_BAR = "app_bottom_bar"
    const val NAVIGATION_RAIL = "app_navigation_rail"

    fun destinationItem(destination: AppDestination): String = "app_navigation_${destination.name.lowercase()}"
}
