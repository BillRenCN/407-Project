package com.cs407.project.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityProfileBinding
import com.cs407.project.lib.displayImage
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var usersDatabase: UsersDatabase
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersDatabase = UsersDatabase.getDatabase(this)

        userId = intent.getIntExtra("USER_ID", 0)

        loadUserDetails(userId, this)
    }

    private fun loadUserDetails(userId: Int, context: Context) {
        lifecycleScope.launch {
            val user = usersDatabase.userDao().getById(userId)
            if (user == null) {
                Toast.makeText(context, "Error loading user", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            binding.name.text = user.username
            binding.rating.rating = user.rating.toFloat()
            binding.description.text = user.description
            displayImage(user.userId, binding.image, "user")
        }
    }

}