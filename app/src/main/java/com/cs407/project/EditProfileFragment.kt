package com.cs407.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        view.findViewById<Button>(R.id.usernamebutton).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.startfragment,
                LoginFragment::class.java,
                null
            )
                ?.setReorderingAllowed(true)?.addToBackStack("loading login fragment")?.commit()
        }

        view.findViewById<Button>(R.id.passwordbutton).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.startfragment,
                RegisterFragment::class.java,
                null
            )
                ?.setReorderingAllowed(true)?.addToBackStack("loading register fragment")?.commit()
        }
        return view
    }


}