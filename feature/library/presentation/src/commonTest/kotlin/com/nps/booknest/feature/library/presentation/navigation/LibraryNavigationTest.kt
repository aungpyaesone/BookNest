package com.nps.booknest.feature.library.presentation.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class LibraryNavigationTest {
    @Test
    fun `book detail key preserves identifier after serialization round trip`() {
        val key: LibraryNavKey = BookDetailKey(bookId = "book-42")

        val encoded = Json.encodeToString(key)
        val decoded = Json.decodeFromString<LibraryNavKey>(encoded)

        assertThat(decoded).isEqualTo(key)
    }

    @Test
    fun `removing detail key returns back stack to library list`() {
        val backStack = mutableListOf<LibraryNavKey>(LibraryListKey, BookDetailKey("book-42"))

        backStack.removeLastOrNull()

        assertThat(backStack).isEqualTo(listOf(LibraryListKey))
    }
}
