package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_books_count
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_coming_soon
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_empty_books
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_empty_reading
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_my_books
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_reading
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_subtitle
import com.nps.booknest.feature.library.presentation.libraryTypography
import com.nps.booknest.feature.library.presentation.overview.libraryOverviewBooks
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.floor

@Composable
fun ReadingNowRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReadingNowViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LibraryBooksRoot(state, viewModel::onAction, viewModel.events, onNavigateBack, modifier)
}

@Composable
fun MyBooksRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyBooksViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LibraryBooksRoot(state, viewModel::onAction, viewModel.events, onNavigateBack, modifier)
}

@Composable
internal fun LibraryBooksRoot(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    events: Flow<LibraryBrowseEvent>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbar = remember { SnackbarHostState() }
    val message = stringResource(Res.string.library_browse_coming_soon)
    ObserveAsEvents(events) { event ->
        when (event) {
            LibraryBrowseEvent.NavigateBack -> onNavigateBack()
            LibraryBrowseEvent.ShowComingSoon -> snackbar.showSnackbar(message)
        }
    }
    LibraryBooksScreen(state, onAction, modifier, snackbar)
}

@Composable
fun ReadingNowScreen(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryBooksScreen(state, onAction, modifier)
}

@Composable
fun MyBooksScreen(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryBooksScreen(state, onAction, modifier)
}

@Composable
internal fun LibraryBooksScreen(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    val focus = remember { FocusRequester() }
    var shouldFocus by remember { mutableStateOf(false) }
    val grid = rememberLazyGridState()
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(state.isSearchVisible, shouldFocus) {
        if (state.isSearchVisible &&
            shouldFocus
        ) {
            grid.scrollToItem(0)
            withFrameNanos {}
            focus.requestFocus()
            keyboard?.show()
            shouldFocus =
                false
        }
    }
    MaterialTheme(typography = libraryTypography()) {
        Scaffold(
            modifier = modifier.imePadding().testTag("library_books_${state.mode.name}"),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbar) },
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                val isTablet = maxWidth >= 600.dp
                Column(
                    Modifier.widthIn(max = 1200.dp).fillMaxSize().padding(horizontal = if (isTablet) 32.dp else 16.dp),
                ) {
                    LibraryBrowseToolbar(
                        onBack = { onAction(LibraryBooksAction.OnBackClick) },
                        onSearch = {
                            shouldFocus = true
                            onAction(LibraryBooksAction.OnSearchClick)
                        },
                        onMore = { onAction(LibraryBooksAction.OnComingSoonClick) },
                    )
                    LibraryBooksGrid(state, onAction, focus, isTablet, grid, Modifier.weight(1f))
                }
            }
        }
        if (state.isFilterVisible) LibraryBooksFilters(state, onAction)
    }
}

@Composable
private fun LibraryBooksGrid(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    focus: FocusRequester,
    isTablet: Boolean,
    grid: LazyGridState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.query, state.category, state.status, state.sort, state.isGridView) { grid.scrollToItem(0) }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val minimumWidth = (if (isTablet) 384f else 320f) * LocalDensity.current.fontScale.coerceAtLeast(1f)
        val columns =
            if (state.mode == LibraryBooksMode.ALL_BOOKS && state.isGridView) {
                allBooksGridColumns(maxWidth.value, isTablet, LocalDensity.current.fontScale)
            } else if (isTablet) {
                floor((maxWidth.value + 12f) / (minimumWidth + 12f)).toInt().coerceIn(1, 2)
            } else {
                1
            }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = grid,
            modifier = Modifier.fillMaxSize().testTag("library_browse_grid"),
            horizontalArrangement = Arrangement.spacedBy(if (state.mode == LibraryBooksMode.ALL_BOOKS) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (state.mode == LibraryBooksMode.ALL_BOOKS) 12.dp else 8.dp),
        ) {
            item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                LibraryBooksHeader(state, onAction, focus)
            }
            if (state.books.isEmpty()) {
                item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        stringResource(
                            if (state.mode == LibraryBooksMode.READING && state.query.isBlank() &&
                                state.category == null
                            ) {
                                Res.string.library_browse_empty_reading
                            } else {
                                Res.string.library_browse_empty_books
                            },
                        ),
                        Modifier.padding(vertical = 24.dp).testTag("library_browse_empty"),
                    )
                }
            }
            items(state.books, key = {
                it.id
            }) { book ->
                if (state.mode == LibraryBooksMode.ALL_BOOKS) {
                    if (state.isGridView) {
                        AllBooksGridCard(book) { onAction(LibraryBooksAction.OnComingSoonClick) }
                    } else {
                        MyBooksBookCard(book) { onAction(LibraryBooksAction.OnComingSoonClick) }
                    }
                } else {
                    LibraryBrowseBookCard(book, state.mode) { onAction(LibraryBooksAction.OnComingSoonClick) }
                }
            }
            item(key = "bottom", span = {
                GridItemSpan(maxLineSpan)
            }) {
                androidx.compose.foundation.layout
                    .Spacer(Modifier.padding(bottom = 12.dp))
            }
        }
    }
}

@Preview
@Composable
private fun ReadingNowScreenPreview() {
    BookNestTheme {
        ReadingNowScreen(
            LibraryBooksState(
                LibraryBooksMode.READING,
                books =
                    libraryOverviewBooks().filter {
                        it.isReading
                    },
            ),
            {},
        )
    }
}

@Preview
@Composable
private fun MyBooksScreenPreview() {
    BookNestTheme { MyBooksScreen(LibraryBooksState(LibraryBooksMode.MY_BOOKS, books = libraryOverviewBooks()), {}) }
}

@Composable
private fun LibraryBooksHeader(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    focus: FocusRequester,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.mode == LibraryBooksMode.ALL_BOOKS) {
            AllBooksHero(state.totalBooks)
        } else {
            LibraryBrowseHero(
                if (state.mode ==
                    LibraryBooksMode.READING
                ) {
                    Res.string.library_browse_reading
                } else {
                    Res.string.library_browse_my_books
                },
                Res.string.library_browse_subtitle,
            )
        }
        if (state.isSearchVisible) LibraryBooksSearch(state, onAction, Modifier.focusRequester(focus))
        LibraryBooksChips(state, onAction)
        if (state.mode == LibraryBooksMode.ALL_BOOKS) {
            AllBooksResultsControls(state, onAction)
        } else {
            LibraryBrowseSortRow(
                pluralStringResource(
                    Res.plurals.library_browse_books_count,
                    state.books.size,
                    state.books.size,
                ),
                state.sort,
                state.availableSorts(),
            ) {
                onAction(LibraryBooksAction.OnSortSelect(it))
            }
        }
    }
}
