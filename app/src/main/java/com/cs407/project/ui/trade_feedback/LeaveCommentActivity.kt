package com.cs407.project.ui.trade_feedback

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.User
import com.cs407.project.data.UsersDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LeaveCommentActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var adapter: ReviewAdapter
    private val reviewsList = mutableListOf<Review>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leave_comment)
        sharedPrefs = SharedPreferences(this)
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
                var username=""
                var reviewerName = ""
                CoroutineScope(Dispatchers.IO).launch {
                    val loginInfo = sharedPrefs.getLogin()
                    val userId = usersDatabase.userDao().getUserFromUsername(loginInfo.username!!)?.userId
                    val user = userDao.getById(userId!!)
                    val sellerId = itemDao.getItemById(itemId)!!.userId
                    val seller = userDao.getById(sellerId)
                    val currentDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedDate = currentDateTime.format(formatter)
                    val review = com.cs407.project.data.Review(
                        user = seller.username,
                        reviewer = user.username,
                        date = formattedDate, // Use the formatted date here
                        message = reviewText,
                        iconResource = R.mipmap.ic_launcher // Replace with an actual drawable
                    )
                    reviewDao.insertReview(review)
                    username = user.username
                    reviewerName = user.username
                }

                val newReview = Review(
                    iconResource =  R.mipmap.ic_launcher, // 假设的默认图标
                    user = username , // 假设的默认用户名
                    date = getCurrentTime(), // 假设的默认日期
                    message = reviewText,
                    reviewer = reviewerName
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter)
    }
}