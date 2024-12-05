package com.cs407.project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.User
import com.cs407.project.data.UsersDatabase
import com.cs407.project.lib.hash
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var userDB: UsersDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val backButton = view.findViewById<ImageButton>(R.id.backArrowButton)
        userDB = UsersDatabase.getDatabase(requireContext())
        usernameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText)
        loginButton = view.findViewById(R.id.userRegisterButton)
        backButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.startfragment, StartFragment::class.java, null
            )
                ?.setReorderingAllowed(true)?.addToBackStack("loading start fragment")?.commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val email = emailEditText.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                Toast.makeText(
                    requireContext(), "One or more fields cannot be empty!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val userDao = userDB.userDao()
                val userExist = userDao.userExistsByUsername(username)
                val emailExist = userDao.userExistsByEmail(email)
                if (userExist || emailExist) {
                    Toast.makeText(
                        requireContext(),
                        "Username or Email already exists! Try login instead",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                if (password != confirmPassword) {
                    Toast.makeText(requireContext(), "Passwords don't match!", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                userDao.insertUser(User(0, username, hash(password), email, 0, 0.0, System.currentTimeMillis()/1000, "No description"))
                val sharedPrefs = SharedPreferences(requireContext())
                sharedPrefs.saveLogin(username, password)
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)

                activity?.finish()
            }
        }
    }
}