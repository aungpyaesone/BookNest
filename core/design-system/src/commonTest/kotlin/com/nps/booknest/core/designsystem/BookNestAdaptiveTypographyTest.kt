package com.nps.booknest.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class BookNestAdaptiveTypographyTest {
    @Test
    fun compactPreservesAllOriginalStyles() {
        val original = Typography()
        assertSame(original, bookNestScaledTypography(original, false))
    }

    @Test
    fun tabletEnlargesEveryRoleAndLineHeightWithoutChangingOtherAttributes() {
        val original = Typography()
        val tablet = bookNestScaledTypography(original, true)
        styles(original).zip(styles(tablet)).forEach { (before, after) ->
            assertEquals(before.fontSize.value * 1.2f, after.fontSize.value, 0.001f)
            assertEquals(before.lineHeight.value * 1.2f, after.lineHeight.value, 0.001f)
            assertEquals(before.fontWeight, after.fontWeight)
            assertEquals(before.fontFamily, after.fontFamily)
            assertEquals(before.letterSpacing, after.letterSpacing)
        }
        assertEquals(Typography(), original)
    }

    private fun styles(typography: Typography): List<TextStyle> =
        with(typography) {
            listOf(
                displayLarge,
                displayMedium,
                displaySmall,
                headlineLarge,
                headlineMedium,
                headlineSmall,
                titleLarge,
                titleMedium,
                titleSmall,
                bodyLarge,
                bodyMedium,
                bodySmall,
                labelLarge,
                labelMedium,
                labelSmall,
            )
        }
}
