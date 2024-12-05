package com.cs407.project.ui.listing

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityItemDetailsBinding
import com.cs407.project.lib.displayImage
import kotlinx.coroutines.launch

class ListingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailsBinding
    private lateinit var database: AppDatabase
    private lateinit var usersDatabase: UsersDatabase
    private var itemId: Int = 0

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
    }

    private fun loadItemDetails(itemId: Int, context: Context) {
        lifecycleScope.launch {
            val item = database.itemDao().getItemById(itemId)
            if (item == null) {
                Toast.makeText(context, "Error loading item", Toast.LENGTH_LONG).show()
            } else {
                val user = usersDatabase.userDao().getById(item.userId)
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
}
