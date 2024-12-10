package com.cs407.project.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.R
import com.cs407.project.data.ReviewDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityProfileBinding
import com.cs407.project.lib.displayImage
import com.cs407.project.ui.listing.ListingFragment
import com.cs407.project.ui.messages.MessagesActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var usersDatabase: UsersDatabase
    private var userId: Int = 0
    private lateinit var reviewDB: ReviewDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersDatabase = UsersDatabase.getDatabase(this)
        reviewDB = ReviewDatabase.getDatabase(this)
        userId = intent.getIntExtra("USER_ID", 0)


        loadUserDetails(userId, this)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.list,
                ListingFragment3::class.java,
                null
            )
            .setReorderingAllowed(true)
            .addToBackStack("loading listing fragment")
            .commit()
    }

    private fun loadUserDetails(userId: Int, context: Context) {
        lifecycleScope.launch {
            val user = usersDatabase.userDao().getById(userId)
            val urlString=user.imageUrl
            val date=user.date
            if (user == null) {
                Toast.makeText(context, "Error loading user", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            binding.username.text = user.username
            binding.description.text = "Member since " + convertUnixDateToString(date)
            if (urlString!=null){
                val uri: Uri = Uri.parse(urlString)
                binding.profilepic.setImageURI(uri)
            }
            val reviews=reviewDB.reviewDao().getReviewsByUser(user.username)
            if (reviews.isEmpty()){
                binding.rating.text="No Reviews"
            }
            else{
                val rating=reviews.map { it.iconResource }.average()*20
                val count=reviews.size
                binding.rating.text=rating.toString()+"% positive feedback ("+count.toString()+")"
            }
            binding.myButton.setOnClickListener {
                lifecycleScope.launch {
                    val myUserId = usersDatabase.userDao()
                        .getUserFromUsername(SharedPreferences(this@ProfileActivity).getLogin().username!!)!!.userId
                    Log.d("ListingDetailsActivity", "My User ID: $myUserId")
                    if (userId == myUserId) {
                        Toast.makeText(this@ProfileActivity, "You cannot message yourself", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    val intent = Intent(this@ProfileActivity, MessagesActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("MY_USER_ID", myUserId)
                    intent.putExtra("USER_NAME", user.username)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun convertUnixDateToString(unixDate: Long): String {
        // Create a Date object from the Unix timestamp
        val date = Date(unixDate * 1000)  // Unix timestamps are in seconds, so multiply by 1000 for milliseconds
        // Define the desired format: MM/dd/yyyy
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        // Return the formatted date string
        return dateFormat.format(date)
    }
}