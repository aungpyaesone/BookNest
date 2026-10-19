package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestLogo
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_all
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_back
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_business
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_downloads
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_education
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_favorites
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_fiction
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_finance
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_finished
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_hero
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_lifestyle
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_more
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_my_collections
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_progress_sort
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_reading
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_recent_added
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_recent_read
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_recent_updated
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_saved
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_science_fiction
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_search
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_self_help
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_shared
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_sort
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_title_sort
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_notes
import com.nps.booknest.feature.library.presentation.overview.LibraryCategory
import com.nps.booknest.feature.library.presentation.overview.LibraryTab
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibraryBrowseToolbar(
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onMore: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onBack, Modifier.testTag("library_browse_back")) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                stringResource(Res.string.library_browse_back),
                Modifier.size(20.dp),
            )
        }
        BookNestLogo(
            "BookNest",
            Modifier.weight(1f),
            iconSize = 24.dp,
            textStyle = MaterialTheme.typography.titleMedium,
        )
        IconButton(onSearch, Modifier.testTag("library_browse_search_action")) {
            Icon(Icons.Outlined.Search, stringResource(Res.string.library_browse_search), Modifier.size(20.dp))
        }
        IconButton(onMore, Modifier.testTag("library_browse_more")) {
            Icon(Icons.Outlined.MoreVert, stringResource(Res.string.library_browse_more), Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun LibraryBrowseHero(
    title: StringResource,
    subtitle: StringResource,
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Image(
            painterResource(Res.drawable.library_browse_hero),
            null,
            Modifier.size(112.dp, 92.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
internal fun LibraryBrowseSortRow(
    count: String,
    sort: LibraryBrowseSort,
    options: List<LibraryBrowseSort>,
    onSelect: (LibraryBrowseSort) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(count, Modifier.weight(1f).testTag("library_browse_count"), style = MaterialTheme.typography.labelMedium)
        Column {
            TextButton(
                onClick = { isExpanded = true },
                modifier = Modifier.widthIn(max = 220.dp).testTag("library_browse_sort"),
            ) {
                Text(
                    stringResource(Res.string.library_browse_sort) + "  " + stringResource(sort.label()),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp))
            }
            DropdownMenu(isExpanded, { isExpanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.label())) },
                        onClick = {
                            isExpanded = false
                            onSelect(option)
                        },
                        modifier = Modifier.testTag("library_browse_sort_${option.name}"),
                    )
                }
            }
        }
    }
}

internal fun LibraryBrowseSort.label() =
    when (this) {
        LibraryBrowseSort.RECENT_READ -> Res.string.library_browse_recent_read
        LibraryBrowseSort.RECENT_ADDED -> Res.string.library_browse_recent_added
        LibraryBrowseSort.RECENT_UPDATED -> Res.string.library_browse_recent_updated
        LibraryBrowseSort.TITLE -> Res.string.library_browse_title_sort
        LibraryBrowseSort.PROGRESS -> Res.string.library_browse_progress_sort
    }

internal fun LibraryCategory?.label() =
    when (this) {
        null -> Res.string.library_browse_all
        LibraryCategory.FICTION -> Res.string.library_browse_fiction
        LibraryCategory.EDUCATION -> Res.string.library_browse_education
        LibraryCategory.BUSINESS -> Res.string.library_browse_business
        LibraryCategory.SELF_HELP -> Res.string.library_browse_self_help
        LibraryCategory.FINANCE -> Res.string.library_browse_finance
        LibraryCategory.LIFESTYLE -> Res.string.library_browse_lifestyle
        LibraryCategory.SCIENCE_FICTION -> Res.string.library_browse_science_fiction
    }

internal fun LibraryTab.browseLabel() =
    when (this) {
        LibraryTab.ALL -> Res.string.library_browse_all
        LibraryTab.READING -> Res.string.library_browse_reading
        LibraryTab.SAVED -> Res.string.library_browse_saved
        LibraryTab.FINISHED -> Res.string.library_browse_finished
        LibraryTab.DOWNLOADS -> Res.string.library_browse_downloads
        LibraryTab.NOTES -> Res.string.library_overview_notes
    }

internal fun CollectionType.label() =
    when (this) {
        CollectionType.ALL -> Res.string.library_browse_all
        CollectionType.MY -> Res.string.library_browse_my_collections
        CollectionType.SHARED -> Res.string.library_browse_shared
        CollectionType.FAVORITES -> Res.string.library_browse_favorites
    }
