@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.VolunteerActivism
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_categories_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_all
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_business
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_education
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_fiction
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_more
import com.nps.booknest.feature.home.presentation.generated.resources.home_category_self_help
import com.nps.booknest.feature.home.presentation.generated.resources.home_see_all
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeHeroCarousel(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
) {
    val pagerState = rememberPagerState(initialPage = state.currentHeroPage, pageCount = { HERO_PAGE_COUNT })
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(state.currentHeroPage) {
        if (pagerState.currentPage != state.currentHeroPage) pagerState.animateScrollToPage(state.currentHeroPage)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> currentOnAction(HomeAction.OnHeroPageChange(page)) }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState, pageSpacing = BookNestSpacing.small) { page ->
            HeroCard(page, { onAction(HomeAction.OnExploreClick) }, layoutMode)
        }
        Row(
            modifier = Modifier.padding(top = BookNestSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
        ) {
            repeat(HERO_PAGE_COUNT) { page ->
                val isSelected = page == state.currentHeroPage
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
                            ).clickable { onAction(HomeAction.OnHeroPageChange(page)) },
                )
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
    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium)) {
        SectionHeader(
            title = stringResource(Res.string.home_categories_title),
            action = stringResource(Res.string.home_see_all),
            onActionClick = { onAction(HomeAction.OnCategoryClick(HomeCategory.ALL)) },
            layoutMode = layoutMode,
        )
        val categories = HomeCategory.entries
        if (layoutMode != HomeLayoutMode.COMPACT) {
            Row(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
                categories.forEach { category ->
                    CategoryItem(
                        category = category,
                        isSelected = state.selectedCategory == category,
                        onAction = onAction,
                        layoutMode = layoutMode,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
                items(categories, key = HomeCategory::name) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = state.selectedCategory == category,
                        onAction = onAction,
                        layoutMode = layoutMode,
                        modifier = Modifier.width(64.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: HomeCategory,
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
                    imageVector = categoryIcon(category),
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
            text = categoryLabel(category),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun categoryIcon(category: HomeCategory): ImageVector =
    when (category) {
        HomeCategory.ALL -> Icons.Outlined.GridView
        HomeCategory.FICTION -> Icons.Outlined.AutoStories
        HomeCategory.EDUCATION -> Icons.Outlined.School
        HomeCategory.BUSINESS -> Icons.Outlined.BusinessCenter
        HomeCategory.SELF_HELP -> Icons.Outlined.VolunteerActivism
        HomeCategory.MORE -> Icons.Outlined.MoreHoriz
    }

@Composable
private fun categoryLabel(category: HomeCategory): String =
    stringResource(
        when (category) {
            HomeCategory.ALL -> Res.string.home_category_all
            HomeCategory.FICTION -> Res.string.home_category_fiction
            HomeCategory.EDUCATION -> Res.string.home_category_education
            HomeCategory.BUSINESS -> Res.string.home_category_business
            HomeCategory.SELF_HELP -> Res.string.home_category_self_help
            HomeCategory.MORE -> Res.string.home_category_more
        },
    )
