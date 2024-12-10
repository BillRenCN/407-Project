package com.cs407.project.ui.trade_feedback

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.Review
import com.cs407.project.ReviewAdapter
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.ReviewDatabase
import com.cs407.project.data.UsersDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LeaveCommentActivity : AppCompatActivity() {

    private lateinit var adapter: ReviewAdapter
    private val reviewsList = mutableListOf<Review>()

    @RequiresApi(Build.VERSION_CODES.O)
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
        val db = ReviewDatabase.getDatabase(this)
        val reviewDao = db.reviewDao()
        val appDatabase = AppDatabase.getDatabase(this)
        val usersDatabase = UsersDatabase.getDatabase(this)
        val itemDao = appDatabase.itemDao()
        val userDao = usersDatabase.userDao()

        // Set up RecyclerView with adapter
        adapter = ReviewAdapter(reviewsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle post button click
        btnPostReview.setOnClickListener {
            val reviewText = etWriteReview.text.toString().trim()
            if (reviewText.isNotEmpty()) {
                val intent = intent
                val itemId = intent.getIntExtra("ITEM_ID", -1)
                val buyerId = intent.getIntExtra("USER_ID", -1)
                // add time save in string
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.getById(buyerId)
                    val sellerId = itemDao.getItemById(itemId)!!.userId
                    val seller = userDao.getById(sellerId)
                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedDate = currentDateTime.format(formatter)
                    val review = com.cs407.project.data.Review(
                        user = user.username,
                        reviewer = seller.username,
                        date = formattedDate, // Use the formatted date here
                        message = "test",
                        iconResource = R.drawable.ic_launcher_foreground // Replace with an actual drawable
                    )
                    reviewDao.insertReview(review)
                }
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
