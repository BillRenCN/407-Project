package com.cs407.project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.lib.hash
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userDB: UsersDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        sharedPrefs = SharedPreferences(this)
        userDB = UsersDatabase.getDatabase(this)

        val loginInfo = sharedPrefs.getLogin()

        Toast.makeText(
            this@LauncherActivity,
            "Stored Credentials: Username = ${loginInfo.username}, Password = ${loginInfo.password}",
            Toast.LENGTH_LONG
        ).show()

        if (loginInfo.username.isNullOrBlank() || loginInfo.password.isNullOrBlank()) {
            startLoginActivity()
        } else {
            lifecycleScope.launch {
                val isValidUser = validateUser(loginInfo.username, loginInfo.password)
                if (isValidUser) {
                    val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                    val userId = userDB.userDao().getUserFromUsername(loginInfo.username)?.userId
                    intent.putExtra("MY_USER_ID", userId)
                    startActivity(intent)
                    finish()
                } else {
                    sharedPrefs.saveLogin(null, null)
                    startLoginActivity()
                }
            }

        }

    }


    private suspend fun validateUser(username: String, password: String): Boolean {
        val userDao = userDB.userDao()
        val userExist = userDao.userExistsByUsername(username)
        val emailExist = userDao.userExistsByEmail(username)

        if (!userExist && !emailExist) return false

        val storedPasswordHash = if (userExist) {
            userDao.getPasswordHashByUsername(username)
        } else {
            userDao.getPasswordHashByEmail(username)
        }

        return storedPasswordHash != null && hash(password) == storedPasswordHash
    }


    private fun startLoginActivity() {
        startActivity(Intent(this@LauncherActivity, NotLoggedInActivity::class.java))
        finish()
    }
}