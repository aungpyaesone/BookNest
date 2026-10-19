package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSearchField
import com.nps.booknest.core.designsystem.BookNestSelectionChip
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestFunnelIcon
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_collection_subtitle
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_collections
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_collections_count
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_coming_soon
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_create
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_empty_collections
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_filter
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_search_collections
import com.nps.booknest.feature.library.presentation.libraryTypography
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionsRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectionsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val message = stringResource(Res.string.library_browse_coming_soon)
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LibraryBrowseEvent.NavigateBack -> onNavigateBack()
            LibraryBrowseEvent.ShowComingSoon -> snackbar.showSnackbar(message)
        }
    }
    CollectionsScreen(state, viewModel::onAction, modifier, snackbar)
}

@Composable
fun CollectionsScreen(
    state: CollectionsState,
    onAction: (CollectionsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    MaterialTheme(typography = libraryTypography()) {
        Scaffold(
            modifier = modifier.imePadding().testTag("collections_screen"),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbar) },
        ) { padding ->
            CollectionsContent(state, onAction, Modifier.padding(padding))
        }
        if (state.isFilterVisible) CollectionsFilters(state, onAction)
    }
}

@Composable
private fun CollectionsContent(
    state: CollectionsState,
    onAction: (CollectionsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val grid = rememberLazyGridState()
    val focus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(state.query, state.type, state.sort) { grid.scrollToItem(0) }
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val isTablet = maxWidth >= 600.dp
        val scale = LocalDensity.current.fontScale
        val minimumCardWidth =
            if (isTablet) {
                220.dp * scale.coerceAtLeast(1f)
            } else if (scale >= 1.3f) {
                320.dp
            } else {
                140.dp
            }
        Column(Modifier.widthIn(max = 1200.dp).fillMaxSize().padding(horizontal = if (isTablet) 32.dp else 16.dp)) {
            LibraryBrowseToolbar(
                onBack = { onAction(CollectionsAction.OnBackClick) },
                onSearch = {
                    scope.launch {
                        grid.scrollToItem(0)
                        withFrameNanos {}
                        focus.requestFocus()
                        keyboard?.show()
                    }
                },
                onMore = { onAction(CollectionsAction.OnComingSoonClick) },
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minimumCardWidth),
                state = grid,
                modifier = Modifier.weight(1f).testTag("collections_grid"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                    CollectionsHeader(state, onAction, focus)
                }
                if (state.collections.isEmpty()) {
                    item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            stringResource(Res.string.library_browse_empty_collections),
                            Modifier.padding(vertical = 24.dp).testTag("collections_empty"),
                        )
                    }
                }
                items(state.collections, key = {
                    it.id
                }) { collection -> LibraryCollectionCard(collection) { onAction(CollectionsAction.OnComingSoonClick) } }
            }
            BookNestPrimaryButton(stringResource(Res.string.library_browse_create), {
                onAction(CollectionsAction.OnComingSoonClick)
            }, Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("collections_create"))
        }
    }
}

@Composable
private fun CollectionsHeader(
    state: CollectionsState,
    onAction: (CollectionsAction) -> Unit,
    focus: FocusRequester,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LibraryBrowseHero(
            Res.string.library_browse_collections,
            Res.string.library_browse_collection_subtitle,
        )
        CollectionsSearch(state, onAction, Modifier.focusRequester(focus))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CollectionType.entries, key = { it.name }) { type ->
                BookNestSelectionChip(stringResource(type.label()), state.type == type, {
                    onAction(CollectionsAction.OnTypeSelect(type))
                }, Modifier.testTag("collections_type_${type.name}"), textStyle = MaterialTheme.typography.labelSmall)
            }
        }
        LibraryBrowseSortRow(
            pluralStringResource(
                Res.plurals.library_browse_collections_count,
                state.collections.size,
                state.collections.size,
            ),
            state.sort,
            collectionSorts,
        ) {
            onAction(CollectionsAction.OnSortSelect(it))
        }
    }
}

@Composable
private fun CollectionsSearch(
    state: CollectionsState,
    onAction: (CollectionsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = stringResource(Res.string.library_browse_search_collections)
    BookNestSearchField(state.query, placeholder, placeholder, {
        onAction(CollectionsAction.OnSearchChange(it))
    }, modifier.testTag("collections_search"), trailingContent = {
        IconButton({ onAction(CollectionsAction.OnFilterClick) }, Modifier.testTag("collections_filter")) {
            Icon(bookNestFunnelIcon, stringResource(Res.string.library_browse_filter), Modifier.size(20.dp))
        }
    })
}

@Preview
@Composable
private fun CollectionsScreenPreview() {
    BookNestTheme { CollectionsScreen(CollectionsState(collections = libraryCollectionSamples()), {}) }
}
