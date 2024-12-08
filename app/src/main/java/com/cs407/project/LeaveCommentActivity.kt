package com.cs407.project

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

class LeaveCommentActivity : AppCompatActivity() {

    private lateinit var adapter: ReviewsAdapter
    private val reviewsList = mutableListOf<String>()

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
        adapter = ReviewsAdapter(reviewsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle post button click
        btnPostReview.setOnClickListener {
            val reviewText = etWriteReview.text.toString().trim()
            if (reviewText.isNotEmpty()) {
                reviewsList.add(0, reviewText) // Add the new review at the beginning
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
