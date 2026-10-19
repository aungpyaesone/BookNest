package com.nps.booknest.feature.library.presentation

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.nps.booknest.core.designsystem.bookNestCompactTypography
import com.nps.booknest.core.designsystem.bookNestTabletTypography

@Composable
internal fun libraryIsTablet(): Boolean =
    LocalWindowInfo.current.containerSize.width / LocalDensity.current.density >= 600f

@Composable
internal fun libraryTypography(): Typography =
    if (libraryIsTablet()) bookNestTabletTypography else bookNestCompactTypography
