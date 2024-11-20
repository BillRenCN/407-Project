package com.cs407.project.ui.profile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.project.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val profileName: TextView = binding.name
        val profileDescription: TextView = binding.description
        val rating = binding.rating

        profileName.setOnFocusChangeListener { _, _ ->
            if (profileViewModel.userState.value.userName != profileName.text.toString()) {
                profileViewModel.setName(profileName.text.toString())
            }
        }

        profileDescription.setOnFocusChangeListener {_, _ ->
            if (profileViewModel.userState.value.userDescription != profileDescription.text.toString()) {
                profileViewModel.setDescription(profileDescription.text.toString())
            }
        }

        root.setOnClickListener {
            val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            root.clearFocus()
        }

        lifecycleScope.launch {
            val userState = profileViewModel.userState
            userState.collect {
                profileName.text = userState.value.userName
                profileDescription.text = userState.value.userDescription
                rating.rating = userState.value.rating
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