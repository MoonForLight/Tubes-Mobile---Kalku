package com.example.kalku.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class AppSettingsManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean = preferences.getBoolean(KEY_DARK_MODE, false)
    fun isReminderEnabled(): Boolean = preferences.getBoolean(KEY_REMINDER, true)

    fun setDarkMode(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        applyTheme(enabled)
    }

    fun setReminderEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_REMINDER, enabled).apply()
    }

    companion object {
        private const val PREF_NAME = "kalku_settings"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_REMINDER = "reminder_enabled"

        fun applySavedTheme(context: Context) {
            val enabled = AppSettingsManager(context).isDarkMode()
            applyTheme(enabled)
        }

        private fun applyTheme(enabled: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}
