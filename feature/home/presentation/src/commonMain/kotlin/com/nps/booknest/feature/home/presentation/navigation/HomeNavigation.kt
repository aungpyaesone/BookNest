package com.nps.booknest.feature.home.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface HomeNavKey : NavKey

@Serializable
data object HomeKey : HomeNavKey

val homeNavigationSerializers =
    SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeKey::class, HomeKey.serializer())
        }
    }
