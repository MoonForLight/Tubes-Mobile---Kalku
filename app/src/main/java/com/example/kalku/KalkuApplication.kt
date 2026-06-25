package com.example.kalku

import android.app.Application
import com.example.kalku.utils.AppSettingsManager

class KalkuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppSettingsManager.applySavedTheme(this)
    }
}
