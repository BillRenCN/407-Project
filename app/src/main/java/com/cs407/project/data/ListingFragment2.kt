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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.FragmentListingBinding
import kotlinx.coroutines.launch

class ListingFragment2 : Fragment() {

    private var _binding: FragmentListingBinding? = null
    private lateinit var appDB: AppDatabase
    private lateinit var userDB: UsersDatabase
    private val binding get() = _binding!!
    private lateinit var viewModel: ListingViewModel2
    private lateinit var adapter: ListingAdapter

    @SuppressLint("NotifyDataSetChanged")
    val resultHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // After a new listing is added, refresh the listings for the user.
            viewModel.refreshListings(getUserIdFromPrefs())
            adapter.notifyDataSetChanged()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        appDB = AppDatabase.getDatabase(requireContext())
        userDB=UsersDatabase.getDatabase(requireContext())
        // Fetch the viewModel from activityViewModels (shared ViewModel between fragments)
        val viewModel: ListingViewModel2 by activityViewModels()
        this.viewModel = viewModel

        // Get userId from SharedPreferences and pass it to the ViewModel
        val userId = getUserIdFromPrefs()
        viewModel.refreshListings(userId)

        // Initialize the adapter with filteredListings
        this.adapter = ListingAdapter(viewModel.filteredListings, viewLifecycleOwner)

        _binding = FragmentListingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // New listing button click listener
        binding.newListingButton.setOnClickListener {
            val intent = Intent(context, AddListingActivity::class.java)
            resultHandler.launch(intent)
        }

        // Set up RecyclerView
        binding.listingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listingRecycler.adapter = adapter

        // Observe ViewModel's isLoading to show loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.loadingText.text = getString(R.string.loading_items)
                binding.loadingText.visibility = View.VISIBLE
            }
        })

        // Observe allListings to show a message if no items are found
        viewModel.allListings.observe(viewLifecycleOwner, Observer { items ->
            binding.listingSearch.setQuery("", false)
            if (items.isEmpty()) {
                binding.loadingText.text = getString(R.string.no_items_found)
                binding.loadingText.visibility = View.VISIBLE
            } else {
                binding.loadingText.visibility = View.GONE
            }
        })

        // Observe search query and filter listings based on search input
        viewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            viewModel.filterListings(query)
        })

        // Set up search functionality


        // Hide action bar for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return root
    }

    private fun getUserIdFromPrefs(): Int {
        // Fetch userId from SharedPreferences
        val sharedPrefs = SharedPreferences(requireContext())
        val username = sharedPrefs.getLogin().username.toString()

        // Query the UserDAO to get the userId based on the username
        val userDao = userDB.userDao()
        var userId = -1

        // Use a coroutine to fetch the userId asynchronously
        lifecycleScope.launch {
            userId = userDao.getIdByUsername(username)

            viewModel.refreshListings(userId)  // Refresh the listings for the user
        }

        return userId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}