package com.nps.booknest.feature.library.data

import com.nps.booknest.core.database.book.BookEntity
import com.nps.booknest.feature.library.domain.Book

fun BookEntity.toBook(): Book =
    Book(
        id = bookId,
        title = title,
        author = author,
    )

fun Book.toEntity(): BookEntity =
    BookEntity(
        bookId = id,
        title = title,
        author = author,
    )
