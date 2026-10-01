package com.omniroute.app

import android.app.Application

class OmniRouteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: OmniRouteApp
            private set
    }
}
