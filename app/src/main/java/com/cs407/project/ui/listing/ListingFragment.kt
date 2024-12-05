package com.cs407.project.ui.listing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cs407.project.R
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.data.AppDatabase
import com.cs407.project.databinding.FragmentListingBinding

class ListingFragment : Fragment() {

    private var _binding: FragmentListingBinding? = null
    private lateinit var appDB: AppDatabase

    private val binding get() = _binding!!
    private lateinit var viewModel: ListingViewModel
    private lateinit var adapter: ListingAdapter

    @SuppressLint("NotifyDataSetChanged")
    val resultHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.refreshListings()
            adapter.notifyDataSetChanged()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        appDB = AppDatabase.getDatabase(requireContext())

        val viewModel: ListingViewModel by activityViewModels()
        this.viewModel = viewModel

        this.adapter = ListingAdapter(viewModel.allListings, viewLifecycleOwner)

        _binding = FragmentListingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.floatingActionButton.setOnClickListener {
            Toast.makeText(context, "Floating action button clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, AddListingActivity::class.java)

            resultHandler.launch(intent)
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.textView3.text = getString(R.string.loading_items)
                binding.textView3.visibility = View.VISIBLE
            }
        })

        viewModel.allListings.observe(viewLifecycleOwner, Observer { items ->
            if (items.isEmpty()) {
                binding.textView3.text = getString(R.string.no_items_found)
                binding.textView3.visibility = View.VISIBLE
            } else {
                binding.textView3.visibility = View.GONE
            }
        })

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}