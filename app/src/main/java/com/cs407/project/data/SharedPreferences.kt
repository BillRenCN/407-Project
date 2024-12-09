package com.cs407.project.data

import android.content.Context
import androidx.core.content.edit

class SharedPreferences() {

    data class UserPass(val username: String?, val password: String?)

    fun saveLogin(username: String?, password: String?, context: Context) {
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE).edit {
            putString("username", username)
            putString("password", password)
        }
    }

    fun getLogin(context: Context): UserPass {
        val preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val username = preferences.getString("username", null)
        val password = preferences.getString("password", null)
        return UserPass(username, password)
    }
}

