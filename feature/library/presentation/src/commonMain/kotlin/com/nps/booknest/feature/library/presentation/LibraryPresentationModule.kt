package com.nps.booknest.feature.library.presentation

import com.nps.booknest.feature.library.presentation.detail.BookDetailViewModel
import com.nps.booknest.feature.library.presentation.list.LibraryListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryPresentationModule =
    module {
        viewModelOf(::LibraryListViewModel)
        viewModel { parameters ->
            BookDetailViewModel(
                bookId = parameters.get(),
                bookLocalDataSource = get(),
            )
        }
    }
