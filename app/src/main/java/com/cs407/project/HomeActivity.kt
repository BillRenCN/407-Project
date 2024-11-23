package com.cs407.project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.item_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fabPostItem: FloatingActionButton = findViewById(R.id.fab_post_item)

        // Navigate to Post Item Screen
        fabPostItem.setOnClickListener {
            startActivity(Intent(this, PostItemActivity::class.java))
        }

        // Initialize adapter with an empty list and set it to the RecyclerView
        adapter = ItemAdapter(listOf()) { item ->
            val intent = Intent(this, ItemDetailsActivity::class.java)
            intent.putExtra("itemId", item.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Load items initially
        loadItems()
    }

    override fun onResume() {
        super.onResume()
        // Reload items every time the activity becomes visible
        loadItems()
    }

    private fun loadItems() {
        val database = AppDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val items: List<Item> = database.itemDao().getAllItems()
            runOnUiThread {
                adapter.updateItems(items)
            }
        }
    }
}
