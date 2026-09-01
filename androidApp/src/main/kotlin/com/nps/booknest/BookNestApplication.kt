package com.nps.booknest

import android.app.Application

class BookNestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(this)
    }
}
