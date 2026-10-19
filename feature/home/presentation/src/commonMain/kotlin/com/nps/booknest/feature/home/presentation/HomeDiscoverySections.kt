@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.core.designsystem.bookNestCategoryIcon
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_banner_page_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_categories_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_see_all
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeHeroCarousel(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
) {
    if (state.banners.isEmpty()) return
    val pagerState = rememberPagerState(initialPage = state.currentHeroPage, pageCount = { state.banners.size })
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(state.currentHeroPage, state.banners.size) {
        if (pagerState.currentPage != state.currentHeroPage) pagerState.animateScrollToPage(state.currentHeroPage)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> currentOnAction(HomeAction.OnHeroPageChange(page)) }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState, pageSpacing = BookNestSpacing.small) { page ->
            state.banners.getOrNull(page)?.let { banner ->
                HeroCard(banner, page, { onAction(HomeAction.OnBannerClick(banner.id)) }, layoutMode)
            }
        }
        Row(
            modifier = Modifier.padding(top = BookNestSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
        ) {
            repeat(state.banners.size) { page ->
                val isSelected = page == state.currentHeroPage
                val pageDescription =
                    stringResource(Res.string.home_banner_page_description, page + 1, state.banners.size)
                Box(
                    modifier =
                        Modifier
                            .size(BookNestSize.minimumTouchTarget)
                            .testTag("home_hero_indicator_$page")
                            .semantics { contentDescription = pageDescription }
                            .selectable(isSelected, role = Role.Tab) { onAction(HomeAction.OnHeroPageChange(page)) },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                                ),
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeCategories(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
        SectionHeader(
            title = stringResource(Res.string.home_categories_title),
            action = stringResource(Res.string.home_see_all),
            onActionClick = { onAction(HomeAction.OnCategorySeeAllClick) },
            layoutMode = layoutMode,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
            items(state.categories, key = { it.id?.toString() ?: it.iconKey }) { category ->
                CategoryItem(
                    category = category,
                    isSelected = category == homeAllCategory,
                    onAction = onAction,
                    layoutMode = layoutMode,
                    modifier = Modifier.width(if (layoutMode == HomeLayoutMode.COMPACT) 72.dp else 96.dp),
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: HomeCategoryUi,
    isSelected: Boolean,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val surfaceSize =
        when (layoutMode) {
            HomeLayoutMode.COMPACT -> 44.dp
            HomeLayoutMode.MEDIUM -> 48.dp
            HomeLayoutMode.EXPANDED -> 52.dp
        }
    Column(
        modifier =
            modifier
                .defaultMinSize(minWidth = BookNestSize.minimumTouchTarget)
                .selectable(
                    selected = isSelected,
                    role = Role.Tab,
                    onClick = { onAction(HomeAction.OnCategoryClick(category)) },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
    ) {
        Surface(
            modifier = Modifier.size(surfaceSize),
            shape = MaterialTheme.shapes.small,
            color =
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
            tonalElevation = if (isSelected) 2.dp else 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = categoryIcon(category.iconKey),
                    contentDescription = null,
                    tint =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    modifier = Modifier.size(BookNestSize.iconSmall),
                )
            }
        }
        Text(
            text = category.label.asString(),
            style = bookNestAdaptiveCompactTypography().labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun categoryIcon(iconKey: String): ImageVector = bookNestCategoryIcon(iconKey)
