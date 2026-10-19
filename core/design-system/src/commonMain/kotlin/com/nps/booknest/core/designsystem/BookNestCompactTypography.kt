package com.nps.booknest.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Compact storefront text uses platform sans-serif with native script fallback.
val bookNestCompactTypography =
    Typography(
        headlineSmall = libraryTextStyle(20, 26, FontWeight.Bold),
        titleLarge = libraryTextStyle(14, 20, FontWeight.Bold),
        titleMedium = libraryTextStyle(14, 20, FontWeight.SemiBold),
        titleSmall = libraryTextStyle(14, 20, FontWeight.SemiBold),
        bodyMedium = libraryTextStyle(12, 17),
        bodySmall = libraryTextStyle(11, 16),
        labelLarge = libraryTextStyle(12, 16, FontWeight.Medium),
        labelMedium = libraryTextStyle(12, 16, FontWeight.SemiBold),
        labelSmall = libraryTextStyle(10, 14, FontWeight.Medium),
    )

private fun libraryTextStyle(
    size: Int,
    height: Int,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = size.sp,
    lineHeight = height.sp,
    fontWeight = weight,
)
