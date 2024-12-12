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
import com.cs407.project.ui.trade_feedback.AllReviewsActivity
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

        // Retrieve item ID passed through intent
        itemId = intent.getIntExtra("ITEM_ID", 0)

        // Load item details from the database
        loadItemDetails(itemId, this)

        binding.btnViewProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_ID", userId)
            this.startActivity(intent)
        }

        supportActionBar?.hide()
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
            }
            binding.btnLeaveComment.setOnClickListener {
                lifecycleScope.launch {
                    Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                    if (item.userId == myUserId) {
                        Toast.makeText(context, "You cannot leave a comment on your own item", Toast.LENGTH_LONG)
                            .show()
                        return@launch
                    }
                    //Toast.makeText(context, "Leave a Comment clicked", Toast.LENGTH_SHORT).show()
                        // You can add the logic for leaving a comment here
                    val intent = Intent(context, LeaveCommentActivity::class.java)
                    intent.putExtra("ITEM_ID", itemId)
                    context.startActivity(intent)
                    }
            }
            binding.btnScheduleTrade.setOnClickListener {
                lifecycleScope.launch {
                    Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                    if (item.userId == myUserId) {
                        Toast.makeText(context, "You cannot schedule a trade with yourself", Toast.LENGTH_LONG)
                            .show()
                        return@launch
                    }
                    val intent = Intent(context, ScheduleTradeActivity::class.java)
                    intent.putExtra("ITEM_ID", itemId)
                    intent.putExtra("USER_ID", userId)
                    Toast.makeText(context, "ScheduleTrade clicked", Toast.LENGTH_SHORT).show()
                    context.startActivity(intent)
                }

            }
            binding.btnViewReviews.setOnClickListener {
                lifecycleScope.launch {
                    Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                    val intent = Intent(context, AllReviewsActivity::class.java)
                    intent.putExtra("ITEM_ID", itemId)
                    context.startActivity(intent)
                }
            }

            // Display seller image
            if (user.imageUrl != null) {
                displayImage(binding.sellerImage, user.imageUrl)
            }

            // Display item image
            displayImage(binding.itemImage, item.imageUrl)
        }
    }

}