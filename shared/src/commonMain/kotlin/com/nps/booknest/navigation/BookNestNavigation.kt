package com.nps.booknest.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.nps.booknest.feature.library.presentation.detail.BookDetailPane
import com.nps.booknest.feature.library.presentation.detail.BookDetailRoot
import com.nps.booknest.feature.library.presentation.list.LibraryListRoot
import com.nps.booknest.feature.library.presentation.navigation.BookDetailKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryListKey
import com.nps.booknest.feature.library.presentation.navigation.libraryNavigationSerializers

private val navigationConfiguration =
    SavedStateConfiguration {
        serializersModule = libraryNavigationSerializers
    }

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookNestNavigation() {
    val backStack = rememberNavBackStack(navigationConfiguration, LibraryListKey)
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailSceneStrategy),
        entryProvider =
            entryProvider {
                entry<LibraryListKey>(
                    metadata =
                        ListDetailSceneStrategy.listPane(
                            detailPlaceholder = { BookDetailPane() },
                        ),
                ) {
                    LibraryListRoot(
                        onNavigateToBookDetail = { bookId ->
                            backStack.removeAll { key -> key is BookDetailKey }
                            backStack.add(BookDetailKey(bookId))
                        },
                    )
                }
                entry<BookDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) { key ->
                    BookDetailRoot(bookId = key.bookId)
                }
            },
    )
}
