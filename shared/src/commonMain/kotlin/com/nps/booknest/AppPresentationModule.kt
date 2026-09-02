package com.nps.booknest

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appPresentationModule =
    module {
        viewModelOf(::AppViewModel)
    }
