package com.cs407.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        view.findViewById<Button>(R.id.usernamebutton).setOnClickListener {
            (requireActivity() as MainActivity).navigateEditUsername()
        }

        view.findViewById<Button>(R.id.passwordbutton).setOnClickListener {
            (requireActivity() as MainActivity).navigateEditPassword()
        }

        view.findViewById<ImageButton>(R.id.backArrowButton).setOnClickListener{
            findNavController().popBackStack()
        }
        return view
    }




}