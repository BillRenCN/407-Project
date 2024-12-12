package com.cs407.project.ui.trade_feedback

import android.os.Build
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Review
import com.cs407.project.data.ReviewDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch


class ReviewListActivity : AppCompatActivity() {
    private lateinit var adapter: ReviewListAdapter
    private var reviewsList = mutableListOf<Review>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_reviews)
        // Enable edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val itemId = intent.extras!!.getInt("ITEM_ID", 0)
        val averageScore: RatingBar = findViewById(R.id.ratingBar3)
        var tvReviewsCount: TextView = findViewById(R.id.tvReviewsCount)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val db = ReviewDatabase.getDatabase(this)
        val reviewDao = db.reviewDao()
        lifecycleScope.launch {
            reviewsList = reviewDao.getReviewsByGoodsId(itemId).toMutableList()
            tvReviewsCount.text = reviewsList.size.toString() + " Reviews"
            if (reviewsList.isNotEmpty()) {
                var allScore = 0.0f
                for (review in reviewsList) {
                    allScore += review.rating
                }
                averageScore.rating = allScore / reviewsList.size
            }
            adapter = ReviewListAdapter(reviewsList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val item = AppDatabase.getDatabase(this@ReviewListActivity).itemDao().getItemById(itemId)!!
            runOnUiThread {
                supportActionBar?.title = "Reviews for '${item.title}'"
            }
        }

    }
}
