package com.nps.booknest.feature.reader.presentation.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ReaderNavigationTest {
    @Test
    fun `reader key preserves book identifier after serialization round trip`() {
        val key: ReaderNavKey = ReaderKey(bookId = "mountain-morning")

        val encoded = Json.encodeToString(key)
        val decoded = Json.decodeFromString<ReaderNavKey>(encoded)

        assertThat(decoded).isEqualTo(key)
    }
}
