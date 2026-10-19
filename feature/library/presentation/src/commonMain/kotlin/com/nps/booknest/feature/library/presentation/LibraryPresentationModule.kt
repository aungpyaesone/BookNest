package com.nps.booknest.feature.library.presentation

import com.nps.booknest.feature.library.presentation.browse.AllBooksViewModel
import com.nps.booknest.feature.library.presentation.browse.CollectionsViewModel
import com.nps.booknest.feature.library.presentation.browse.MyBooksViewModel
import com.nps.booknest.feature.library.presentation.browse.ReadingNowViewModel
import com.nps.booknest.feature.library.presentation.detail.BookDetailViewModel
import com.nps.booknest.feature.library.presentation.list.LibraryListViewModel
import com.nps.booknest.feature.library.presentation.overview.LibraryOverviewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryPresentationModule =
    module {
        viewModelOf(::LibraryListViewModel)
        viewModelOf(::LibraryOverviewViewModel)
        viewModelOf(::ReadingNowViewModel)
        viewModelOf(::MyBooksViewModel)
        viewModelOf(::AllBooksViewModel)
        viewModelOf(::CollectionsViewModel)
        viewModel { parameters ->
            BookDetailViewModel(
                bookId = parameters.get(),
                bookLocalDataSource = get(),
            )
        }
    }
