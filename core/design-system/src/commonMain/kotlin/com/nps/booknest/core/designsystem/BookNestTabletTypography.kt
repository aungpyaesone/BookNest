package com.nps.booknest.core.designsystem

import androidx.compose.ui.unit.sp

// Opt-in Library sizing; compact storefront defaults remain unchanged.
val bookNestTabletTypography =
    bookNestCompactTypography.copy(
        headlineSmall = bookNestCompactTypography.headlineSmall.copy(fontSize = 24.sp, lineHeight = 32.sp),
        titleLarge = bookNestCompactTypography.titleLarge.copy(fontSize = 16.sp, lineHeight = 22.sp),
        titleMedium = bookNestCompactTypography.titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = bookNestCompactTypography.titleSmall.copy(fontSize = 16.sp, lineHeight = 22.sp),
        bodyMedium = bookNestCompactTypography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = bookNestCompactTypography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = bookNestCompactTypography.labelLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
        labelMedium = bookNestCompactTypography.labelMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
        labelSmall = bookNestCompactTypography.labelSmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
    )
