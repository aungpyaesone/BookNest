package com.nps.booknest.feature.library.presentation.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nps.booknest.core.designsystem.BookNestBookCover
import com.nps.booknest.feature.library.presentation.generated.resources.Res
import com.nps.booknest.feature.library.presentation.generated.resources.library_browse_cover
import com.nps.booknest.feature.library.presentation.libraryIsTablet
import com.nps.booknest.feature.library.presentation.overview.LibraryBookUi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MyBooksBookCard(
    book: LibraryBookUi,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("library_browse_book_${book.id}"),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val isTablet = libraryIsTablet()
            val coverWidth = if (isTablet) 80.dp else 64.dp
            val hasSideAction = maxWidth >= (if (isTablet) 384.dp else 320.dp) && LocalDensity.current.fontScale < 1.3f
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BookNestBookCover(
                    book.coverUrl,
                    book.title,
                    stringResource(Res.string.library_browse_cover, book.title),
                    Modifier.width(coverWidth).aspectRatio(0.68f).testTag("my_books_cover_${book.id}"),
                )
                Column(
                    Modifier.weight(1f).heightIn(min = coverWidth / 0.68f).testTag("my_books_body_${book.id}"),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        Modifier.fillMaxWidth().testTag("my_books_header_${book.id}"),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        MyBooksBookMetadata(book, Modifier.weight(1f))
                        if (hasSideAction) MyBooksCardAction(book, onClick)
                        MyBooksCardOverflow(book, onClick)
                    }
                    if (!hasSideAction) {
                        MyBooksCardAction(
                            book,
                            onClick,
                            Modifier.align(Alignment.End).padding(top = 4.dp, end = 48.dp),
                        )
                    }
                    Box(Modifier.fillMaxWidth().padding(top = 4.dp)) { MyBooksCardProgress(book) }
                }
            }
        }
    }
}

@Composable
private fun MyBooksBookMetadata(
    book: LibraryBookUi,
    modifier: Modifier = Modifier,
) {
    Column(modifier.testTag("my_books_metadata_${book.id}"), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            book.title,
            Modifier.testTag("my_books_title_${book.id}"),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            book.author,
            Modifier.testTag("my_books_author_${book.id}"),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Surface(
            modifier = Modifier.testTag("my_books_category_${book.id}"),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shadowElevation = 1.dp,
        ) {
            Text(
                stringResource(book.category.label()),
                Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
