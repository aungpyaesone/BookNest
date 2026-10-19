package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_all_books_grid
import com.nps.booknest.feature.library.presentation.generated.resources.library_all_books_list
import com.nps.booknest.feature.library.presentation.generated.resources.library_all_books_title
import com.nps.booknest.feature.library.presentation.generated.resources.library_all_books_total
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_books_count
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_hero
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AllBooksHero(total: Int) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(Res.string.library_all_books_title), style = MaterialTheme.typography.headlineSmall)
            Text(
                pluralStringResource(Res.plurals.library_all_books_total, total, total),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Image(painterResource(Res.drawable.library_browse_hero), null, Modifier.size(112.dp, 92.dp))
    }
}

@Composable
internal fun AllBooksResultsControls(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val isStacked = maxWidth < 380.dp || LocalDensity.current.fontScale >= 1.3f
        if (isStacked) {
            Column(Modifier.fillMaxWidth()) {
                AllBooksSort(state, onAction)
                AllBooksViewToggle(state, onAction, Modifier.align(Alignment.End))
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { AllBooksSort(state, onAction) }
                AllBooksViewToggle(state, onAction)
            }
        }
    }
}

@Composable
private fun AllBooksSort(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
) {
    LibraryBrowseSortRow(
        pluralStringResource(Res.plurals.library_browse_books_count, state.books.size, state.books.size),
        state.sort,
        state.availableSorts(),
    ) { onAction(LibraryBooksAction.OnSortSelect(it)) }
}

@Composable
private fun AllBooksViewToggle(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        listOf(true, false).forEach { isGrid ->
            IconToggleButton(
                checked = state.isGridView == isGrid,
                onCheckedChange = { onAction(LibraryBooksAction.OnViewModeSelect(isGrid)) },
                modifier = Modifier.testTag(if (isGrid) "all_books_grid_toggle" else "all_books_list_toggle"),
                colors =
                    IconButtonDefaults.iconToggleButtonColors(
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Icon(
                    if (isGrid) Icons.Outlined.GridView else Icons.AutoMirrored.Outlined.ViewList,
                    stringResource(
                        if (isGrid) Res.string.library_all_books_grid else Res.string.library_all_books_list,
                    ),
                    Modifier.size(20.dp),
                )
            }
        }
    }
}
