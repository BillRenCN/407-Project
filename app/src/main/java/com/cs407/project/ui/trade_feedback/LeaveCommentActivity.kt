package com.cs407.project.ui.trade_feedback

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.Review
import com.cs407.project.ReviewAdapter

class LeaveCommentActivity : AppCompatActivity() {

    private lateinit var adapter: ReviewAdapter
    private val reviewsList = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leave_comment)

        // Enable edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val etWriteReview: EditText = findViewById(R.id.etWriteReview)
        val btnPostReview: Button = findViewById(R.id.btnPostReview)
        val tvReviewsCount: TextView = findViewById(R.id.tvReviewsCount)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Set up RecyclerView with adapter
        adapter = ReviewAdapter(reviewsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle post button click
        btnPostReview.setOnClickListener {
            val reviewText = etWriteReview.text.toString().trim()
            if (reviewText.isNotEmpty()) {
                val newReview = Review(
                    iconResource = 5, // 假设的默认图标
                    user = "Anonymous", // 假设的默认用户名
                    date = "Today", // 假设的默认日期
                    message = reviewText,
                    reviewer = ""
                )
                reviewsList.add(0, newReview) // Add the new review at the beginning
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0) // Scroll to the top to show the latest review

                // Update review count
                tvReviewsCount.text = "${reviewsList.size} Reviews"

                // Clear the input field
                etWriteReview.text.clear()
            }
        }
    }
}
