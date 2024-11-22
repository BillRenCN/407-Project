package com.cs407.project

import android.os.Bundle
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

        val itemId = intent.getIntExtra("ITEM_ID", -1) // Retrieve item ID from intent
        val database = AppDatabase.getDatabase(applicationContext)

        if (itemId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val item = database.itemDao().getItemById(itemId)
                item?.let {
                    runOnUiThread {
                        // Bind data to the UI
                        findViewById<TextView>(R.id.item_title).text = it.title
                        findViewById<TextView>(R.id.item_description).text = it.description
                        findViewById<TextView>(R.id.item_price).text = "$${it.price}"
                    }
                }
            }
        }
    }
}
