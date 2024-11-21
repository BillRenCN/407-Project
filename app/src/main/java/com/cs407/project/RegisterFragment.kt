package com.cs407.project


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import java.security.MessageDigest
import com.cs407.project.data.UsersDatabase
import com.cs407.project.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordEditText2: EditText
    private lateinit var emailEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var userDB: UsersDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_register, container, false)
        val backButton=view.findViewById<ImageButton>(R.id.backArrowButton)
        userDB = UsersDatabase.getDatabase(requireContext())
        usernameEditText=view.findViewById<EditText>(R.id.nameEditText)
        emailEditText=view.findViewById<EditText>(R.id.emailEditText)
        passwordEditText=view.findViewById<EditText>(R.id.passwordEditText)
        passwordEditText2=view.findViewById<EditText>(R.id.confirmPasswordEditText)
        loginButton=view.findViewById<Button>(R.id.userRegisterButton)
        backButton.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.startfragment, StartFragment::class.java, null)  // Pass the fragment instance here
                ?.setReorderingAllowed(true)
                ?.addToBackStack("loading start fragment")
                ?.commit()
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        loginButton.setOnClickListener{
            val username = usernameEditText.text.toString()
            val password1 = passwordEditText.text.toString()
            val password2 = passwordEditText2.text.toString()
            val email=emailEditText.text.toString()

            if (username.isEmpty() || password1.isEmpty() || password2.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "One or more fields cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch{
                val userExist=userDB.userDao().userExistsByUsername(username)
                val emailExist=userDB.userDao().userExistsByEmail(email)
                if (userExist || emailExist){
                    Toast.makeText(requireContext(), "Username or Email already exists! Try login instead", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (password1!=password2){
                    Toast.makeText(requireContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                userDB.userDao().insertUser(User(0, username, hash(password1), email))
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private suspend fun getUserPasswd(){}

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}