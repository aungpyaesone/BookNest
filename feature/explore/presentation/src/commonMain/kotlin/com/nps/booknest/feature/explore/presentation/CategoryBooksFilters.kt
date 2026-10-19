package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.nps.booknest.feature.explore.domain.CatalogSort
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_books_count
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_sort
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_sort_low
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_sort_popular
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_sort_recent
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_sort_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CategoryBookToolbar(
    state: CategoryBooksState,
    onAction: (CategoryBooksAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) {
            if (state.hasLoaded) {
                Text(
                    stringResource(Res.string.explore_books_count, state.total),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.testTag("category_books_count"),
                )
            }
        }
        Box {
            TextButton(onClick = {
                onAction(CategoryBooksAction.OnSortClick)
            }, modifier = Modifier.testTag("category_sort_action")) {
                Text(stringResource(sortLabel(state.sort)), style = MaterialTheme.typography.labelSmall)
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = stringResource(Res.string.explore_sort))
            }
            DropdownMenu(
                expanded = state.isSortVisible,
                onDismissRequest = { onAction(CategoryBooksAction.OnSortDismiss) },
            ) {
                CatalogSort.entries.forEach { sort ->
                    DropdownMenuItem(text = {
                        Text(stringResource(sortLabel(sort)))
                    }, onClick = { onAction(CategoryBooksAction.OnSortSelect(sort)) })
                }
            }
        }
    }
}

private fun sortLabel(sort: CatalogSort) =
    when (sort) {
        CatalogSort.POPULAR -> Res.string.explore_sort_popular
        CatalogSort.PRICE -> Res.string.explore_sort_low
        CatalogSort.RECENT -> Res.string.explore_sort_recent
        CatalogSort.TITLE -> Res.string.explore_sort_title
    }
