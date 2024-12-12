package com.cs407.project.ui.messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.data.Message
import com.cs407.project.data.MessagesDatabase
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityMessagesBinding
import kotlinx.coroutines.launch

class MessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessagesBinding
    private lateinit var usersDatabase: UsersDatabase
    private lateinit var messagesDatabase: MessagesDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersDatabase = UsersDatabase.getDatabase(this)
        messagesDatabase = MessagesDatabase.getDatabase(this)
        val messagesDao = messagesDatabase.messagesDao()

        val myUserId = intent.getIntExtra("MY_USER_ID", 0)
        val otherUserId = intent.getIntExtra("USER_ID", 0)
        val otherUsername = intent.getStringExtra("USER_NAME")
        var messages: MutableList<Message> = mutableListOf()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.messagesRecycler.layoutManager = layoutManager
        binding.messagesRecycler.adapter = MessagesAdapter(messages, myUserId)

        lifecycleScope.launch {
            val allMessages = messagesDao.getMessagesByParticipants(otherUserId, myUserId)
            messages.addAll(allMessages)
            Log.d("MessagesActivity", "messages: $messages")
            binding.messagesRecycler.adapter?.notifyDataSetChanged()
        }

        binding.imageButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (otherUserId == 0) {
                Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("MessagesActivity", "otherUserId: $otherUserId")
            lifecycleScope.launch {
                val message = Message(myUserId, otherUserId, text, System.currentTimeMillis())
                messagesDao.insertItem(message)
                messages.add(message)
                Toast.makeText(this@MessagesActivity, "Message sent", Toast.LENGTH_SHORT).show()
                binding.messagesRecycler.adapter?.notifyItemInserted(messages.size - 1)
                binding.messageEditText.text.clear()
            }

        }

        val supportActionBar = (this as AppCompatActivity).supportActionBar
        supportActionBar?.title = "Message @$otherUsername"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}