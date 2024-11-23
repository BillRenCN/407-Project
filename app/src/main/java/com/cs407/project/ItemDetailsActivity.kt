package com.cs407.project

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        val itemTitle: TextView = findViewById(R.id.item_title)
        val itemDescription: TextView = findViewById(R.id.item_description)
        val itemPrice: TextView = findViewById(R.id.item_price)

        val itemId = intent.getIntExtra("itemId", -1)

        if (itemId != -1) {
            val database = AppDatabase.getDatabase(applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                val item = database.itemDao().getItemById(itemId)
                if (item != null) {
                    runOnUiThread {
                        itemTitle.text = item.title
                        itemDescription.text = item.description
                        itemPrice.text = "$${item.price}"
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ItemDetailsActivity, "Item not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
