package com.cs407.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentContainerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_start, container, false)
        val parentView=inflater.inflate(R.layout.activity_login_register, container, false)
        val fragmentWindow=parentView.findViewById<FragmentContainerView>(R.id.startfragment)
        view.findViewById<Button>(R.id.loginbutton).setOnClickListener{
           //todo: change the height to 300dp
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.startfragment, LoginFragment::class.java, null)  // Pass the fragment instance here
                ?.setReorderingAllowed(true)
                ?.addToBackStack("loading login fragment")
                ?.commit()
        }

        val registerButton=view.findViewById<Button>(R.id.registerbutton)
        registerButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.startfragment, RegisterFragment::class.java, null)  // Pass the fragment instance here
                ?.setReorderingAllowed(true)
                ?.addToBackStack("loading register fragment")
                ?.commit()
        }
        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}