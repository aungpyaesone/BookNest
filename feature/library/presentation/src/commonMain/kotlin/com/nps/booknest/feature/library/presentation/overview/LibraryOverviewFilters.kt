package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_apply
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_recent
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_reset
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_sort
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_sort_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryOverviewFilters(
    state: LibraryOverviewState,
    onAction: (LibraryOverviewAction) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { onAction(LibraryOverviewAction.OnFilterDismiss) }) {
        Column(Modifier.fillMaxWidth().padding(BookNestSpacing.section).testTag("library_filter_sheet")) {
            Text(stringResource(Res.string.library_overview_sort), style = MaterialTheme.typography.titleMedium)
            LibrarySort.entries.forEach { sort ->
                FilterChip(
                    selected = state.draftSort == sort,
                    onClick = { onAction(LibraryOverviewAction.OnSortSelect(sort)) },
                    label = {
                        Text(
                            stringResource(
                                if (sort == LibrarySort.RECENT) {
                                    Res.string.library_overview_recent
                                } else {
                                    Res.string.library_overview_sort_title
                                },
                            ),
                        )
                    },
                    modifier = Modifier.testTag("library_sort_${sort.name}"),
                )
            }
            Row {
                TextButton(
                    onClick = { onAction(LibraryOverviewAction.OnFilterReset) },
                    modifier = Modifier.weight(1f).testTag("library_filter_reset"),
                ) { Text(stringResource(Res.string.library_overview_reset)) }
                BookNestPrimaryButton(
                    stringResource(Res.string.library_overview_apply),
                    { onAction(LibraryOverviewAction.OnFilterApply) },
                    Modifier.weight(1f).testTag("library_filter_apply"),
                )
            }
        }
    }
}
