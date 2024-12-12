package com.cs407.project.ui.profile.self

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivitySelfListingDetailsBinding
import com.cs407.project.lib.displayImage
import com.cs407.project.ui.trade_feedback.ReviewListActivity
import kotlinx.coroutines.launch

class SelfListingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelfListingDetailsBinding
    private lateinit var database: AppDatabase
    private lateinit var usersDatabase: UsersDatabase
    private var itemId: Int = 0
    private var userId: Int = 0

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
        }

        binding.btnViewReviews.setOnClickListener {
            val intent = Intent(this@SelfListingDetailsActivity, ReviewListActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
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
            binding.itemImage.setImageResource(R.drawable.ic_placeholder_image) // Placeholder image
            binding.itemName.text = item.title
            binding.itemPrice.text = buildString {
                append("$")
                append(item.price)
            }
            binding.itemDescription.text = item.description

            buildString {
                append(user.rating)
                append("/5")
            }

            displayImage(binding.itemImage, item.imageUrl)
        }

    }
}
