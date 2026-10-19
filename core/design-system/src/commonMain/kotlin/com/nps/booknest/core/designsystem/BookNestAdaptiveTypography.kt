package com.nps.booknest.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle

/** Uses the window before content padding or form width limits are applied. */
@Composable
fun bookNestIsTablet(): Boolean = LocalWindowInfo.current.containerSize.width / LocalDensity.current.density >= 600f

@Composable
fun bookNestAdaptiveCompactTypography(): Typography =
    if (bookNestIsTablet()) bookNestTabletTypography else bookNestCompactTypography

/** Opt in at a screen boundary so Reader and already-sized Library screens stay unchanged. */
@Composable
fun BookNestAdaptiveText(content: @Composable () -> Unit) {
    MaterialTheme(typography = bookNestAdaptiveTypography(), content = content)
}

@Composable
fun bookNestAdaptiveTypography(): Typography {
    val original = MaterialTheme.typography
    val isTablet = bookNestIsTablet()
    return remember(original, isTablet) { bookNestScaledTypography(original, isTablet) }
}

internal fun bookNestScaledTypography(
    original: Typography,
    isTablet: Boolean,
): Typography =
    if (!isTablet) {
        original
    } else {
        original.copy(
            displayLarge = original.displayLarge.tabletSize(),
            displayMedium = original.displayMedium.tabletSize(),
            displaySmall = original.displaySmall.tabletSize(),
            headlineLarge = original.headlineLarge.tabletSize(),
            headlineMedium = original.headlineMedium.tabletSize(),
            headlineSmall = original.headlineSmall.tabletSize(),
            titleLarge = original.titleLarge.tabletSize(),
            titleMedium = original.titleMedium.tabletSize(),
            titleSmall = original.titleSmall.tabletSize(),
            bodyLarge = original.bodyLarge.tabletSize(),
            bodyMedium = original.bodyMedium.tabletSize(),
            bodySmall = original.bodySmall.tabletSize(),
            labelLarge = original.labelLarge.tabletSize(),
            labelMedium = original.labelMedium.tabletSize(),
            labelSmall = original.labelSmall.tabletSize(),
        )
    }

private fun TextStyle.tabletSize(): TextStyle = copy(fontSize = fontSize * 1.2f, lineHeight = lineHeight * 1.2f)
