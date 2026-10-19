package com.nps.booknest

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.settings.ThemeMode
import com.nps.booknest.navigation.BookNestNavigation
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(viewModel: AppViewModel = koinViewModel()) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader
            .Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
                add(SvgDecoder.Factory())
            }.build()
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isSystemDarkTheme = isSystemInDarkTheme()
    val isDarkTheme =
        when (state.themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemDarkTheme
        }

    BookNestTheme(isDarkTheme = isDarkTheme) {
        BookNestNavigation()
    }
}
