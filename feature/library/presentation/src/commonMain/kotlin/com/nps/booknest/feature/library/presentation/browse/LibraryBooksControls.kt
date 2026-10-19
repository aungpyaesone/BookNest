package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSearchField
import com.nps.booknest.core.designsystem.BookNestSelectionChip
import com.nps.booknest.core.designsystem.bookNestFunnelIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_filter
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_more_categories
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_search_books
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_search_reading
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryBooksSearch(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder =
        stringResource(
            if (state.mode ==
                LibraryBooksMode.READING
            ) {
                Res.string.library_browse_search_reading
            } else {
                Res.string.library_browse_search_books
            },
        )
    BookNestSearchField(
        state.query,
        placeholder,
        placeholder,
        { onAction(LibraryBooksAction.OnSearchChange(it)) },
        modifier.testTag("library_browse_search"),
        trailingContent = {
            IconButton({ onAction(LibraryBooksAction.OnFilterClick) }, Modifier.testTag("library_browse_filter")) {
                Icon(bookNestFunnelIcon, stringResource(Res.string.library_browse_filter), Modifier.size(20.dp))
            }
        },
    )
}

@Composable
internal fun LibraryBooksChips(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
) {
    val labelHeight = with(LocalDensity.current) { (MaterialTheme.typography.labelSmall.lineHeight * 2).toDp() }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.mode == LibraryBooksMode.READING) {
            items(readingChipCategories, key = { it?.name ?: "ALL" }) { category ->
                BookNestSelectionChip(
                    stringResource(category.label()),
                    state.category == category,
                    {
                        onAction(LibraryBooksAction.OnCategorySelect(category))
                    },
                    Modifier.testTag(
                        "library_browse_category_${category?.name ?: "ALL"}",
                    ),
                    textStyle = MaterialTheme.typography.labelSmall,
                )
            }
            item(key = "more") {
                BookNestSelectionChip(
                    stringResource(Res.string.library_browse_more_categories),
                    state.category != null && state.category !in readingChipCategories,
                    {
                        onAction(LibraryBooksAction.OnFilterClick)
                    },
                    Modifier.testTag("library_browse_more_categories"),
                    textStyle = MaterialTheme.typography.labelSmall,
                )
            }
        } else {
            items(libraryBrowseStatuses, key = { it.name }) { status ->
                BookNestSelectionChip(
                    stringResource(status.browseLabel()) +
                        if (state.mode == LibraryBooksMode.MY_BOOKS) "\n" + (state.statusCounts[status] ?: 0) else "",
                    state.status == status,
                    {
                        onAction(LibraryBooksAction.OnStatusSelect(status))
                    },
                    Modifier
                        .heightIn(min = if (state.mode == LibraryBooksMode.MY_BOOKS) labelHeight + 16.dp else 32.dp)
                        .testTag("library_browse_status_${status.name}"),
                    textAlign = TextAlign.Center,
                    textStyle = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

internal val readingChipCategories =
    listOf(
        null,
        LibraryCategory.FICTION,
        LibraryCategory.EDUCATION,
        LibraryCategory.BUSINESS,
        LibraryCategory.SELF_HELP,
    )
