package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSize
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.bookNestAdaptiveCompactTypography
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_continue_reading_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_more_options_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_my_library
import com.nps.booknest.feature.home.presentation.generated.resources.home_premium_body
import com.nps.booknest.feature.home.presentation.generated.resources.home_premium_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_progress_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_read_now
import com.nps.booknest.feature.home.presentation.generated.resources.home_upgrade_now
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ContinueReadingCard(
    book: ContinueReadingUi,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val progressDescription = stringResource(Res.string.home_progress_description, book.progressText)
    val useStackedLayout = layoutMode == HomeLayoutMode.COMPACT || LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium)) {
        SectionHeader(
            title = stringResource(Res.string.home_continue_reading_title),
            action = stringResource(Res.string.home_my_library),
            onActionClick = { onAction(HomeAction.OnLibraryClick) },
            layoutMode = layoutMode,
        )
        Card(
            modifier = Modifier.testTag(HomeTestTags.CONTINUE_READING_CARD),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            if (useStackedLayout) {
                CompactContinueReadingContent(book, progressDescription, onAction)
            } else {
                WideContinueReadingContent(book, progressDescription, onAction)
            }
        }
    }
}

@Composable
private fun CompactContinueReadingContent(
    book: ContinueReadingUi,
    progressDescription: String,
    onAction: (HomeAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(BookNestSpacing.large),
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiniBookCover(modifier = Modifier.width(56.dp).height(84.dp))
            ReadingDetails(
                book = book,
                progressDescription = progressDescription,
                modifier = Modifier.weight(1f),
            )
            MoreOptionsButton(book = book, onAction = onAction)
        }
        BookNestPrimaryButton(
            text = stringResource(Res.string.home_read_now),
            onClick = { onAction(HomeAction.OnReadNowClick(book.bookId)) },
            modifier = Modifier.fillMaxWidth().testTag(HomeTestTags.READ_NOW_ACTION),
        )
    }
}

@Composable
private fun WideContinueReadingContent(
    book: ContinueReadingUi,
    progressDescription: String,
    onAction: (HomeAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(BookNestSpacing.large),
        horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiniBookCover(modifier = Modifier.width(64.dp).height(96.dp))
        ReadingDetails(
            book = book,
            progressDescription = progressDescription,
            modifier = Modifier.weight(1f),
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        ) {
            MoreOptionsButton(book = book, onAction = onAction)
            BookNestPrimaryButton(
                text = stringResource(Res.string.home_read_now),
                onClick = { onAction(HomeAction.OnReadNowClick(book.bookId)) },
                modifier = Modifier.testTag(HomeTestTags.READ_NOW_ACTION),
            )
        }
    }
}

@Composable
private fun ReadingDetails(
    book: ContinueReadingUi,
    progressDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
    ) {
        Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            text = book.author,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        ) {
            LinearProgressIndicator(
                progress = { book.progress },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .semantics { contentDescription = progressDescription },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Text(text = book.progressText, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun MoreOptionsButton(
    book: ContinueReadingUi,
    onAction: (HomeAction) -> Unit,
) {
    IconButton(
        onClick = { onAction(HomeAction.OnBookClick(book.bookId)) },
        modifier = Modifier.size(BookNestSize.minimumTouchTarget),
    ) {
        Icon(
            Icons.Outlined.MoreVert,
            contentDescription = stringResource(Res.string.home_more_options_description),
        )
    }
}

@Composable
private fun MiniBookCover(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .background(Brush.verticalGradient(listOf(Color(0xFF1C5671), Color(0xFF082C42)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Outlined.AutoStories,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(BookNestSize.iconLarge),
        )
    }
}

@Composable
internal fun PremiumBanner(
    onUpgradeClick: () -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val useStackedLayout = layoutMode == HomeLayoutMode.COMPACT || LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    Card(
        modifier = modifier.testTag(HomeTestTags.PREMIUM_BANNER),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (useStackedLayout) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(BookNestSpacing.large),
                verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PremiumIcon(size = 44.dp)
                    PremiumCopy(modifier = Modifier.weight(1f))
                }
                BookNestPrimaryButton(
                    text = stringResource(Res.string.home_upgrade_now),
                    onClick = onUpgradeClick,
                    modifier = Modifier.fillMaxWidth().testTag(HomeTestTags.UPGRADE_ACTION),
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(BookNestSpacing.large),
                horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PremiumIcon(size = 48.dp)
                PremiumCopy(modifier = Modifier.weight(1f))
                BookNestPrimaryButton(
                    text = stringResource(Res.string.home_upgrade_now),
                    onClick = onUpgradeClick,
                    modifier = Modifier.testTag(HomeTestTags.UPGRADE_ACTION),
                )
            }
        }
    }
}

@Composable
private fun PremiumIcon(size: Dp) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(size)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Outlined.WorkspacePremium,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun PremiumCopy(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = stringResource(Res.string.home_premium_title), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(Res.string.home_premium_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    action: String,
    onActionClick: () -> Unit,
    layoutMode: HomeLayoutMode,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style =
                if (layoutMode ==
                    HomeLayoutMode.COMPACT
                ) {
                    bookNestAdaptiveCompactTypography().titleMedium
                } else {
                    bookNestAdaptiveCompactTypography().titleLarge
                },
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier =
                Modifier
                    .defaultMinSize(minHeight = BookNestSize.minimumTouchTarget)
                    .clickable(onClick = onActionClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = action,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style =
                    if (layoutMode ==
                        HomeLayoutMode.COMPACT
                    ) {
                        bookNestAdaptiveCompactTypography().labelSmall
                    } else {
                        bookNestAdaptiveCompactTypography().bodySmall
                    },
            )
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val LARGE_FONT_SCALE = 1.3f
