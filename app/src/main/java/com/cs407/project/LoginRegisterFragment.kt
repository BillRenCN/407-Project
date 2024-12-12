package com.cs407.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class LoginRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        view.findViewById<Button>(R.id.loginbutton).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                    R.id.startfragment,
                    LoginFragment::class.java,
                    null
                )
                ?.setReorderingAllowed(true)?.addToBackStack("loading login fragment")?.commit()
        }

        view.findViewById<Button>(R.id.registerbutton).setOnClickListener {
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