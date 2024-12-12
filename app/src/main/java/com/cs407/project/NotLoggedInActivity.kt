package com.cs407.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotLoggedInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.startfragment, LoginRegisterFragment())
                .commit()
        }
        supportActionBar?.hide()
    }
}