package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestLogo
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.core.presentation.UiText
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_filter_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_logo_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_notifications_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_search_label
import com.nps.booknest.feature.home.presentation.generated.resources.home_search_placeholder
import com.nps.booknest.feature.home.presentation.generated.resources.home_settings_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

enum class HomeLayoutMode {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

object HomeTestTags {
    const val COMPACT_FEED = "home_compact_feed"
    const val ADAPTIVE_FEED = "home_adaptive_feed"
    const val FILTER_SHEET = "home_filter_sheet"
    const val EMPTY_RESULTS = "home_empty_results"
    const val HERO_CARD = "home_hero_card"
    const val HERO_ACTION = "home_hero_action"
    const val CONTINUE_READING_CARD = "home_continue_reading_card"
    const val READ_NOW_ACTION = "home_read_now_action"
    const val PREMIUM_BANNER = "home_premium_banner"
    const val UPGRADE_ACTION = "home_upgrade_action"
    const val APP_BAR = "home_app_bar"
    const val LOGO = "home_logo"
    const val SEARCH = "home_search"
    const val SETTINGS_ACTION = "home_settings_action"

    fun popularBookTitle(bookId: String) = "home_popular_book_title_$bookId"

    fun buyAction(bookId: String) = "home_buy_action_$bookId"

    fun rentAction(bookId: String) = "home_rent_action_$bookId"
}

@Composable
fun HomeRoot(
    onNavigateToExplore: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReader: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingSnackbar by remember { mutableStateOf<UiText?>(null) }
    val snackbarMessage = pendingSnackbar?.asString()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToExplore -> onNavigateToExplore()
            HomeEvent.NavigateToLibrary -> onNavigateToLibrary()
            HomeEvent.NavigateToSettings -> onNavigateToSettings()
            is HomeEvent.NavigateToReader -> onNavigateToReader(event.bookId)
            is HomeEvent.ShowSnackbar -> pendingSnackbar = event.message
        }
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            pendingSnackbar = null
        }
    }

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    layoutMode: HomeLayoutMode? = null,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val resolvedLayoutMode = layoutMode ?: homeLayoutMode(maxWidth)
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                HomeHeader(
                    onNotificationClick = { onAction(HomeAction.OnNotificationClick) },
                    onSettingsClick = { onAction(HomeAction.OnSettingsClick) },
                    layoutMode = resolvedLayoutMode,
                )
            },
        ) { contentPadding ->
            if (resolvedLayoutMode == HomeLayoutMode.COMPACT) {
                CompactHomeFeed(state, onAction, contentPadding)
            } else {
                AdaptiveHomeFeed(state, onAction, contentPadding, resolvedLayoutMode)
            }
        }
        if (state.isFilterSheetVisible) HomeFilterSheet(state = state, onAction = onAction)
    }
}

private fun homeLayoutMode(maxWidth: Dp): HomeLayoutMode =
    when {
        maxWidth < 600.dp -> HomeLayoutMode.COMPACT
        maxWidth < 840.dp -> HomeLayoutMode.MEDIUM
        else -> HomeLayoutMode.EXPANDED
    }

@Composable
private fun CompactHomeFeed(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag(HomeTestTags.COMPACT_FEED),
        contentPadding =
            PaddingValues(
                start = BookNestSpacing.screenCompact,
                top = contentPadding.calculateTopPadding() + BookNestSpacing.medium,
                end = BookNestSpacing.screenCompact,
                bottom = contentPadding.calculateBottomPadding() + BookNestSpacing.section,
            ),
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
    ) {
        item { HomeSearch(state = state, onAction = onAction, layoutMode = HomeLayoutMode.COMPACT) }
        item { HomeHeroCarousel(state, onAction, HomeLayoutMode.COMPACT) }
        item { HomeCategories(state, onAction, HomeLayoutMode.COMPACT) }
        item { PopularBooks(state, onAction, columns = 1, layoutMode = HomeLayoutMode.COMPACT) }
        item { ContinueReadingCard(state.continueReading, onAction, HomeLayoutMode.COMPACT) }
        item {
            PremiumBanner(
                onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                layoutMode = HomeLayoutMode.COMPACT,
            )
        }
    }
}

