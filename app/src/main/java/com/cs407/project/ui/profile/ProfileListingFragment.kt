package com.cs407.project.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.FragmentProfileListingBinding
import com.cs407.project.ui.listing.ListingAdapter
import kotlinx.coroutines.launch

class ProfileListingFragment : Fragment() {

    private var _binding: FragmentProfileListingBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDB: AppDatabase
    private lateinit var userDB: UsersDatabase
    private lateinit var viewModel2: ProfileListingViewModel
    private lateinit var adapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Use ViewBinding to inflate the layout
        _binding = FragmentProfileListingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val userId=requireActivity().intent.getIntExtra("USER_ID", 0)
        // Initialize the database
        appDB = AppDatabase.getDatabase(requireContext())
        userDB = UsersDatabase.getDatabase(requireContext())

        // Fetch the ViewModel from activityViewModels (shared ViewModel between fragments)
        val viewModel: ProfileListingViewModel by activityViewModels()
        this.viewModel2 = viewModel

        // Get userId from SharedPreferences and pass it to the ViewModel

        viewModel.refreshListings(userId)

        // Initialize the adapter with filteredListings
        this.adapter = ListingAdapter(viewModel.filteredListings, viewLifecycleOwner)

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

        viewModel2.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
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

        // Set up search functionality with the viewModel
        binding.listingSearch.setOnQueryTextListener(SearchTextListener(viewModel))

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

            // Once userId is fetched, refresh listings for the user
            if (userId != -1) {
                viewModel2.refreshListings(userId)  // Refresh the listings for the user
            }
        }

        return userId
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        // Refresh the listings for the user when the fragment is resumed
        viewModel2.refreshListings(getUserIdFromPrefs())
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        activity?.finish()
        super.onDestroyView()
        // Nullify the binding when the view is destroyed to avoid memory leaks
        _binding = null
    }

    // SearchTextListener for search query handling
    class SearchTextListener(private val viewModel: ProfileListingViewModel) : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // Set the search query in the ViewModel
            viewModel.setSearchQuery(query ?: "")
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            // Set the search query in the ViewModel
            viewModel.setSearchQuery(query ?: "")
            return true
        }
    }
}
