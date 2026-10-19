package com.nps.booknest.feature.profile.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilePresentationModule =
    module {
        viewModelOf(::ProfileViewModel)
        viewModelOf(::EditProfileViewModel)
    }
