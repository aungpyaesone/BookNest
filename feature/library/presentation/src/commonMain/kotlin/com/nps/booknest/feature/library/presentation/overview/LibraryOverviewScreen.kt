package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestLogo
import com.nps.booknest.core.designsystem.BookNestSearchField
import com.nps.booknest.core.designsystem.BookNestSelectionChip
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestFunnelIcon
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_all
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_brand
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_collections
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_coming_soon
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_downloads
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_filter
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_finished
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_my_books
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_notes
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_reading
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_recent
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_saved
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_search
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_title
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryOverviewRoot(
    modifier: Modifier = Modifier,
    onNavigateToReadingNow: () -> Unit = {},
    onNavigateToCollections: () -> Unit = {},
    onNavigateToMyBooks: () -> Unit = {},
    viewModel: LibraryOverviewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val message = stringResource(Res.string.library_overview_coming_soon)
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LibraryOverviewEvent.NavigateToReadingNow -> onNavigateToReadingNow()
            LibraryOverviewEvent.NavigateToCollections -> onNavigateToCollections()
            LibraryOverviewEvent.NavigateToMyBooks -> onNavigateToMyBooks()
            LibraryOverviewEvent.ShowComingSoon -> snackbar.showSnackbar(message)
        }
    }
    LibraryOverviewScreen(state, viewModel::onAction, modifier, snackbar)
}

@Composable
fun LibraryOverviewScreen(
    state: LibraryOverviewState,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    MaterialTheme(
        typography = libraryOverviewTypography,
        shapes = MaterialTheme.shapes.copy(small = RoundedCornerShape(6.dp)),
    ) {
        Box(modifier.fillMaxSize().testTag("library_overview_screen")) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbar) },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { padding ->
                LibraryOverviewContent(state, onAction, Modifier.padding(padding))
            }
            if (state.isFilterVisible) LibraryOverviewFilters(state, onAction)
        }
    }
}

@Composable
private fun LibraryOverviewContent(
    state: LibraryOverviewState,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val horizontalPadding =
            if (maxWidth < 600.dp) BookNestSpacing.screenCompact else BookNestSpacing.screenExpanded
        Column(
            Modifier
                .widthIn(max = BookNestSize.contentMaxWidth)
                .fillMaxSize()
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
        ) {
            BookNestLogo(
                stringResource(Res.string.library_overview_brand),
                Modifier.padding(top = BookNestSpacing.small),
                iconSize = if (libraryIsTablet()) 28.dp else 24.dp,
                textStyle = MaterialTheme.typography.titleLarge,
            )
            Text(stringResource(Res.string.library_overview_title), style = MaterialTheme.typography.headlineSmall)
            BookNestSearchField(
                query = state.query,
                placeholder = stringResource(Res.string.library_overview_search),
                contentDescription = stringResource(Res.string.library_overview_search),
                onQueryChange = { onAction(LibraryOverviewAction.OnSearchChange(it)) },
                modifier = Modifier.testTag("library_overview_search"),
                trailingContent = {
                    IconButton(
                        onClick = { onAction(LibraryOverviewAction.OnFilterClick) },
                        modifier = Modifier.testTag("library_overview_filter"),
                    ) {
                        Icon(
                            bookNestFunnelIcon,
                            stringResource(Res.string.library_overview_filter),
                            Modifier.size(20.dp),
                        )
                    }
                },
            )
            LibraryStatusTabs(state.selectedTab, onAction)
            LibraryOverviewFeed(state, onAction, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LibraryStatusTabs(
    selected: LibraryTab,
    onAction: (LibraryOverviewAction) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag("library_overview_tabs"),
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
    ) {
        items(LibraryTab.entries, key = { it.name }) { tab ->
            BookNestSelectionChip(
                label = stringResource(libraryTabLabel(tab)),
                selected = selected == tab,
                onClick = { onAction(LibraryOverviewAction.OnTabSelect(tab)) },
                modifier = Modifier.testTag("library_tab_${tab.name}"),
                textStyle = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun LibraryOverviewFeed(
    state: LibraryOverviewState,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.selectedTab, state.query, state.sort) { listState.scrollToItem(0) }
    LazyColumn(
        modifier = modifier.testTag("library_overview_feed"),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        contentPadding = PaddingValues(bottom = BookNestSpacing.section),
    ) {
        if (state.selectedTab == LibraryTab.ALL) {
            item(key = "reading") {
                Column(
                    Modifier.testTag("library_dashboard_reading"),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    LibrarySectionHeader(stringResource(Res.string.library_overview_reading)) {
                        onAction(LibraryOverviewAction.OnReadingSeeAllClick)
                    }
                    LibraryReadingRow(state.readingBooks, onAction)
                }
            }
            item(key = "summary") {
                Column(
                    Modifier.testTag("library_dashboard_summary"),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    LibrarySectionHeader(stringResource(Res.string.library_overview_my_books)) {
                        onAction(LibraryOverviewAction.OnMyBooksSeeAllClick)
                    }
                    LibrarySummaryRow(onAction)
                }
            }
            item(key = "collections") {
                Column(
                    Modifier.testTag("library_dashboard_collections"),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    LibrarySectionHeader(stringResource(Res.string.library_overview_collections)) {
                        onAction(LibraryOverviewAction.OnCollectionsSeeAllClick)
                    }
                    LibraryCollectionRow(onAction)
                }
            }
            item(key = "recent") {
                Column(Modifier.testTag("library_dashboard_recent"), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LibrarySectionHeader(stringResource(Res.string.library_overview_recent)) {
                        onAction(LibraryOverviewAction.OnComingSoonClick)
                    }
                    LibraryRecentRow(state.recentlyAdded, onAction)
                }
            }
        } else {
            item(key = "filtered_header") {
                Text(stringResource(libraryTabLabel(state.selectedTab)), style = MaterialTheme.typography.titleMedium)
            }
            if (state.books.isEmpty()) item(key = "empty") { LibraryOverviewEmpty() }
            items(state.books, key = LibraryBookUi::id) { book ->
                LibraryBookRow(book, onAction, showNotes = state.selectedTab == LibraryTab.NOTES)
            }
        }
    }
}

internal fun libraryTabLabel(tab: LibraryTab) =
    when (tab) {
        LibraryTab.ALL -> Res.string.library_overview_all
        LibraryTab.READING -> Res.string.library_overview_reading
        LibraryTab.SAVED -> Res.string.library_overview_saved
        LibraryTab.FINISHED -> Res.string.library_overview_finished
        LibraryTab.DOWNLOADS -> Res.string.library_overview_downloads
        LibraryTab.NOTES -> Res.string.library_overview_notes
    }

@Preview
@Composable
private fun LibraryOverviewPreview() {
    val books = libraryOverviewBooks()
    BookNestTheme {
        LibraryOverviewScreen(
            LibraryOverviewState(
                books = books,
                readingBooks = books.filter { it.isReading },
                recentlyAdded = books.sortedBy { it.addedDaysAgo }.take(2),
            ),
            {},
        )
    }
}
