package com.nps.booknest.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nps.booknest.core.designsystem.generated.resources.Res
import com.nps.booknest.core.designsystem.generated.resources.noto_sans_myanmar
import org.jetbrains.compose.resources.Font

private val lightColorScheme =
    lightColorScheme(
        primary = Color(0xFF70452F),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF1DFD1),
        onPrimaryContainer = Color(0xFF2E1A10),
        secondary = Color(0xFF8B674F),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF6E9DE),
        onSecondaryContainer = Color(0xFF2F2118),
        tertiary = Color(0xFF9B6B2F),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFFCF8F4),
        onBackground = Color(0xFF2B211B),
        surface = Color(0xFFFFFCF9),
        onSurface = Color(0xFF2B211B),
        surfaceVariant = Color(0xFFF4E9DF),
        onSurfaceVariant = Color(0xFF67584D),
        outline = Color(0xFFB9A99C),
        outlineVariant = Color(0xFFE1D4C9),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
    )

private val darkColorScheme =
    darkColorScheme(
        primary = Color(0xFFE6BFA7),
        onPrimary = Color(0xFF432817),
        primaryContainer = Color(0xFF5A3421),
        onPrimaryContainer = Color(0xFFFFDBCA),
        secondary = Color(0xFFD8C0AF),
        onSecondary = Color(0xFF3C2B20),
        secondaryContainer = Color(0xFF544035),
        onSecondaryContainer = Color(0xFFF5DED0),
        tertiary = Color(0xFFE9C17A),
        onTertiary = Color(0xFF402D00),
        background = Color(0xFF1B1613),
        onBackground = Color(0xFFF0E3DB),
        surface = Color(0xFF211B17),
        onSurface = Color(0xFFF0E3DB),
        surfaceVariant = Color(0xFF4A4039),
        onSurfaceVariant = Color(0xFFD4C3B8),
        outline = Color(0xFF9E8E83),
        outlineVariant = Color(0xFF4A4039),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
    )

private val bookNestShapes =
    Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(18.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp),
    )

object BookNestSpacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 20.dp
    val section = 24.dp
    val screenCompact = 16.dp
    val screenExpanded = 32.dp
}

object BookNestSize {
    val minimumTouchTarget = 48.dp
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 28.dp
    val logoIcon = 32.dp
    val contentMaxWidth = 1200.dp
}

@Composable
private fun bookNestTypography(): Typography {
    val fontFamily =
        FontFamily(
            Font(Res.font.noto_sans_myanmar, FontWeight.Normal),
            Font(Res.font.noto_sans_myanmar, FontWeight.Medium),
            Font(Res.font.noto_sans_myanmar, FontWeight.SemiBold),
            Font(Res.font.noto_sans_myanmar, FontWeight.Bold),
        )
    return Typography(
        displaySmall = fontFamily.textStyle(FontWeight.Bold, 30, 42),
        headlineLarge = fontFamily.textStyle(FontWeight.Bold, 27, 39),
        headlineMedium = fontFamily.textStyle(FontWeight.Bold, 23, 34),
        headlineSmall = fontFamily.textStyle(FontWeight.SemiBold, 19, 30),
        titleLarge = fontFamily.textStyle(FontWeight.SemiBold, 18, 28),
        titleMedium = fontFamily.textStyle(FontWeight.SemiBold, 15, 24),
        titleSmall = fontFamily.textStyle(FontWeight.Medium, 13, 21),
        bodyLarge = fontFamily.textStyle(FontWeight.Normal, 15, 25),
        bodyMedium = fontFamily.textStyle(FontWeight.Normal, 13, 22),
        bodySmall = fontFamily.textStyle(FontWeight.Normal, 12, 20),
        labelLarge = fontFamily.textStyle(FontWeight.SemiBold, 13, 21),
        labelMedium = fontFamily.textStyle(FontWeight.Medium, 12, 20),
        labelSmall = fontFamily.textStyle(FontWeight.Medium, 11, 18),
    )
}

private fun FontFamily.textStyle(
    fontWeight: FontWeight,
    fontSize: Int,
    lineHeight: Int,
) = TextStyle(
    fontFamily = this,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
)

@Composable
fun BookNestTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme,
        typography = bookNestTypography(),
        shapes = bookNestShapes,
        content = content,
    )
}
