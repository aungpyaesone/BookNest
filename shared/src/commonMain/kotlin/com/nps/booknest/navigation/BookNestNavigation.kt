package com.nps.booknest.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.nps.booknest.feature.explore.presentation.CategoryBooksRoot
import com.nps.booknest.feature.explore.presentation.ExploreRoot
import com.nps.booknest.feature.explore.presentation.navigation.CategoryBooksKey
import com.nps.booknest.feature.explore.presentation.navigation.exploreNavigationSerializers
import com.nps.booknest.feature.home.presentation.HomeRoot
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.home.presentation.navigation.homeNavigationSerializers
import com.nps.booknest.feature.library.presentation.detail.BookDetailPane
import com.nps.booknest.feature.library.presentation.detail.BookDetailRoot
import com.nps.booknest.feature.library.presentation.list.LibraryListRoot
import com.nps.booknest.feature.library.presentation.navigation.BookDetailKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryListKey
import com.nps.booknest.feature.library.presentation.navigation.libraryNavigationSerializers
import com.nps.booknest.feature.reader.presentation.ReaderRoot
import com.nps.booknest.feature.reader.presentation.navigation.ReaderKey
import com.nps.booknest.feature.reader.presentation.navigation.readerNavigationSerializers
import com.nps.booknest.feature.settings.presentation.SettingsRoot
import kotlinx.serialization.modules.SerializersModule

internal const val BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS = 220
internal const val BOOK_NEST_NAVIGATION_EXIT_DURATION_MILLIS = 140

internal val categoryBooksSceneMetadata: Map<String, Any> =
    mapOf(
        BOOK_NEST_DESTINATION_KEY to ExploreKey,
        BOOK_NEST_HIDE_NAVIGATION to true,
    )

private val navigationConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                include(appNavigationSerializers)
                include(homeNavigationSerializers)
                include(exploreNavigationSerializers)
                include(libraryNavigationSerializers)
                include(readerNavigationSerializers)
            }
    }

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookNestNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(navigationConfiguration, HomeKey)
    val chromeStrategy = rememberChromeStrategy(backStack)

    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.navigateBack(backStack.lastOrNull().toAppDestination()) },
            sceneStrategies = listOf(rememberListDetailSceneStrategy<NavKey>()),
            sceneDecoratorStrategies = listOf(chromeStrategy),
            sizeTransform = null,
            transitionSpec = { bookNestForwardContentTransform() },
            popTransitionSpec = { bookNestPopContentTransform() },
            predictivePopTransitionSpec = { _ -> bookNestPopContentTransform() },
            entryProvider =
                entryProvider {
                    entry<HomeKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to HomeKey)) {
                        HomeRoot(
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
                    entry<ExploreKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ExploreKey)) {
                        ExploreDestination(backStack)
                    }
                    entry<CategoryBooksKey>(metadata = categoryBooksSceneMetadata) { key ->
                        CategoryBooksDestination(key, backStack)
                    }
                    entry<ProfileKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to ProfileKey)) {
                        ComingSoonScreen(destination = AppDestination.PROFILE)
                    }
                    entry<SettingsKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to SettingsKey)) {
                        SettingsRoot(onNavigateBack = { backStack.removeLastOrNull() })
                    }
                    entry<LibraryListKey>(
                        metadata =
                            ListDetailSceneStrategy.listPane(
                                detailPlaceholder = { BookDetailPane() },
                            ) + mapOf(BOOK_NEST_DESTINATION_KEY to LibraryListKey),
                    ) {
                        LibraryListRoot(
                            onNavigateToBookDetail = { bookId ->
                                backStack.removeAll { key -> key is BookDetailKey }
                                backStack.add(BookDetailKey(bookId))
                            },
                        )
                    }
                    entry<BookDetailKey>(
                        metadata =
                            ListDetailSceneStrategy.detailPane() + mapOf(BOOK_NEST_DESTINATION_KEY to LibraryListKey),
                    ) { key ->
                        BookDetailRoot(
                            bookId = key.bookId,
                            onNavigateToReader = { bookId -> backStack.add(ReaderKey(bookId)) },
                        )
                    }
                    entry<ReaderKey>(metadata = mapOf(BOOK_NEST_READER_SCENE to true)) { key ->
                        ReaderRoot(
                            bookId = key.bookId,
                            onNavigateBack = {
                                if (backStack.lastOrNull() == key) backStack.removeLastOrNull()
                            },
                        )
                    }
                },
        )
    }
}

