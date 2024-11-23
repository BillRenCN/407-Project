package com.cs407.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.UsersDatabase
import kotlinx.coroutines.launch
import java.security.MessageDigest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var userDB: UsersDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_login, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        usernameEditText=view.findViewById(R.id.emailEditText)
        passwordEditText=view.findViewById(R.id.passwordEditText)
        loginButton=view.findViewById(R.id.userLoginButton)
        val backButton=view.findViewById<ImageButton>(R.id.backArrowButton)
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
            val password = passwordEditText.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "One or more fields cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch{
                //userExist and emailExist cannot both be true
                val userExist=userDB.userDao().userExistsByUsername(username)
                val emailExist=userDB.userDao().userExistsByEmail(username)
                if (!userExist && !emailExist){
                    Toast.makeText(requireContext(), "User does not exist!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (userExist){
                    val realPassword=userDB.userDao().getPasswordByUsername(username)
                    if (hash(password)==realPassword){
                        Toast.makeText(requireContext(), "Login Success!", Toast.LENGTH_SHORT).show()
                        //Todo: change wireframe to main home page
                    }
                    else{
                        Toast.makeText(requireContext(), "Password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                }

                if (emailExist){
                    val realPassword=userDB.userDao().getPasswordByEmail(username)
                    if (hash(password)==realPassword){
                        Toast.makeText(requireContext(), "Login Success!", Toast.LENGTH_SHORT).show()
                        //Todo: change wireframe to main home page
                    }
                    else{
                        Toast.makeText(requireContext(), "Password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}