package com.nps.booknest

import androidx.compose.runtime.Composable
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.navigation.BookNestNavigation

@Composable
fun App() {
    BookNestTheme {
        BookNestNavigation()
    }
}
