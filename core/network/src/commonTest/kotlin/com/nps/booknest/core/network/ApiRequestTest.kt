package com.nps.booknest.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ApiRequestTest {
    @Test
    fun `relative SVG and optional URLs resolve without insecure images`() {
        assertEquals("$BOOK_NEST_API_URL/images/banner.svg", resolveApiImageUrl("/images/banner.svg"))
        assertEquals("https://cdn.example.com/cover.png", resolveApiImageUrl("https://cdn.example.com/cover.png"))
        assertNull(resolveApiImageUrl(null))
        assertNull(resolveApiImageUrl(" "))
        assertFailsWith<IllegalArgumentException> { resolveApiImageUrl("http://example.com/cover.png") }
    }
}
