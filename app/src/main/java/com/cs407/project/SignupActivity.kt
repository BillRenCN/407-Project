package com.cs407.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.data.PreferencesManager

class SignupActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        preferencesManager = PreferencesManager(this)

        val emailInput: EditText = findViewById(R.id.signup_email_input)
        val passwordInput: EditText = findViewById(R.id.signup_password_input)
        val confirmPasswordInput: EditText = findViewById(R.id.signup_confirm_password_input)
        val signupButton: Button = findViewById(R.id.btn_signup)
        val backToLogin: TextView = findViewById(R.id.back_to_login)

        // Handle Sign-Up Logic
        signupButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                preferencesManager.saveLoginDetails(email, password)
                Toast.makeText(this, "Sign-Up Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please check your input fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate back to Login
        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
