package com.cs407.project.ui.messages

import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.data.Message
import com.cs407.project.data.UsersDatabase
import kotlinx.coroutines.runBlocking

class MessageListAdapter(
    private val conversation: List<Message>, private val myUserId: Int
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_message_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("MessageListAdapter", "conversations: $conversation")
        val model = conversation[position]
        Log.d("MessageListAdapter", "model: $model")

        val isMyMessage = model.senderId == myUserId

        val otherPerson = if (isMyMessage) {
            model.receiverId
        } else {
            model.senderId
        }

        val userDB = UsersDatabase.getDatabase(holder.itemView.context)

        if (model.message.length > 175) {
            holder.messageText.text = model.message.substring(0, 175) + "..."
        } else {
            holder.messageText.text = model.message
        }
        runBlocking {
            holder.username.text = userDB.userDao().getById(otherPerson).username
        }

        holder.timestamp.text = DateUtils.formatDateTime(
            holder.itemView.context, model.timestamp, DateUtils.FORMAT_SHOW_TIME
        )

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessagesActivity::class.java)
            intent.putExtra("USER_ID", otherPerson)
            intent.putExtra("USER_NAME", holder.username.text)
            intent.putExtra("MY_USER_ID", myUserId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return conversation.size
    }

    class ViewHolder(messageListView: View) : RecyclerView.ViewHolder(messageListView) {
        val messageText: TextView = messageListView.findViewById(R.id.messageListSummary)
        val username: TextView = messageListView.findViewById(R.id.messageListUsername)
        val timestamp: TextView = messageListView.findViewById(R.id.messageListTimestamp)
    }
}
