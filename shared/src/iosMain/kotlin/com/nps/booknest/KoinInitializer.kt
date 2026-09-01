package com.nps.booknest

import com.nps.booknest.di.appModules
import org.koin.core.context.startKoin

fun initializeKoin() {
    startKoin {
        modules(appModules)
    }
}
