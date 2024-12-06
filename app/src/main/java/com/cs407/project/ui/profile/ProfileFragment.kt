package com.cs407.project.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.project.LauncherActivity
import com.cs407.project.data.SharedPreferences
import com.cs407.project.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment(private val injectedProfileViewModel: ProfileViewModel? = null) : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        profileViewModel = injectedProfileViewModel ?:
                ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val logoutButton = binding.buttonLogout
        logoutButton.setOnClickListener {
            SharedPreferences(requireContext()).saveLogin(null, null)
            val intent = Intent(requireContext(), LauncherActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        lifecycleScope.launch {
            profileViewModel.userState.collect { state ->
                binding.username.text = state.userName
                binding.description.text = state.userDescription
            }
        }


        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
