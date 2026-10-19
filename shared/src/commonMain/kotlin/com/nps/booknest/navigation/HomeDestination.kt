package com.nps.booknest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nps.booknest.feature.explore.presentation.navigation.CategoryBooksKey
import com.nps.booknest.feature.home.presentation.HomeRoot
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.library.presentation.navigation.AllBooksKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryListKey
import com.nps.booknest.feature.reader.presentation.navigation.ReaderKey

@Composable
internal fun HomeDestination(backStack: NavBackStack<NavKey>) {
    HomeRoot(
        onNavigateToCatalog = { categoryId ->
            if (backStack.lastOrNull() == HomeKey) {
                backStack.add(if (categoryId == null) AllBooksKey else CategoryBooksKey(categoryId))
            }
        },
        onNavigateToExplore = { backStack.replaceWith(ExploreKey) },
        onNavigateToLibrary = { backStack.replaceWith(LibraryListKey) },
        onNavigateToSettings = {
            if (backStack.lastOrNull() != SettingsKey) {
                backStack.add(SettingsKey)
            }
        },
        onNavigateToReader = { bookId ->
            if (backStack.lastOrNull() !is ReaderKey) {
                backStack.add(ReaderKey(bookId))
            }
        },
    )
}
