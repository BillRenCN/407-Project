package com.cs407.project.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferences(context: Context) {

    data class UserPass(val username: String?, val password: String?)

    private val preferences: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    fun saveLogin(username: String?, password: String?) {
        preferences.edit {
            putString("username", username)
            putString("password", password)
        }
    }

    fun getLogin(): UserPass {
        val username = preferences.getString("username", null)
        val password = preferences.getString("password", null)
        return UserPass(username, password)
    }
}

