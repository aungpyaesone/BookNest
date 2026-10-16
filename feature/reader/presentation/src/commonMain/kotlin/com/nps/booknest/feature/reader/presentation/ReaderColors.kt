package com.nps.booknest.feature.reader.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nps.booknest.core.settings.ReaderThemeMode
import com.nps.booknest.feature.reader.presentation.generated.resources.Res
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_dark
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_light
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_sepia

internal data class ReaderColors(
    val background: Color,
    val surface: Color,
    val content: Color,
    val contentMuted: Color,
    val accent: Color,
    val track: Color,
)

@Composable
internal fun ReaderThemeMode.readerColors(): ReaderColors =
    when (this) {
        ReaderThemeMode.LIGHT -> {
            ReaderColors(
                background = Color(0xFFFFFBF7),
                surface = Color(0xFFFFFFFF),
                content = Color(0xFF30251F),
                contentMuted = Color(0xFF71635B),
                accent = MaterialTheme.colorScheme.primary,
                track = Color(0xFFE9DDD4),
            )
        }

        ReaderThemeMode.SEPIA -> {
            ReaderColors(
                background = Color(0xFFF4E8D0),
                surface = Color(0xFFEBDAB9),
                content = Color(0xFF3F3025),
                contentMuted = Color(0xFF756050),
                accent = Color(0xFF805533),
                track = Color(0xFFD9C49D),
            )
        }

        ReaderThemeMode.DARK -> {
            ReaderColors(
                background = Color(0xFF171310),
                surface = Color(0xFF211B17),
                content = Color(0xFFF2E7DC),
                contentMuted = Color(0xFFC9B9AB),
                accent = Color(0xFFE0AD86),
                track = Color(0xFF4A3B32),
            )
        }
    }

internal fun ReaderThemeMode.labelResource() =
    when (this) {
        ReaderThemeMode.LIGHT -> Res.string.reader_theme_light
        ReaderThemeMode.SEPIA -> Res.string.reader_theme_sepia
        ReaderThemeMode.DARK -> Res.string.reader_theme_dark
    }
