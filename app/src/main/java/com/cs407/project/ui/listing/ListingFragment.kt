package com.cs407.project.ui.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.databinding.FragmentListingBinding

class ListingFragment : Fragment() {

    private var _binding: FragmentListingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val listingViewModel = ViewModelProvider(this)[ListingViewModel::class.java]

        _binding = FragmentListingBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textListing
//        listingViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val listingModelArrayList: ArrayList<ListingModel> = ArrayList()
        for (i in 0..100) {
            listingModelArrayList.add(ListingModel("Item $i", "Short description $i", 0))
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ListingAdapter(listingModelArrayList)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}