package com.nps.booknest

import android.app.Application
import com.nps.booknest.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun initializeKoin(application: Application) {
    if (GlobalContext.getOrNull() != null) return

    startKoin {
        androidContext(application)
        modules(appModules)
    }
}
