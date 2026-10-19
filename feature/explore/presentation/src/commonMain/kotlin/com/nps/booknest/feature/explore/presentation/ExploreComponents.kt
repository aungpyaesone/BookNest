package com.nps.booknest.feature.explore.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.explore.presentation.generated.resources.Res
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_back
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_cover_description
import com.nps.booknest.feature.explore.presentation.generated.resources.explore_demo
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExploreAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("explore_back")) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(Res.string.explore_back))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    )
}

@Composable
internal fun ExploreBookCard(
    book: ExploreBookUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDetails: Boolean = true,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick).testTag("explore_book_${book.id}"),
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
    ) {
        BookNestBookCover(
            imageUrl = book.coverUrl,
            title = book.title,
            contentDescription = stringResource(Res.string.explore_cover_description, book.title),
            modifier = Modifier.fillMaxWidth().aspectRatio(0.68f),
        )
        Text(
            book.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (showDetails) {
            Text(
                book.author,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (book.isDemo) Text(stringResource(Res.string.explore_demo), style = MaterialTheme.typography.labelSmall)
            Text(
                book.priceText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun ExploreEmptyContent(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        modifier = modifier.fillMaxWidth().padding(vertical = BookNestSpacing.section).testTag("explore_empty"),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
