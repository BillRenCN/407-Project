package com.cs407.project.ui.listing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.cs407.project.MainActivity
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivitySelfListingDetailsBinding
import com.cs407.project.lib.displayImage
import com.cs407.project.ui.profile.ProfileActivity
import com.cs407.project.ui.trade_feedback.LeaveCommentActivity
import com.cs407.project.ui.trade_feedback.ScheduleTradeActivity
import com.cs407.project.ui.listing.SelfListingAdapter
import com.cs407.project.ui.listing.ListingViewModel
import com.cs407.project.ui.profile.ListingViewModel2
import kotlinx.coroutines.launch

class SelfListingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelfListingDetailsBinding
    private lateinit var database: AppDatabase
    private lateinit var userDB: UsersDatabase
    private lateinit var usersDatabase: UsersDatabase
    private var itemId: Int = 0
    private var userId: Int = 0
    private lateinit var viewModel2: ListingViewModel2
    private lateinit var viewModel: ListingViewModel
    private lateinit var adapter: SelfListingAdapter

    @SuppressLint("NotifyDataSetChanged")
    val resultHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // After a new listing is added, refresh the listings for the user.
            viewModel2.refreshListings(getUserIdFromPrefs())
            adapter.notifyDataSetChanged()
        }

    @SuppressLint("NotifyDataSetChanged")
    val resultHandler2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.refreshListings()
            adapter.notifyDataSetChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database
        database = AppDatabase.getDatabase(this)
        usersDatabase = UsersDatabase.getDatabase(this)

        // Retrieve item ID passed through intent
        itemId = intent.getIntExtra("ITEM_ID", 0)

        // Load item details from the database
        loadItemDetails(itemId, this)



        binding.btnScheduleTrade.setOnClickListener {
            lifecycleScope.launch {
                // Delete the item from the database
                database.itemDao().deleteItemById(itemId)
            }
            // Close the activity and return to the previous screen
            Toast.makeText(this@SelfListingDetailsActivity, "Item removed successfully", Toast.LENGTH_SHORT).show()
            finish() // End the current activity
            val intent2 = Intent(this@SelfListingDetailsActivity, MainActivity::class.java)

            resultHandler2.launch(intent2)
            resultHandler.launch(intent2)

        }

        binding.btnViewReviews.setOnClickListener {
            Toast.makeText(this, "View All Reviews clicked", Toast.LENGTH_SHORT).show()
            // You can add the logic for viewing reviews here
        }
    }

    private fun loadItemDetails(itemId: Int, context: Context) {
        lifecycleScope.launch {
            val item = database.itemDao().getItemById(itemId)
            if (item == null) {
                Toast.makeText(context, "Error loading item", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            val user = usersDatabase.userDao().getById(item.userId)
            if (user == null) {
                Toast.makeText(context, "Error loading user", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            userId = user.userId
            binding.itemImage.setImageResource(R.drawable.ic_placeholder_image) // Placeholder image
            binding.itemName.text = item.title
            binding.itemPrice.text = buildString {
                append("$")
                append(item.price)
            }
            binding.itemDescription.text = item.description

            val ratingStr = buildString {
                append(user.rating)
                append("/5")
            }

            displayImage(item.id, binding.itemImage, "listing")
        }

    }
    private fun getUserIdFromPrefs(): Int {
        // Fetch userId from SharedPreferences
        val sharedPrefs = SharedPreferences(this)
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
}
