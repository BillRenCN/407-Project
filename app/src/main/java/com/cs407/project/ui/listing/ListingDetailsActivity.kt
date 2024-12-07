package com.cs407.project.ui.listing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityItemDetailsBinding
import com.cs407.project.lib.displayImage
import com.cs407.project.ui.profile.ProfileActivity
import com.cs407.project.ui.trade_feedback.LeaveCommentActivity
import com.cs407.project.ui.trade_feedback.ScheduleTradeActivity
import kotlinx.coroutines.launch

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

        // Initialize the database
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
        binding.btnLeaveComment.setOnClickListener {
            Toast.makeText(this, "Leave a Comment clicked", Toast.LENGTH_SHORT).show()
            // You can add the logic for leaving a comment here
            val intent = Intent(this, LeaveCommentActivity::class.java)
            intent.putExtra("ITEM_ID", userId)
            this.startActivity(intent)
        }

        binding.btnScheduleTrade.setOnClickListener {
            val intent = Intent(this, ScheduleTradeActivity::class.java)
            intent.putExtra("ITEM_ID", userId)
            Toast.makeText(this, "ScheduleTrade clicked", Toast.LENGTH_SHORT).show()
            this.startActivity(intent)
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
            binding.sellerName.text = user.username
            val ratingStr = buildString {
                append(user.rating)
                append("/5")
            }
            binding.sellerRatingSales.text =
                getString(R.string.rating_sales, ratingStr, user.sales)
            displayImage(user.userId, binding.sellerImage, "user")
            displayImage(item.id, binding.itemImage, "listing")
        }

    }
}
