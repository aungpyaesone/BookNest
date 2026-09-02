package com.nps.booknest.feature.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestOutlineButton
import com.nps.booknest.core.designsystem.BookNestPrimaryButton
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.feature.home.presentation.generated.resources.Res
import com.nps.booknest.feature.home.presentation.generated.resources.home_book_cover_description
import com.nps.booknest.feature.home.presentation.generated.resources.home_buy
import com.nps.booknest.feature.home.presentation.generated.resources.home_no_books_body
import com.nps.booknest.feature.home.presentation.generated.resources.home_no_books_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_not_rentable
import com.nps.booknest.feature.home.presentation.generated.resources.home_popular_books_title
import com.nps.booknest.feature.home.presentation.generated.resources.home_rent
import com.nps.booknest.feature.home.presentation.generated.resources.home_see_all
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PopularBooks(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    columns: Int,
    layoutMode: HomeLayoutMode,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.large)) {
        SectionHeader(
            title = stringResource(Res.string.home_popular_books_title),
            action = stringResource(Res.string.home_see_all),
            onActionClick = { onAction(HomeAction.OnCategoryClick(HomeCategory.ALL)) },
            layoutMode = layoutMode,
        )
        when {
            state.visibleBooks.isEmpty() -> {
                EmptyBookResults()
            }

            columns == 1 -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.large)) {
                    items(state.visibleBooks, key = HomeBookUi::id) { book ->
                        HomeBookCard(
                            book = book,
                            onAction = onAction,
                            layoutMode = layoutMode,
                            modifier = Modifier.width(112.dp),
                        )
                    }
                }
            }

            else -> {
                BookGrid(
                    books = state.visibleBooks,
                    columns = columns,
                    onAction = onAction,
                    layoutMode = layoutMode,
                )
            }
        }
    }
}

@Composable
private fun BookGrid(
    books: List<HomeBookUi>,
    columns: Int,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraLarge)) {
        books.chunked(columns).forEach { rowBooks ->
            Row(horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.extraLarge)) {
                rowBooks.forEach { book ->
                    HomeBookCard(
                        book = book,
                        onAction = onAction,
                        layoutMode = layoutMode,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowBooks.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun HomeBookCard(
    book: HomeBookUi,
    onAction: (HomeAction) -> Unit,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val titleStyle =
        if (layoutMode == HomeLayoutMode.COMPACT) {
            MaterialTheme.typography.titleSmall
        } else {
            MaterialTheme.typography.titleMedium
        }
    Column(
        modifier = modifier.clickable { onAction(HomeAction.OnBookClick(book.id)) },
        verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
    ) {
        BookCover(
            book = book,
            layoutMode = layoutMode,
            modifier = Modifier.fillMaxWidth().aspectRatio(0.7f),
        )
        Text(
            text = book.title,
            modifier = Modifier.testTag(HomeTestTags.popularBookTitle(book.id)),
            style = titleStyle,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = book.author,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(text = book.priceText, style = titleStyle)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.extraSmall),
        ) {
            BookNestPrimaryButton(
                text = stringResource(Res.string.home_buy),
                onClick = { onAction(HomeAction.OnBuyClick(book.id)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(HomeTestTags.buyAction(book.id)),
            )
            BookNestOutlineButton(
                text =
                    if (book.isRentable) {
                        stringResource(Res.string.home_rent)
                    } else {
                        stringResource(Res.string.home_not_rentable)
                    },
                onClick = { onAction(HomeAction.OnRentClick(book.id)) },
                modifier = Modifier.fillMaxWidth().testTag(HomeTestTags.rentAction(book.id)),
            )
        }
    }
}

@Composable
private fun BookCover(
    book: HomeBookUi,
    layoutMode: HomeLayoutMode,
    modifier: Modifier = Modifier,
) {
    val coverDescription = stringResource(Res.string.home_book_cover_description, book.title)
    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .background(Brush.linearGradient(coverColors(book.coverStyle)))
                .semantics { contentDescription = coverDescription },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.09f), radius = size.minDimension * 0.45f)
            drawLine(
                color = Color.White.copy(alpha = 0.22f),
                start =
                    androidx.compose.ui.geometry
                        .Offset(0f, size.height * 0.76f),
                end =
                    androidx.compose.ui.geometry
                        .Offset(size.width, size.height * 0.54f),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(BookNestSpacing.medium),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = book.title,
                color = Color.White,
                style =
                    if (layoutMode == HomeLayoutMode.COMPACT) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                fontWeight = FontWeight.Bold,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = book.author,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

private fun coverColors(style: BookCoverStyle): List<Color> =
    when (style) {
        BookCoverStyle.SAND -> listOf(Color(0xFFC89B6D), Color(0xFF7B4D31))
        BookCoverStyle.FOREST -> listOf(Color(0xFF365C46), Color(0xFF142E23))
        BookCoverStyle.PLUM -> listOf(Color(0xFF633365), Color(0xFF241129))
        BookCoverStyle.SKY -> listOf(Color(0xFF72A7B5), Color(0xFF345C6C))
    }

@Composable
private fun EmptyBookResults() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(BookNestSpacing.extraLarge)
                .testTag(HomeTestTags.EMPTY_RESULTS),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(Res.string.home_no_books_title), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(Res.string.home_no_books_body),
            modifier = Modifier.padding(top = BookNestSpacing.small),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
