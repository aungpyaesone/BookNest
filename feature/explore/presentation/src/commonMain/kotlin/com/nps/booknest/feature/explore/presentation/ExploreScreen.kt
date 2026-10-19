package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestAdaptiveText
import com.nps.booknest.core.designsystem.BookNestSearchField
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.core.designsystem.bookNestCatalogColumns
import com.nps.booknest.core.designsystem.bookNestCategoryIcon
import com.nps.booknest.core.designsystem.bookNestIsTablet
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_empty_categories
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_no_categories
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_search_categories
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreRoot(
    onNavigateToCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ExploreEvent.NavigateToCategory -> onNavigateToCategory(event.categoryId)
        }
    }
    ExploreScreen(state, viewModel::onAction, modifier)
}

@Composable
fun ExploreScreen(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BookNestAdaptiveText {
        Scaffold(
            modifier = modifier.testTag("explore_screen"),
            topBar = {
                ExploreAppBar(stringResource(Res.string.explore_title))
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                val horizontalPadding =
                    if (maxWidth <
                        600.dp
                    ) {
                        BookNestSpacing.screenCompact
                    } else {
                        BookNestSpacing.screenExpanded
                    }
                val viewportWidth = maxWidth
                val searchShape = if (maxWidth < 600.dp) MaterialTheme.shapes.medium else MaterialTheme.shapes.large
                Column(
                    Modifier
                        .widthIn(
                            max = BookNestSize.contentMaxWidth,
                        ).fillMaxSize()
                        .padding(horizontal = horizontalPadding),
                ) {
                    BookNestSearchField(
                        query = state.query,
                        placeholder = stringResource(Res.string.explore_search_categories),
                        contentDescription = stringResource(Res.string.explore_search_categories),
                        onQueryChange = { onAction(ExploreAction.OnSearchChange(it)) },
                        modifier = Modifier.padding(bottom = BookNestSpacing.small).testTag("explore_category_search"),
                        shape = searchShape,
                    )
                    if (state.hasLoaded) {
                        state.error?.let { error ->
                            ExploreFeedStatus(state.isRefreshing, state.isShowingCachedData, error) {
                                onAction(ExploreAction.OnRefreshClick)
                            }
                        }
                        ExploreCategoryGrid(state, onAction, viewportWidth, Modifier.weight(1f))
                    } else {
                        ExploreInitialStatus(
                            isLoading = state.isLoading,
                            isRefreshing = state.isRefreshing,
                            error = state.error,
                            onRetry = { onAction(ExploreAction.OnRefreshClick) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreCategoryGrid(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
    viewportWidth: Dp,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val columns =
            GridCells.Fixed(
                bookNestCatalogColumns(
                    viewportWidth,
                    maxWidth,
                    LocalDensity.current.fontScale,
                    if (bookNestIsTablet()) 116.dp else 96.dp,
                ),
            )
        LazyVerticalGrid(
            columns = columns,
            modifier = Modifier.fillMaxSize().testTag("explore_category_grid"),
            contentPadding = PaddingValues(vertical = BookNestSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        ) {
            if (state.hasLoaded && state.categories.isEmpty()) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    ExploreEmptyContent(
                        stringResource(
                            if (state.query.isBlank()) {
                                Res.string.explore_no_categories
                            } else {
                                Res.string.explore_empty_categories
                            },
                        ),
                    )
                }
            }
            items(state.categories, key = ExploreCategoryUi::id) { category ->
                ExploreCategoryCard(category, { onAction(ExploreAction.OnCategoryClick(category.id)) })
            }
        }
    }
}

@Composable
private fun ExploreCategoryCard(
    category: ExploreCategoryUi,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 88.dp).testTag("explore_category_${category.id}"),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth().padding(
                    horizontal = BookNestSpacing.small,
                    vertical = BookNestSpacing.extraSmall,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
        ) {
            Icon(
                bookNestCategoryIcon(category.iconKey),
                null,
                Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                category.name,
                style = bookNestAdaptiveCompactTypography().labelSmall,
                textAlign = TextAlign.Center,
                minLines = 3,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun ExplorePreview() {
    BookNestTheme { ExploreScreen(explorePreviewState(), {}) }
}
