package com.cs407.project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import com.cs407.project.databinding.ActivityPostItemBinding
import kotlinx.coroutines.launch

class PostItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostItemBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database
        database = AppDatabase.getDatabase(this)

        binding.btnListItem.setOnClickListener {
            if (binding.checkboxAgree.isChecked) {
                addItemToDatabase()
            } else {
                Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addItemToDatabase() {
        val title = binding.itemTitle.text.toString()
        val description = binding.itemDescription.text.toString()
        val price = binding.itemPrice.text.toString().toDoubleOrNull() ?: 0.0
        val userId = 1 // Replace with the actual user ID in a real app

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val newItem = Item(
                title = title,
                description = description,
                price = price,
                userId = userId
            )

            // Insert the item into the database
            lifecycleScope.launch {
                database.itemDao().insertItem(newItem)
                Toast.makeText(this@PostItemActivity, "Item posted successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
