package com.nps.booknest.feature.library.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface LibraryNavKey : NavKey

@Serializable
data object LibraryOverviewKey : LibraryNavKey

@Serializable
data object ReadingNowKey : LibraryNavKey

@Serializable
data object CollectionsKey : LibraryNavKey

@Serializable
data object AllBooksKey : LibraryNavKey

@Serializable
data object MyBooksKey : LibraryNavKey

@Serializable
data object LibraryListKey : LibraryNavKey

@Serializable
data class BookDetailKey(
    val bookId: String,
) : LibraryNavKey

val libraryNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(LibraryOverviewKey::class, LibraryOverviewKey.serializer())
            subclass(ReadingNowKey::class, ReadingNowKey.serializer())
            subclass(CollectionsKey::class, CollectionsKey.serializer())
            subclass(MyBooksKey::class, MyBooksKey.serializer())
            subclass(AllBooksKey::class, AllBooksKey.serializer())
            subclass(LibraryListKey::class, LibraryListKey.serializer())
            subclass(BookDetailKey::class, BookDetailKey.serializer())
        }
    }
