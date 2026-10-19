package com.nps.booknest.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.bookNestAdaptiveTypography
import com.nps.booknest.core.designsystem.bookNestBookshelfIcon
import com.nps.booknest.core.designsystem.bookNestIsTablet
import com.nps.booknest.shared.generated.resources.Res
import com.nps.booknest.shared.generated.resources.app_nav_explore
import com.nps.booknest.shared.generated.resources.app_nav_home
import com.nps.booknest.shared.generated.resources.app_nav_library
import com.nps.booknest.shared.generated.resources.app_nav_profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// The shell keeps its independent navigation, adaptive-layout, and chrome states explicit at the composition boundary.
@Suppress("LongParameterList")
@Composable
fun BookNestAppScaffold(
    selectedDestination: AppDestination,
    onDestinationSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    forceExpanded: Boolean? = null,
    isSettingsVisible: Boolean = false,
    isReaderVisible: Boolean = false,
    isNavigationHidden: Boolean = false,
    allowSelectedDestinationClick: Boolean = false,
    content: @Composable () -> Unit,
) {
    val currentContent by rememberUpdatedState(content)
    val movableContent = remember { movableContentOf { currentContent() } }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag(AppNavigationTestTags.APP_SCAFFOLD),
    ) {
        val isExpanded = forceExpanded ?: (maxWidth >= 840.dp)
        val shouldShowBottomNavigation = !(isSettingsVisible && maxHeight > maxWidth)
        if (isReaderVisible) {
            Box(modifier = Modifier.fillMaxSize()) { movableContent() }
        } else if (isNavigationHidden) {
            Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) { movableContent() }
        } else if (isExpanded) {
            Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                BookNestNavigationRail(
                    selectedDestination = selectedDestination,
                    onDestinationSelect = onDestinationSelect,
                    allowSelectedDestinationClick = allowSelectedDestinationClick,
                )
                Box(modifier = Modifier.weight(1f)) { movableContent() }
            }
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (shouldShowBottomNavigation) {
                        BookNestNavigationBar(
                            selectedDestination = selectedDestination,
                            onDestinationSelect = onDestinationSelect,
                            allowSelectedDestinationClick = allowSelectedDestinationClick,
                        )
                    }
                },
            ) { contentPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) { movableContent() }
            }
        }
    }
}

@Composable
fun BookNestNavigationBar(
    selectedDestination: AppDestination,
    onDestinationSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    allowSelectedDestinationClick: Boolean = false,
) {
    val bookNestNavigationTypography = bookNestAdaptiveTypography()
    NavigationBar(
        modifier = modifier.testTag(AppNavigationTestTags.BOTTOM_BAR),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        AppDestination.entries.forEach { destination ->
            NavigationBarItem(
                modifier = Modifier.testTag(AppNavigationTestTags.destinationItem(destination)),
                selected = selectedDestination == destination,
                onClick = {
                    if (destination != selectedDestination || allowSelectedDestinationClick) {
                        onDestinationSelect(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon(),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.label()),
                        style = bookNestNavigationTypography.labelSmall,
                    )
                },
            )
        }
    }
}

@Composable
fun BookNestNavigationRail(
    selectedDestination: AppDestination,
    onDestinationSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    allowSelectedDestinationClick: Boolean = false,
) {
    val bookNestNavigationTypography = bookNestAdaptiveTypography()
    NavigationRail(
        modifier =
            modifier
                .widthIn(min = navigationRailMinWidth(bookNestNavigationTypography.labelMedium))
                .testTag(AppNavigationTestTags.NAVIGATION_RAIL),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        AppDestination.entries.forEachIndexed { index, destination ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(BookNestSpacing.small))
            }
            NavigationRailItem(
                modifier = Modifier.testTag(AppNavigationTestTags.destinationItem(destination)),
                selected = selectedDestination == destination,
                onClick = {
                    if (destination != selectedDestination || allowSelectedDestinationClick) {
                        onDestinationSelect(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon(),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.label()),
                        style = bookNestNavigationTypography.labelMedium,
                    )
                },
            )
        }
    }
}

private fun AppDestination.icon(): ImageVector =
    when (this) {
        AppDestination.HOME -> Icons.Outlined.Home
        AppDestination.EXPLORE -> Icons.Outlined.Explore
        AppDestination.LIBRARY -> bookNestBookshelfIcon
        AppDestination.PROFILE -> Icons.Outlined.PersonOutline
    }

private fun AppDestination.label(): StringResource =
    when (this) {
        AppDestination.HOME -> Res.string.app_nav_home
        AppDestination.EXPLORE -> Res.string.app_nav_explore
        AppDestination.LIBRARY -> Res.string.app_nav_library
        AppDestination.PROFILE -> Res.string.app_nav_profile
    }

@Composable
private fun navigationRailMinWidth(labelStyle: TextStyle): Dp {
    if (!bookNestIsTablet()) return 80.dp
    val measurer = rememberTextMeasurer()
    val labelWidth =
        AppDestination.entries.maxOf { destination ->
            measurer.measure(stringResource(destination.label()), style = labelStyle, softWrap = false).size.width
        }
    return with(LocalDensity.current) { labelWidth.toDp() }.coerceAtLeast(56.dp) + 24.dp
}
