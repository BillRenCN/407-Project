package com.cs407.project.ui.messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.project.data.Message
import com.cs407.project.data.MessagesDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.FragmentMessageListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MessageListFragment : Fragment() {

    private var _binding: FragmentMessageListBinding? = null
    private lateinit var userDB: UsersDatabase
    private lateinit var messagesDB: MessagesDatabase

    private val binding get() = _binding!!
    private lateinit var adapter: MessageListAdapter

    val conversations: MutableList<Message> = mutableListOf<Message>()
    var myUserId: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userDB = UsersDatabase.getDatabase(requireContext())
        messagesDB = MessagesDatabase.getDatabase(requireContext())


        val sharedPreferences = SharedPreferences(requireContext())
        val username = sharedPreferences.getLogin().username!!
        myUserId = runBlocking {
            userDB.userDao().getUserFromUsername(username)!!.userId
        }

        Log.d("MessageListFragment", "myUserId: $myUserId")

        this.adapter = MessageListAdapter(conversations, myUserId)

        _binding = FragmentMessageListBinding.inflate(layoutInflater)
        val root: View = binding.root

        binding.messageListRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.messageListRecycler.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            CoroutineScope(Dispatchers.IO).launch {
                refetchMessages()
            }
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return root
    }

    suspend fun refetchMessages() {
        val allMessages = messagesDB.messagesDao().getConversationsForUser(myUserId)

        for (message in allMessages) {
            val otherUserId =
                if (message.senderId == myUserId) message.receiverId else message.senderId

            val existingConversation = conversations.find { it.senderId == otherUserId || it.receiverId == otherUserId }

            if (existingConversation == null) {
                conversations.add(message)
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.notifyItemInserted(conversations.size - 1)
                }
            } else {
                if (message.timestamp > existingConversation.timestamp) {
                    conversations[conversations.indexOf(existingConversation)] = message
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.notifyItemChanged(conversations.size - 1)
                    }
                }
            }
        }

        binding.messageListWarning.visibility = if (conversations.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        Log.d("MessageListFragment", "onResume")
        CoroutineScope(Dispatchers.IO).launch {
            refetchMessages()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
