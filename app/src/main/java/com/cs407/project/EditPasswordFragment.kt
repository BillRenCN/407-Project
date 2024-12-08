package com.cs407.project

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.lib.hash
import com.cs407.project.ui.profile.ProfileViewModel
import com.cs407.project.ui.profile.UserState
import kotlinx.coroutines.launch

class EditPasswordFragment : Fragment() {
    private lateinit var newPassword1: EditText
    private lateinit var newPassword2: EditText
    private lateinit var saveButton: Button
    private lateinit var userDB: UsersDatabase
    private lateinit var profileViewModel: ProfileViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_password, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        newPassword1 = view.findViewById(R.id.emailEditText)
        newPassword2 = view.findViewById(R.id.passwordEditText)
        saveButton = view.findViewById(R.id.userLoginButton)
        val backButton = view.findViewById<ImageButton>(R.id.backArrowButton)

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveButton.setOnClickListener {
            val password1 = newPassword1.text.toString().trim()
            val password2 = newPassword2.text.toString().trim()

            if (password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(
                    requireContext(), "Password fields cannot be empty!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password1 == password2) {
                val hashedPassword = hash(password1)
                val sharedPrefs = SharedPreferences(requireContext())
                val username = sharedPrefs.getLogin().username.toString()

                lifecycleScope.launch {
                    try {
                        userDB.userDao().updatePasswordByUsername(username, hashedPassword)
                        sharedPrefs.saveLogin(username, hashedPassword)

                        Toast.makeText(
                            requireContext(), "Password Updated!", Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } catch (e: Exception) {
                        Log.e("EditPasswordFragment", "Error updating password: ${e.message}")
                        Toast.makeText(
                            requireContext(), "An error occurred while updating the password.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


