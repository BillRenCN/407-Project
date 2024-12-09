package com.cs407.project.ui.listing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.cs407.project.R
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

        this.adapter = ListingAdapter(viewModel.filteredListings, viewLifecycleOwner)

        _binding = FragmentListingBinding.inflate(layoutInflater)
        val root: View = binding.root

        binding.newListingButton.setOnClickListener {
            val intent = Intent(context, AddListingActivity::class.java)
            resultHandler.launch(intent)
        }

        binding.listingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listingRecycler.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.loadingText.text = getString(R.string.loading_items)
                binding.loadingText.visibility = View.VISIBLE
            }
        })

        viewModel.allListings.observe(viewLifecycleOwner, Observer { items ->
            binding.listingSearch.setQuery("", false)
            if (items.isEmpty()) {
                binding.loadingText.text = getString(R.string.no_items_found)
                binding.loadingText.visibility = View.VISIBLE
            } else {
                binding.loadingText.visibility = View.GONE
            }
        })

        viewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            viewModel.filterListings(query)
        })

        class SearchTextListener : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.setSearchQuery(p0 ?: "")
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.setSearchQuery(p0 ?: "")
                return true
            }

        }

        binding.listingSearch.setOnQueryTextListener(SearchTextListener())

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