@Composable
private fun ExploreDestination(backStack: NavBackStack<NavKey>) {
    ExploreRoot(
        onNavigateToCategory = { categoryId -> backStack.add(CategoryBooksKey(categoryId)) },
    )
}

@Composable
private fun CategoryBooksDestination(
    key: CategoryBooksKey,
    backStack: NavBackStack<NavKey>,
) {
    CategoryBooksRoot(
        categoryId = key.categoryId,
        onNavigateBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun rememberChromeStrategy(backStack: NavBackStack<NavKey>): BookNestChromeSceneStrategy =
    remember(backStack) {
        BookNestChromeSceneStrategy(
            destinationForKey = { it.toAppDestination() },
            onDestinationSelect = { destination -> backStack.replaceWith(destination.rootKey()) },
        )
    }

private fun MutableList<NavKey>.replaceWith(rootKey: NavKey) {
    if (size != 1 || lastOrNull() != rootKey) {
        clear()
        add(rootKey)
    }
}

private fun MutableList<NavKey>.navigateBack(selectedDestination: AppDestination) {
    when {
        size > 1 -> removeLastOrNull()
        selectedDestination != AppDestination.HOME -> replaceWith(HomeKey)
    }
}

internal fun bookNestForwardContentTransform(): ContentTransform =
    (
        fadeIn(
            animationSpec =
                tween(
                    durationMillis = BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS,
                    easing = LinearOutSlowInEasing,
                ),
        ) +
            scaleIn(
                animationSpec =
                    tween(
                        durationMillis = BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS,
                        easing = LinearOutSlowInEasing,
                    ),
                initialScale = 0.985f,
            )
    ).togetherWith(
        fadeOut(
            animationSpec =
                tween(
                    durationMillis = BOOK_NEST_NAVIGATION_EXIT_DURATION_MILLIS,
                    easing = FastOutLinearInEasing,
                ),
        ),
    )

internal fun bookNestPopContentTransform(): ContentTransform =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS,
                easing = LinearOutSlowInEasing,
            ),
    ).togetherWith(
        fadeOut(
            animationSpec =
                tween(
                    durationMillis = BOOK_NEST_NAVIGATION_EXIT_DURATION_MILLIS,
                    easing = FastOutLinearInEasing,
                ),
        ) +
            scaleOut(
                animationSpec =
                    tween(
                        durationMillis = BOOK_NEST_NAVIGATION_EXIT_DURATION_MILLIS,
                        easing = FastOutLinearInEasing,
                    ),
                targetScale = 0.985f,
            ),
    )

private fun AppDestination.rootKey(): NavKey =
    when (this) {
        AppDestination.HOME -> HomeKey
        AppDestination.EXPLORE -> ExploreKey
        AppDestination.LIBRARY -> LibraryListKey
        AppDestination.PROFILE -> ProfileKey
    }

private fun NavKey?.toAppDestination(): AppDestination =
    when (this) {
        HomeKey -> AppDestination.HOME

        ExploreKey, is CategoryBooksKey -> AppDestination.EXPLORE

        LibraryListKey,
        is BookDetailKey,
        -> AppDestination.LIBRARY

        ProfileKey -> AppDestination.PROFILE

        SettingsKey -> AppDestination.HOME

        else -> AppDestination.HOME
    }
