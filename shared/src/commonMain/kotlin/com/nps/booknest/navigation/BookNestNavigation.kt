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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.nps.booknest.feature.home.presentation.HomeRoot
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.home.presentation.navigation.homeNavigationSerializers
import com.nps.booknest.feature.library.presentation.detail.BookDetailPane
import com.nps.booknest.feature.library.presentation.detail.BookDetailRoot
import com.nps.booknest.feature.library.presentation.list.LibraryListRoot
import com.nps.booknest.feature.library.presentation.navigation.BookDetailKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryListKey
import com.nps.booknest.feature.library.presentation.navigation.libraryNavigationSerializers
import com.nps.booknest.feature.settings.presentation.SettingsRoot
import kotlinx.serialization.modules.SerializersModule

internal const val BOOK_NEST_NAVIGATION_ENTER_DURATION_MILLIS = 220
internal const val BOOK_NEST_NAVIGATION_EXIT_DURATION_MILLIS = 140

private val navigationConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                include(appNavigationSerializers)
                include(homeNavigationSerializers)
                include(libraryNavigationSerializers)
            }
    }

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookNestNavigation() {
    val backStack = rememberNavBackStack(navigationConfiguration, HomeKey)
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()
    val selectedDestination = backStack.lastOrNull().toAppDestination()
    val isSettingsVisible = backStack.lastOrNull() == SettingsKey

    BookNestAppScaffold(
        selectedDestination = selectedDestination,
        isSettingsVisible = isSettingsVisible,
        allowSelectedDestinationClick = isSettingsVisible,
        onDestinationSelect = { destination -> backStack.replaceWith(destination.rootKey()) },
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.navigateBack(selectedDestination) },
            sceneStrategies = listOf(listDetailSceneStrategy),
            sizeTransform = null,
            transitionSpec = { bookNestForwardContentTransform() },
            popTransitionSpec = { bookNestPopContentTransform() },
            predictivePopTransitionSpec = { _ -> bookNestPopContentTransform() },
            entryProvider =
                entryProvider {
                    entry<HomeKey> {
                        HomeRoot(
                            onNavigateToExplore = { backStack.replaceWith(ExploreKey) },
                            onNavigateToLibrary = { backStack.replaceWith(LibraryListKey) },
                            onNavigateToSettings = {
                                if (backStack.lastOrNull() != SettingsKey) {
                                    backStack.add(SettingsKey)
                                }
                            },
                        )
                    }
                    entry<ExploreKey> {
                        ComingSoonScreen(destination = AppDestination.EXPLORE)
                    }
                    entry<ProfileKey> {
                        ComingSoonScreen(destination = AppDestination.PROFILE)
                    }
                    entry<SettingsKey> {
                        SettingsRoot(onNavigateBack = { backStack.removeLastOrNull() })
                    }
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

        ExploreKey -> AppDestination.EXPLORE

        LibraryListKey,
        is BookDetailKey,
        -> AppDestination.LIBRARY

        ProfileKey -> AppDestination.PROFILE

        SettingsKey -> AppDestination.HOME

        else -> AppDestination.HOME
    }
