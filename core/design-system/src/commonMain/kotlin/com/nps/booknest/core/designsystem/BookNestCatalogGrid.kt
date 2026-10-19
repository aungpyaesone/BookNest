package com.nps.booknest.core.designsystem

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/** Counts use the visible viewport for the breakpoint and the padded, capped content for card widths. */
fun bookNestCatalogColumns(
    viewportWidth: Dp,
    contentWidth: Dp,
    fontScale: Float,
    minimumCardWidth: Dp,
): Int =
    if (viewportWidth < 600.dp) {
        if (fontScale >= 1.3f) 3 else 4
    } else {
        val gap = 8f
        val minimumWidth = minimumCardWidth.value * fontScale.coerceAtLeast(1f)
        floor((contentWidth.value + gap) / (minimumWidth + gap)).toInt().coerceAtLeast(1)
    }
