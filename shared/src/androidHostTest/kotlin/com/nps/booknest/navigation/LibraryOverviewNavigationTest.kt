package com.nps.booknest.navigation

import com.nps.booknest.feature.library.presentation.navigation.BookDetailKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryListKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryOverviewKey
import kotlin.test.Test
import kotlin.test.assertEquals

class LibraryOverviewNavigationTest {
    @Test
    fun `library tab opens overview instead of the local book list`() {
        assertEquals(LibraryOverviewKey, AppDestination.LIBRARY.rootKey())
    }

    @Test
    fun `overview local list and adaptive detail retain library selection`() {
        assertEquals(AppDestination.LIBRARY, LibraryOverviewKey.toAppDestination())
        assertEquals(AppDestination.LIBRARY, LibraryListKey.toAppDestination())
        assertEquals(AppDestination.LIBRARY, BookDetailKey("mountain-morning").toAppDestination())
    }
}
