package com.nps.booknest.feature.home.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule =
    module {
        viewModelOf(::HomeViewModel)
    }
