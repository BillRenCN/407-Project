package com.cs407.project.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.project.LauncherActivity
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment(private val injectedProfileViewModel: ProfileViewModel? = null) : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userDB: UsersDatabase

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        profileViewModel = injectedProfileViewModel ?:
                ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        val root: View = binding.root
        val sharedPrefs = SharedPreferences(requireContext())
        val username=sharedPrefs.getLogin().username.toString()

        val logoutButton = binding.buttonLogout
        logoutButton.setOnClickListener {
            SharedPreferences(requireContext()).saveLogin(null, null)
            val intent = Intent(requireContext(), LauncherActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        lifecycleScope.launch {
            val date=userDB.userDao().getDateByUsername(username)
            Log.d("date", date.toString())
            binding.username.text = username
            binding.description.text="Member since "+convertUnixDateToString(date)
        }


        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun convertUnixDateToString(unixDate: Long): String {
        // Create a Date object from the Unix timestamp
        val date = Date(unixDate * 1000)  // Unix timestamps are in seconds, so multiply by 1000 for milliseconds

        // Define the desired format: MM/dd/yyyy
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        // Return the formatted date string
        return dateFormat.format(date)
    }
}
