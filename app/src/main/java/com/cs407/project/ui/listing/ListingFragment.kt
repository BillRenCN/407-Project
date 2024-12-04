package com.cs407.project.ui.listing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.PostItemActivity
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

        binding.floatingActionButton.setOnClickListener {
            Toast.makeText(context, "Floating action button clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, PostItemActivity::class.java)
            startActivity(intent)
        }

//        val textView: TextView = binding.textListing
//        listingViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val listingModelArrayList: ArrayList<ListingModel> = ArrayList()
        for (i in 0..100) {
            listingModelArrayList.add(ListingModel("Item $i", "Short description $i", 0))
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ListingAdapter(listingModelArrayList)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}