@Composable
private fun AdaptiveHomeFeed(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    contentPadding: PaddingValues,
    layoutMode: HomeLayoutMode,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .width(BookNestSize.contentMaxWidth)
                    .testTag(HomeTestTags.ADAPTIVE_FEED),
            contentPadding =
                PaddingValues(
                    start = BookNestSpacing.screenExpanded,
                    top = contentPadding.calculateTopPadding() + BookNestSpacing.extraLarge,
                    end = BookNestSpacing.screenExpanded,
                    bottom = contentPadding.calculateBottomPadding() + BookNestSpacing.section,
                ),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.section),
        ) {
            item { HomeSearch(state = state, onAction = onAction, layoutMode = layoutMode) }
            item { HomeHeroCarousel(state, onAction, layoutMode) }
            item { HomeCategories(state, onAction, layoutMode) }
            item {
                PopularBooks(
                    state = state,
                    onAction = onAction,
                    columns = if (layoutMode == HomeLayoutMode.EXPANDED) 6 else 3,
                    layoutMode = layoutMode,
                )
            }
            item {
                if (layoutMode == HomeLayoutMode.MEDIUM) {
                    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraLarge)) {
                        ContinueReadingCard(state.continueReading, onAction, layoutMode)
                        PremiumBanner(
                            onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                            layoutMode = layoutMode,
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.extraLarge)) {
                        ContinueReadingCard(
                            state.continueReading,
                            onAction,
                            layoutMode,
                            Modifier.weight(1.5f),
                        )
                        PremiumBanner(
                            onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                            layoutMode = layoutMode,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val horizontalPadding =
        if (layoutMode == HomeLayoutMode.COMPACT) {
            BookNestSpacing.screenCompact
        } else {
            BookNestSpacing.screenExpanded
        }
    Surface(
        modifier = modifier.fillMaxWidth().testTag(HomeTestTags.APP_BAR),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier =
                    Modifier
                        .widthIn(max = BookNestSize.contentMaxWidth)
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = BookNestSpacing.extraSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BookNestLogo(
                    contentDescription = stringResource(Res.string.home_logo_description),
                    modifier = Modifier.testTag(HomeTestTags.LOGO),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(BookNestSize.minimumTouchTarget),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = stringResource(Res.string.home_notifications_description),
                            modifier = Modifier.size(if (layoutMode == HomeLayoutMode.COMPACT) 20.dp else 22.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier =
                            Modifier
                                .size(BookNestSize.minimumTouchTarget)
                                .testTag(HomeTestTags.SETTINGS_ACTION),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(Res.string.home_settings_description),
                            modifier = Modifier.size(if (layoutMode == HomeLayoutMode.COMPACT) 20.dp else 22.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSearch(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val searchLabel = stringResource(Res.string.home_search_label)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape =
        if (layoutMode == HomeLayoutMode.COMPACT) {
            MaterialTheme.shapes.medium
        } else {
            MaterialTheme.shapes.large
        }
    val textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
    BasicTextField(
        value = state.searchQuery,
        onValueChange = { onAction(HomeAction.OnSearchQueryChange(it)) },
        modifier =
            modifier
                .fillMaxWidth()
                .height(BookNestSize.minimumTouchTarget)
                .testTag(HomeTestTags.SEARCH)
                .background(MaterialTheme.colorScheme.surface, shape)
                .semantics { contentDescription = searchLabel }
                .border(
                    width = 1.dp,
                    color =
                        if (isFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    shape = shape,
                ),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = BookNestSpacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier.weight(1f).padding(horizontal = BookNestSpacing.small),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.home_search_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
                IconButton(
                    onClick = { onAction(HomeAction.OnFilterClick) },
                    modifier = Modifier.size(BookNestSize.minimumTouchTarget),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = stringResource(Res.string.home_filter_description),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    BookNestTheme(isDarkTheme = false) {
        HomeScreen(state = HomeState(), onAction = {}, layoutMode = HomeLayoutMode.COMPACT)
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    BookNestTheme(isDarkTheme = true) {
        HomeScreen(state = HomeState(), onAction = {}, layoutMode = HomeLayoutMode.EXPANDED)
    }
}
