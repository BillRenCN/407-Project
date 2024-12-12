package com.cs407.project.ui.messages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.data.Message
import java.text.SimpleDateFormat

class MessagesAdapter(
    private val messages: List<Message>, private val myUserId: Int
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("MessagesAdapter", "onCreateViewHolder called")
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = messages[position]

        holder.messageText.text = model.message
        val sender = model.senderId

        val isMyMessage = sender == myUserId
        val constraintSet = ConstraintSet()
        constraintSet.clone(holder.messageLayout)
        if (isMyMessage) {
            constraintSet.setHorizontalBias(R.id.messageTextView, 1f)
        } else {
            constraintSet.setHorizontalBias(R.id.messageTextView, 0f)
        }
        constraintSet.applyTo(holder.messageLayout)
        holder.messageText.backgroundTintList = if (isMyMessage) {
            holder.itemView.context.getColorStateList(com.google.android.material.R.color.design_default_color_primary)
        } else {
            holder.itemView.context.getColorStateList(R.color.black)
        }

        holder.itemView.setOnClickListener {
            val dateTime = SimpleDateFormat.getDateTimeInstance().format(model.timestamp)
            Toast.makeText(it.context, "Message sent at $dateTime", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(messageView: View) : RecyclerView.ViewHolder(messageView) {
        val messageText: TextView = messageView.findViewById(R.id.messageTextView)
        val messageLayout: ConstraintLayout = messageView.findViewById(R.id.messageLayout)
    }
}
