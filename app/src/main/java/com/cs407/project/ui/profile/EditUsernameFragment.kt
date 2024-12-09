package com.cs407.project.ui.profile

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
import androidx.navigation.fragment.findNavController
import com.cs407.project.R
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [EditUsernameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditUsernameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var newUsername: EditText
    private lateinit var saveButton: Button
    private lateinit var userDB: UsersDatabase




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_username, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        newUsername = view.findViewById(R.id.username)
        saveButton = view.findViewById(R.id.userLoginButton)
        val backButton = view.findViewById<ImageButton>(R.id.backArrowButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Always call the parent method

        val sharedPrefs = SharedPreferences(requireContext())
        saveButton.setOnClickListener {
            // Fetch the value dynamically when the button is clicked
            val newName = newUsername.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(
                    requireContext(), "Please enter a valid username", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val userExist = userDB.userDao().userExistsByUsername(newName)
                if (userExist) {
                    Toast.makeText(
                        requireContext(), "User Already Exists!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val username = sharedPrefs.getLogin().username.toString()
                    val password = sharedPrefs.getLogin().password.toString()
                    userDB.userDao().updateUsername(username, newName)
                    sharedPrefs.saveLogin(newName, password)
                    Toast.makeText(
                        requireContext(), "Username Updated!", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }


}