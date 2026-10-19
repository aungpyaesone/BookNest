package com.nps.booknest.feature.library.presentation.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.bookNestBookmarkStarIcon
import com.nps.booknest.core.designsystem.bookNestBookshelfIcon
import com.nps.booknest.core.designsystem.bookNestCircledCheckIcon
import com.nps.booknest.core.designsystem.bookNestDownloadTrayIcon
import com.nps.booknest.core.designsystem.bookNestOpenBookIcon
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_collection_business
import com.nps.booknest.feature.library.presentation.generated.resources.library_collection_personal
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_business
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_collection_count
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_empty
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_personal
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_see_all
import com.nps.booknest.feature.library.presentation.generated.resources.library_overview_total
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LibrarySectionHeader(
    title: String,
    onSeeAll: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            Modifier.weight(1f).alignByBaseline(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        TextButton(onClick = onSeeAll, modifier = Modifier.alignByBaseline()) {
            Text(stringResource(Res.string.library_overview_see_all), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
internal fun LibrarySummaryRow(onAction: (LibraryOverviewAction) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("library_summary_section")) {
        val isTablet = libraryIsTablet()
        val cardWidth = ((maxWidth - BookNestSpacing.extraSmall * 4) / 5).coerceAtLeast(if (isTablet) 88.dp else 64.dp)
        Row(
            Modifier.horizontalScroll(rememberScrollState()).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
        ) {
            librarySummaryCounts.forEach { (tab, count) ->
                ElevatedCard(
                    onClick = { onAction(LibraryOverviewAction.OnTabSelect(tab)) },
                    modifier =
                        Modifier
                            .width(
                                cardWidth,
                            ).heightIn(min = if (isTablet) 112.dp else 96.dp)
                            .fillMaxHeight()
                            .testTag("library_summary_${tab.name}"),
                    shape =
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(6.dp),
                    colors =
                        CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
                    ) {
                        Icon(
                            librarySummaryIcon(tab),
                            null,
                            Modifier.size(if (isTablet) 28.dp else 24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(count.toString(), style = MaterialTheme.typography.titleSmall)
                        Text(
                            stringResource(
                                if (tab == LibraryTab.ALL) Res.string.library_overview_total else libraryTabLabel(tab),
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            minLines = 2,
                        )
                    }
                }
            }
        }
    }
}

private fun librarySummaryIcon(tab: LibraryTab) =
    when (tab) {
        LibraryTab.ALL -> bookNestBookshelfIcon
        LibraryTab.READING -> bookNestOpenBookIcon
        LibraryTab.SAVED -> bookNestBookmarkStarIcon
        LibraryTab.FINISHED -> bookNestCircledCheckIcon
        LibraryTab.DOWNLOADS -> bookNestDownloadTrayIcon
        LibraryTab.NOTES -> bookNestOpenBookIcon
    }

@Composable
internal fun LibraryCollectionRow(onAction: (LibraryOverviewAction) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("library_collections_section")) {
        val cardWidth = ((maxWidth - BookNestSpacing.medium) / 2).coerceAtLeast(156.dp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.medium)) {
            item(key = "personal") {
                LibraryCollectionCard(
                    Res.string.library_overview_personal,
                    12,
                    Res.drawable.library_collection_personal,
                    onAction,
                    Modifier.width(cardWidth),
                )
            }
            item(key = "business") {
                LibraryCollectionCard(
                    Res.string.library_overview_business,
                    8,
                    Res.drawable.library_collection_business,
                    onAction,
                    Modifier.width(cardWidth),
                )
            }
        }
    }
}

@Composable
private fun LibraryCollectionCard(
    title: StringResource,
    count: Int,
    artwork: DrawableResource,
    onAction: (LibraryOverviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onAction(LibraryOverviewAction.OnComingSoonClick) },
        modifier = modifier,
        shape =
            androidx.compose.foundation.shape
                .RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(artwork),
                null,
                Modifier
                    .width(
                        if (libraryIsTablet()) 64.dp else 48.dp,
                    ).height(if (libraryIsTablet()) 104.dp else 84.dp),
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.weight(1f).padding(BookNestSpacing.small)) {
                Text(stringResource(title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(Res.string.library_overview_collection_count, count),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
internal fun LibraryOverviewEmpty() {
    Text(
        stringResource(Res.string.library_overview_empty),
        Modifier.fillMaxWidth().padding(vertical = BookNestSpacing.section).testTag("library_overview_empty"),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
