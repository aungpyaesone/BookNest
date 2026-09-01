package com.nps.booknest.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColorScheme =
    lightColorScheme(
        primary = Color(0xFF365E3B),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFB8F1B8),
        onPrimaryContainer = Color(0xFF002107),
        secondary = Color(0xFF52634F),
        background = Color(0xFFF7FBF2),
        surface = Color(0xFFF7FBF2),
    )

private val darkColorScheme =
    darkColorScheme(
        primary = Color(0xFF9CD49E),
        onPrimary = Color(0xFF053912),
        primaryContainer = Color(0xFF1D5126),
        onPrimaryContainer = Color(0xFFB8F1B8),
        secondary = Color(0xFFB9CCB5),
        background = Color(0xFF10140F),
        surface = Color(0xFF10140F),
    )

@Composable
fun BookNestTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme,
        content = content,
    )
}
