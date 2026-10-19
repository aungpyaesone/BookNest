package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_all
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_category_description
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_coming_soon
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_empty_books
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_hero_books
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryBooksRoot(
    categoryId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryBooksViewModel = koinViewModel(key = categoryId, parameters = { parametersOf(categoryId) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val comingSoon = stringResource(Res.string.explore_coming_soon)
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CategoryBooksEvent.NavigateBack -> onNavigateBack()
            CategoryBooksEvent.ShowComingSoon -> snackbar.showSnackbar(comingSoon)
        }
    }
    CategoryBooksScreen(state, viewModel::onAction, modifier, snackbar)
}

@Composable
fun CategoryBooksScreen(
    state: CategoryBooksState,
    onAction: (CategoryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.testTag("category_books_screen"),
        topBar = {
            ExploreAppBar(state.category?.name.orEmpty(), onBack = { onAction(CategoryBooksAction.OnBackClick) })
        },
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        if (state.hasShownCatalog) {
            CategoryBookGrid(state, onAction, Modifier.padding(padding))
        } else {
            ExploreInitialStatus(
                isLoading = state.isLoading,
                isRefreshing = state.isRefreshing,
                error = state.error,
                onRetry = { onAction(CategoryBooksAction.OnRefreshClick) },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun CategoryBookGrid(
    state: CategoryBooksState,
    onAction: (CategoryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    ObserveCatalogPagination(state, gridState, onAction)
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val horizontalPadding =
            if (maxWidth < 600.dp) BookNestSpacing.screenCompact else BookNestSpacing.screenExpanded
        val columns = if (maxWidth < 600.dp) GridCells.Fixed(3) else GridCells.Adaptive(120.dp)
        LazyVerticalGrid(
            columns = columns,
            state = gridState,
            modifier =
                Modifier
                    .widthIn(
                        max = BookNestSize.contentMaxWidth,
                    ).fillMaxSize()
                    .testTag("category_books_grid"),
            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = BookNestSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
        ) {
            state.category?.let { category ->
                item(key = "catalog_hero", span = { GridItemSpan(maxLineSpan) }) { CategoryHero(category) }
            }
            item(key = "catalog_chips", span = { GridItemSpan(maxLineSpan) }) { CategorySubcategories(state, onAction) }
            item(key = "catalog_toolbar", span = { GridItemSpan(maxLineSpan) }) { CategoryBookToolbar(state, onAction) }
            categoryBookResults(state, gridState, onAction)
        }
    }
}

private const val CATALOG_RESULTS_STATUS_KEY = "catalog_results_status"

private fun LazyGridScope.categoryBookResults(
    state: CategoryBooksState,
    gridState: LazyGridState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    if (!state.hasLoaded) {
        item(key = CATALOG_RESULTS_STATUS_KEY, span = { GridItemSpan(maxLineSpan) }) {
            CatalogResultsStatus(state, gridState, onAction)
        }
        return
    }
    state.error?.let { error ->
        item(key = "catalog_error", span = { GridItemSpan(maxLineSpan) }) {
            ExploreFeedStatus(state.isRefreshing, state.isShowingCachedData, error) {
                onAction(CategoryBooksAction.OnRefreshClick)
            }
        }
    }
    if (state.books.isEmpty()) {
        item(key = "catalog_empty", span = { GridItemSpan(maxLineSpan) }) {
            ExploreEmptyContent(stringResource(Res.string.explore_empty_books))
        }
    }
    items(state.books, key = ExploreBookUi::id) { book ->
        ExploreBookCard(book, { onAction(CategoryBooksAction.OnBookClick) })
    }
    if (state.isAppending || state.appendError != null) {
        item(key = "catalog_append", span = { GridItemSpan(maxLineSpan) }) {
            CatalogAppendStatus(state, onAction)
        }
    }
}

@Composable
private fun CatalogResultsStatus(
    state: CategoryBooksState,
    gridState: LazyGridState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    // Lazy-grid geometry is Compose-owned state; keep the status inside the remaining results viewport.
    val remainingHeight by remember(gridState) {
        derivedStateOf {
            val layout = gridState.layoutInfo
            val top =
                layout.visibleItemsInfo
                    .firstOrNull { it.key == CATALOG_RESULTS_STATUS_KEY }
                    ?.offset
                    ?.y ?: 0
            (layout.viewportEndOffset - layout.afterContentPadding - top).coerceAtLeast(0)
        }
    }
    val minimumHeight =
        with(
            LocalDensity.current,
        ) { remainingHeight.toDp() }.coerceAtLeast(BookNestSize.minimumTouchTarget)
    ExploreInitialStatus(
        isLoading = state.isLoading,
        isRefreshing = state.isRefreshing,
        error = state.error,
        onRetry = { onAction(CategoryBooksAction.OnRefreshClick) },
        modifier = Modifier.fillMaxWidth().heightIn(min = minimumHeight).testTag("category_results_status"),
    )
}

@Composable
private fun ObserveCatalogPagination(
    state: CategoryBooksState,
    gridState: LazyGridState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    val currentState by rememberUpdatedState(state)
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(state.queryVersion) { gridState.scrollToItem(0) }
    LaunchedEffect(gridState) {
        snapshotFlow {
            val layout = gridState.layoutInfo
            val feed = currentState
            feed.hasLoaded && feed.hasMore && !feed.isRefreshing && !feed.isAppending && feed.appendError == null &&
                feed.error == null &&
                layout.totalItemsCount > 0 &&
                (layout.visibleItemsInfo.lastOrNull()?.index ?: -1) >= layout.totalItemsCount - 4
        }.distinctUntilChanged().filter { it }.collect { currentOnAction(CategoryBooksAction.OnLoadNextPage) }
    }
}

@Composable
private fun CategoryHero(category: ExploreCategoryUi) {
    BoxWithConstraints {
        val isCompact = maxWidth < 600.dp
        Card(
            modifier = Modifier.testTag("category_hero"),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth().padding(
                        horizontal = BookNestSpacing.large,
                        vertical = BookNestSpacing.medium,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1.3f),
                    verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
                ) {
                    Text(
                        category.name,
                        style =
                            if (isCompact) {
                                MaterialTheme.typography.titleLarge
                            } else {
                                MaterialTheme.typography.headlineSmall
                            },
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        stringResource(Res.string.explore_category_description),
                        style =
                            if (isCompact) {
                                MaterialTheme.typography.bodySmall
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                    )
                }
                AsyncImage(
                    model = category.bannerUrl,
                    placeholder = painterResource(Res.drawable.explore_hero_books),
                    error = painterResource(Res.drawable.explore_hero_books),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.weight(1f).height(if (isCompact) 112.dp else 144.dp),
                )
            }
        }
    }
}

@Composable
private fun CategorySubcategories(
    state: CategoryBooksState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag("category_subcategories"),
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
    ) {
        item(key = "all") {
            FilterChip(
                selected = state.selectedSubcategoryId == null,
                onClick = { onAction(CategoryBooksAction.OnSubcategoryClick(null)) },
                label = { Text(stringResource(Res.string.explore_all), style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.testTag("category_chip_all"),
            )
        }
        items(state.subcategories, key = { it.id }) { category ->
            FilterChip(
                selected = state.selectedSubcategoryId == category.id,
                onClick = { onAction(CategoryBooksAction.OnSubcategoryClick(category.id)) },
                label = { Text(category.name, style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.testTag("category_chip_${category.id}"),
            )
        }
    }
}

@Preview
@Composable
private fun CategoryBooksPreview() {
    BookNestTheme {
        CategoryBooksScreen(categoryBooksPreviewState(), {})
    }
}
