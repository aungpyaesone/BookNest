package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSelectionChip
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_apply
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_category
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_collection_type
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_reset
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_status
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryBooksFilters(
    state: LibraryBooksState,
    onAction: (LibraryBooksAction) -> Unit,
) {
    LibraryBrowseFilterSheet(
        onDismiss = { onAction(LibraryBooksAction.OnFilterDismiss) },
        onReset = { onAction(LibraryBooksAction.OnFilterReset) },
        onApply = { onAction(LibraryBooksAction.OnFilterApply) },
    ) {
        if (state.mode != LibraryBooksMode.READING) {
            Text(stringResource(Res.string.library_browse_status), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                libraryBrowseStatuses.forEach { status ->
                    BookNestSelectionChip(
                        stringResource(status.browseLabel()),
                        state.draftStatus == status,
                        {
                            onAction(LibraryBooksAction.OnDraftStatusSelect(status))
                        },
                        Modifier.testTag(
                            "library_filter_status_${status.name}",
                        ),
                        textStyle = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
        Text(stringResource(Res.string.library_browse_category), style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (listOf(null) + LibraryCategory.entries).forEach { category ->
                BookNestSelectionChip(
                    stringResource(category.label()),
                    state.draftCategory == category,
                    {
                        onAction(LibraryBooksAction.OnDraftCategorySelect(category))
                    },
                    Modifier.testTag(
                        "library_filter_category_${category?.name ?: "ALL"}",
                    ),
                    textStyle = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
internal fun CollectionsFilters(
    state: CollectionsState,
    onAction: (CollectionsAction) -> Unit,
) {
    LibraryBrowseFilterSheet(
        onDismiss = { onAction(CollectionsAction.OnFilterDismiss) },
        onReset = { onAction(CollectionsAction.OnFilterReset) },
        onApply = { onAction(CollectionsAction.OnFilterApply) },
    ) {
        Text(stringResource(Res.string.library_browse_collection_type), style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CollectionType.entries.forEach { type ->
                BookNestSelectionChip(stringResource(type.label()), state.draftType == type, {
                    onAction(CollectionsAction.OnDraftTypeSelect(type))
                }, Modifier.testTag("collection_filter_${type.name}"), textStyle = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryBrowseFilterSheet(
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(16.dp)
                .testTag("library_browse_filter_sheet"),
        ) {
            Column(
                Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                content()
            }
            Row(Modifier.fillMaxWidth()) {
                TextButton(onReset, Modifier.weight(1f).testTag("library_browse_filter_reset")) {
                    Text(stringResource(Res.string.library_browse_reset))
                }
                BookNestPrimaryButton(
                    stringResource(Res.string.library_browse_apply),
                    onApply,
                    Modifier.weight(1f).testTag("library_browse_filter_apply"),
                )
            }
        }
    }
}
