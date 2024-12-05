package com.cs407.project

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        val itemTitle: TextView = findViewById(R.id.item_title) // Updated to match the correct ID
        val itemDescription: TextView = findViewById(R.id.item_description)
        val itemPrice: TextView = findViewById(R.id.item_price)
        val itemImage: ImageView = findViewById(R.id.item_image)

        val itemId = intent.getIntExtra("itemId", -1)

        if (itemId != -1) {
            val database = AppDatabase.getDatabase(applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                val item = database.itemDao().getItemById(itemId)
                runOnUiThread {
                    if (item != null) {
                        itemTitle.text = item.title // Ensure nullable item is handled safely
                        itemDescription.text = item.description
                        itemPrice.text = "$${item.price}"
                        item.imageUrl?.let { imageUri ->
                            itemImage.setImageURI(Uri.parse(imageUri))
                        }
                    }
                }
            }
        }
    }
}
