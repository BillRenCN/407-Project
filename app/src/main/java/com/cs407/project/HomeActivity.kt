package com.cs407.project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.item_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fabPostItem: FloatingActionButton = findViewById(R.id.fab_post_item)

        fabPostItem.setOnClickListener {
            startActivity(Intent(this, PostItemActivity::class.java))
        }

        itemAdapter = ItemAdapter(emptyList())
        recyclerView.adapter = itemAdapter

        loadItems()
    }


    private fun loadItems() {
        val database = AppDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val items: List<Item> = database.itemDao().getAllItems()
            runOnUiThread {
                recyclerView.adapter = ItemAdapter(items)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }
}
