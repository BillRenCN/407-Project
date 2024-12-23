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
import com.cs407.project.data.UsersDatabase
import com.cs407.project.lib.hash
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var userDB: UsersDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        usernameEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.userLoginButton)
        val backButton = view.findViewById<ImageButton>(R.id.backArrowButton)
        backButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.startfragment, LoginRegisterFragment::class.java, null
            )?.setReorderingAllowed(true)?.addToBackStack("loading start fragment")?.commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(), "One or more fields cannot be empty!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                //userExist and emailExist cannot both be true
                val userDao = userDB.userDao()
                val realPasswordHash: String?

                if (userDao.userExistsByUsername(username)) {
                    realPasswordHash = userDao.getPasswordHashByUsername(username)
                } else if (userDao.userExistsByEmail(username)) {
                    realPasswordHash = userDao.getPasswordHashByEmail(username)
                } else {
                    Toast.makeText(requireContext(), "User does not exist!", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                if (hash(password) == realPasswordHash) {
                    val sharedPrefs = SharedPreferences(requireContext())
                    sharedPrefs.saveLogin(username, password)

                    Toast.makeText(requireContext(), "Login Success!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)

                    activity?.finish()
                } else {
                    Toast.makeText(requireContext(), "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}