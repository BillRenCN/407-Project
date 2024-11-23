package com.cs407.project.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveLoginDetails(email: String, password: String) {
        sharedPreferences.edit()
            .putString("EMAIL", email)
            .putString("PASSWORD", password)
            .apply()
    }

    fun isValidLogin(email: String, password: String): Boolean {
        val savedEmail = sharedPreferences.getString("EMAIL", null)
        val savedPassword = sharedPreferences.getString("PASSWORD", null)
        return email == savedEmail && password == savedPassword
    }
}

