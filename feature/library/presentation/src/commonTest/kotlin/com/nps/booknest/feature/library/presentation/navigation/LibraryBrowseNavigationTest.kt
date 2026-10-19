package com.nps.booknest.feature.library.presentation.navigation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryBrowseNavigationTest {
    @Test
    fun `all see all keys preserve their identity across serialization`() {
        listOf(ReadingNowKey, MyBooksKey, CollectionsKey).forEach { key ->
            assertEquals(key, Json.decodeFromString<LibraryNavKey>(Json.encodeToString<LibraryNavKey>(key)))
        }
    }
}
