package com.nps.booknest.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nps.booknest.feature.home.presentation.navigation.HomeKey
import com.nps.booknest.feature.library.presentation.browse.AllBooksRoot
import com.nps.booknest.feature.library.presentation.browse.CollectionsRoot
import com.nps.booknest.feature.library.presentation.browse.MyBooksRoot
import com.nps.booknest.feature.library.presentation.browse.ReadingNowRoot
import com.nps.booknest.feature.library.presentation.navigation.AllBooksKey
import com.nps.booknest.feature.library.presentation.navigation.CollectionsKey
import com.nps.booknest.feature.library.presentation.navigation.LibraryOverviewKey
import com.nps.booknest.feature.library.presentation.navigation.MyBooksKey
import com.nps.booknest.feature.library.presentation.navigation.ReadingNowKey
import com.nps.booknest.feature.library.presentation.overview.LibraryOverviewRoot

internal val libraryBrowseSceneMetadata =
    mapOf(
        BOOK_NEST_DESTINATION_KEY to LibraryOverviewKey,
        BOOK_NEST_HIDE_NAVIGATION to true,
    )

internal fun EntryProviderScope<NavKey>.libraryBrowseEntries(backStack: NavBackStack<NavKey>) {
    entry<LibraryOverviewKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to LibraryOverviewKey)) {
        LibraryOverviewRoot(
            onNavigateToReadingNow = { if (backStack.lastOrNull() == LibraryOverviewKey) backStack.add(ReadingNowKey) },
            onNavigateToCollections = {
                if (backStack.lastOrNull() ==
                    LibraryOverviewKey
                ) {
                    backStack.add(CollectionsKey)
                }
            },
            onNavigateToMyBooks = { if (backStack.lastOrNull() == LibraryOverviewKey) backStack.add(MyBooksKey) },
        )
    }
    entry<ReadingNowKey>(metadata = libraryBrowseSceneMetadata) {
        ReadingNowRoot(onNavigateBack = { if (backStack.lastOrNull() == ReadingNowKey) backStack.removeLastOrNull() })
    }
    entry<CollectionsKey>(metadata = libraryBrowseSceneMetadata) {
        CollectionsRoot(onNavigateBack = { if (backStack.lastOrNull() == CollectionsKey) backStack.removeLastOrNull() })
    }
    entry<AllBooksKey>(metadata = mapOf(BOOK_NEST_DESTINATION_KEY to HomeKey, BOOK_NEST_HIDE_NAVIGATION to true)) {
        AllBooksRoot(onNavigateBack = { if (backStack.lastOrNull() == AllBooksKey) backStack.removeLastOrNull() })
    }
    entry<MyBooksKey>(metadata = libraryBrowseSceneMetadata) {
        MyBooksRoot(onNavigateBack = { if (backStack.lastOrNull() == MyBooksKey) backStack.removeLastOrNull() })
    }
}
