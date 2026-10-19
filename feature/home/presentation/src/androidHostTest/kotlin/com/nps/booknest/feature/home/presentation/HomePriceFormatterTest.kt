package com.nps.booknest.feature.home.presentation

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomePriceFormatterTest {
    @Test
    fun `price formatting uses currency fraction digits without truncating decimals`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("1200", formatHomePrice(1200.0, "JPY").filter(Char::isDigit))
            assertEquals("120050", formatHomePrice(1200.5, "USD").filter(Char::isDigit))
            assertTrue(formatHomePrice(1200.5, "USD").contains('$'))
        } finally {
            Locale.setDefault(original)
        }
    }
}
