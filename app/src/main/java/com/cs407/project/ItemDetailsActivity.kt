package com.cs407.project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.databinding.ActivityItemDetailsBinding
import kotlinx.coroutines.launch

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailsBinding
    private lateinit var database: AppDatabase
    private var itemId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database
        database = AppDatabase.getDatabase(this)

        // Retrieve item ID passed through intent
        itemId = intent.getIntExtra("ITEM_ID", 0)

        // Load item details from the database
        loadItemDetails(itemId)

        // Jump to Schedule Trade
        binding.btnLeaveComment.setOnClickListener {
            Toast.makeText(this, "Leave a Comment clicked", Toast.LENGTH_SHORT).show()
            // You can add the logic for leaving a comment here
            val intent = Intent(this, LeaveCommentActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }

        binding.btnScheduleTrade.setOnClickListener {
            val intent = Intent(this, ScheduleTradeActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }

        binding.btnViewReviews.setOnClickListener {
            Toast.makeText(this, "View All Reviews clicked", Toast.LENGTH_SHORT).show()
            // You can add the logic for viewing reviews here
        }
    }

    private fun loadItemDetails(itemId: Int) {
        lifecycleScope.launch {
            val item = database.itemDao().getItemById(itemId)
            item?.let {
                binding.itemImage.setImageResource(R.drawable.ic_placeholder_image) // Placeholder image
                binding.itemName.text = it.title
                binding.itemPrice.text = "$${it.price}"
                binding.itemDescription.text = it.description
            }
        }
    }
}
