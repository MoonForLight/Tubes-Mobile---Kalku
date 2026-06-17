package com.example.kalku.utils

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLogin(
        userId: Int,
        fullName: String,
        businessName: String,
        email: String
    ) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_BUSINESS_NAME, businessName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun updateProfile(fullName: String, businessName: String, email: String) {
        preferences.edit()
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_BUSINESS_NAME, businessName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getUserId(): Int = preferences.getInt(KEY_USER_ID, 0)
    fun getFullName(): String = preferences.getString(KEY_FULL_NAME, "Pengguna") ?: "Pengguna"
    fun getBusinessName(): String = preferences.getString(KEY_BUSINESS_NAME, "UMKM") ?: "UMKM"
    fun getEmail(): String = preferences.getString(KEY_EMAIL, "") ?: ""

    fun logout() {
        preferences.edit().clear().apply()
    }

    companion object {
        const val PREF_NAME = "kalku_session"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_ID = "user_id"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_BUSINESS_NAME = "business_name"
        const val KEY_EMAIL = "email"
    }
}
