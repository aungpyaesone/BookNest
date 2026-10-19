package com.nps.booknest.feature.explore.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data class CategoryBooksKey(
    val categoryId: String,
) : NavKey

val exploreNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(CategoryBooksKey::class, CategoryBooksKey.serializer())
        }
    }
