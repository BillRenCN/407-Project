package com.cs407.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_item)

        val titleInput: EditText = findViewById(R.id.item_title)
        val descriptionInput: EditText = findViewById(R.id.item_description)
        val priceInput: EditText = findViewById(R.id.item_price)
        val submitButton: Button = findViewById(R.id.btn_list_item)

        submitButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull()
            val userId = 1 // Placeholder: Replace with logic to get current user ID if available

            if (title.isNotEmpty() && description.isNotEmpty() && price != null) {
                val newItem = Item(
                    title = title,
                    description = description,
                    price = price,
                    userId = userId
                )

                val database = AppDatabase.getDatabase(applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    database.itemDao().insertItem(newItem)
                }
                Toast.makeText(this, "Item Posted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
