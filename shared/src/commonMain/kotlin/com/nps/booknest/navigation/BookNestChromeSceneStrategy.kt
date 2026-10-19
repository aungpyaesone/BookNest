package com.nps.booknest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope

internal const val BOOK_NEST_DESTINATION_KEY = "booknest.destination"
internal const val BOOK_NEST_HIDE_NAVIGATION = "booknest.hide_navigation"
internal const val BOOK_NEST_READER_SCENE = "booknest.reader"

/** Keeps each scene's viewport and chrome independent throughout push and pop animations. */
internal class BookNestChromeSceneStrategy(
    private val destinationForKey: (NavKey?) -> AppDestination,
    private val onDestinationSelect: (AppDestination) -> Unit,
) : SceneDecoratorStrategy<NavKey> {
    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(scene: Scene<NavKey>): Scene<NavKey> =
        BookNestChromeScene(scene, destinationForKey, onDestinationSelect)
}

private class BookNestChromeScene(
    private val scene: Scene<NavKey>,
    destinationForKey: (NavKey?) -> AppDestination,
    private val onDestinationSelect: (AppDestination) -> Unit,
) : Scene<NavKey> by scene {
    // Preserve transitions between different underlying scene types, including adaptive panes.
    override val key: Any = scene::class to scene.key
    private val destinationKey =
        scene.entries
            .lastOrNull()
            ?.metadata
            ?.get(BOOK_NEST_DESTINATION_KEY) as? NavKey
    private val selectedDestination = destinationForKey(destinationKey)
    private val isReaderVisible =
        scene.entries
            .lastOrNull()
            ?.metadata
            ?.get(BOOK_NEST_READER_SCENE) == true

    private val isNavigationHidden =
        scene.entries
            .lastOrNull()
            ?.metadata
            ?.get(BOOK_NEST_HIDE_NAVIGATION) == true

    override val content: @Composable () -> Unit = {
        BookNestAppScaffold(
            selectedDestination = selectedDestination,
            onDestinationSelect = onDestinationSelect,
            isSettingsVisible = (destinationKey == SettingsKey || destinationKey == ProfileSettingsKey),
            isReaderVisible = isReaderVisible,
            isNavigationHidden = isNavigationHidden,
            allowSelectedDestinationClick = (destinationKey == SettingsKey || destinationKey == ProfileSettingsKey),
        ) {
            scene.content()
        }
    }
}
