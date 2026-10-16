package com.nps.booknest.feature.reader.presentation

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val readerPresentationModule =
    module {
        viewModel { parameters ->
            ReaderViewModel(
                bookId = parameters.get(),
                bookContentDataSource = get(),
                readingProgressLocalDataSource = get(),
                readerPreferencesLocalDataSource = get(),
            )
        }
    }
