package com.nps.booknest.feature.home.presentation

internal fun homePopularBookColumns(
    windowWidth: Float,
    windowHeight: Float,
    layoutMode: HomeLayoutMode,
): Int =
    when {
        layoutMode == HomeLayoutMode.COMPACT -> 1
        windowWidth >= 600f && windowHeight >= 600f && windowHeight >= windowWidth -> 4
        layoutMode == HomeLayoutMode.EXPANDED -> 6
        else -> 3
    }
