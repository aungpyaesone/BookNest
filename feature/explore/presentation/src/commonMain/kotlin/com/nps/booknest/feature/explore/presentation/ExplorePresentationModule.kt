package com.nps.booknest.feature.explore.presentation

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val explorePresentationModule =
    module {
        viewModelOf(::ExploreViewModel)
        viewModel { parameters -> CategoryBooksViewModel(parameters.get(), get()) }
    }
