package com.cs407.project.ui.listing

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityItemDetailsBinding
import com.cs407.project.lib.displayImage
import com.cs407.project.ui.messages.MessagesActivity
import com.cs407.project.ui.profile.ProfileActivity
import com.cs407.project.ui.trade_feedback.ReviewListActivity
import com.cs407.project.ui.trade_feedback.LeaveCommentActivity
import com.cs407.project.ui.trade_feedback.ScheduleTradeActivity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency

class ListingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailsBinding
    private lateinit var database: AppDatabase
    private lateinit var usersDatabase: UsersDatabase
    private var itemId: Int = 0
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        usersDatabase = UsersDatabase.getDatabase(this)

        try {
            // Retrieve item ID passed through intent
            itemId = intent.getIntExtra("ITEM_ID", 0)

            // Load item details from the database
            loadItemDetails(itemId, this)
        } catch (e: Exception) {
            Log.e("ListingDetailsActivity", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Failed to initialize item details", Toast.LENGTH_LONG).show()
        }

        binding.btnViewProfile.setOnClickListener {
            try {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_ID", userId)
                this.startActivity(intent)
            } catch (e: Exception) {
                Log.e("ListingDetailsActivity", "Error launching ProfileActivity: ${e.message}")
                Toast.makeText(this, "Failed to view profile", Toast.LENGTH_SHORT).show()
            }
        }

        supportActionBar?.hide()
    }

    private fun loadItemDetails(itemId: Int, context: Context) {
        lifecycleScope.launch {
            try {
                val item = database.itemDao().getItemById(itemId)
                if (item == null) {
                    Toast.makeText(context, "Error loading item", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }
                val user = usersDatabase.userDao().getById(item.userId)
                userId = user.userId

                // Set item details
                binding.itemName.text = item.title
                val priceFormat = NumberFormat.getCurrencyInstance()
                priceFormat.currency = Currency.getInstance("USD")
                binding.itemPrice.text = priceFormat.format(item.price)
                binding.itemDescription.text = item.description
                binding.sellerName.text = user.username
                val ratingStr = buildString {
                    append(user.rating)
                    append("/5")
                }
                binding.sellerRatingSales.text = getString(R.string.rating_sales, ratingStr, user.sales)

                val myUserId = usersDatabase.userDao()
                    .getUserFromUsername(SharedPreferences(this@ListingDetailsActivity).getLogin().username!!)!!.userId

                if (item.userId == myUserId) {
                    binding.messageButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    binding.btnLeaveComment.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    binding.btnScheduleTrade.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }

                // Message button logic
                binding.messageButton.setOnClickListener {
                    try {
                        lifecycleScope.launch {
                            Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                            if (item.userId == myUserId) {
                                Toast.makeText(context, "You cannot message yourself", Toast.LENGTH_LONG).show()
                                return@launch
                            }
                            val intent = Intent(context, MessagesActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            intent.putExtra("MY_USER_ID", myUserId)
                            intent.putExtra("USER_NAME", user.username)
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.e("ListingDetailsActivity", "Error handling message button: ${e.message}")
                        Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
                }

                binding.btnLeaveComment.setOnClickListener {
                    try {
                        lifecycleScope.launch {
                            Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                            if (item.userId == myUserId) {
                                Toast.makeText(context, "You cannot leave a comment on your own item", Toast.LENGTH_LONG)
                                    .show()
                                return@launch
                            }
                            val intent = Intent(context, LeaveCommentActivity::class.java)
                            intent.putExtra("ITEM_ID", itemId)
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.e("ListingDetailsActivity", "Error handling leave comment button: ${e.message}")
                        Toast.makeText(context, "Failed to leave comment", Toast.LENGTH_SHORT).show()
                    }
                }

                binding.btnScheduleTrade.setOnClickListener {
                    try {
                        lifecycleScope.launch {
                            Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                            if (item.userId == myUserId) {
                                Toast.makeText(context, "You cannot schedule a trade with yourself", Toast.LENGTH_LONG)
                                    .show()
                                return@launch
                            }
                            val intent = Intent(context, ScheduleTradeActivity::class.java)
                            intent.putExtra("ITEM_ID", item.id)
                            intent.putExtra("USER_ID", item.userId)
                            intent.putExtra("MY_USER_ID", myUserId)
                            Toast.makeText(context, "ScheduleTrade clicked", Toast.LENGTH_SHORT).show()
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.e("ListingDetailsActivity", "Error handling schedule trade button: ${e.message}")
                        Toast.makeText(context, "Failed to schedule trade", Toast.LENGTH_SHORT).show()
                    }
                }

                binding.btnViewReviews.setOnClickListener {
                    try {
                        lifecycleScope.launch {
                            Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                            val intent = Intent(context, ReviewListActivity::class.java)
                            intent.putExtra("ITEM_ID", itemId)
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.e("ListingDetailsActivity", "Error handling view reviews button: ${e.message}")
                        Toast.makeText(context, "Failed to view reviews", Toast.LENGTH_SHORT).show()
                    }
                }

                // Display seller image
                try {
                    if (user.imageUrl != null) {
                        displayImage(binding.sellerImage, user.imageUrl)
                    }
                } catch (e: Exception) {
                    Log.e("ListingDetailsActivity", "Error displaying seller image: ${e.message}")
                }

                // Display item image
                try {
                    displayImage(binding.itemImage, item.imageUrl)
                } catch (e: Exception) {
                    Log.e("ListingDetailsActivity", "Error displaying item image: ${e.message}")
                }

            } catch (e: Exception) {
                Log.e("ListingDetailsActivity", "Error loading item details: ${e.message}")
                Toast.makeText(context, "Failed to load item details", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
