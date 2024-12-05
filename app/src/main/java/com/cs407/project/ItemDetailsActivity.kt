package com.cs407.project

import android.os.Bundle
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
    }

    private fun loadItemDetails(itemId: Int) {
        lifecycleScope.launch {
            val item = database.itemDao().getItemById(itemId)
            item?.let {
                binding.itemImage.setImageResource(R.drawable.ic_placeholder_image) // Placeholder image
                binding.itemName.text = it.title
                binding.itemPrice.text = buildString {
                    append("$")
                    append(it.price)
                }
                binding.itemDescription.text = it.description
            }
        }
    }
}
