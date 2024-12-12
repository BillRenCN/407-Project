package com.cs407.project.ui.messages

import android.content.res.ColorStateList
import android.util.Log
import android.util.TypedValue
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

        val backgroundAttribute = if (isMyMessage) {
            com.google.android.material.R.attr.colorPrimary
        } else {
            com.google.android.material.R.attr.colorSecondary
        }
        val backgroundColor = TypedValue().apply {
            holder.itemView.context.theme.resolveAttribute(backgroundAttribute, this, true)
        }.data

        holder.messageText.backgroundTintList = ColorStateList.valueOf(backgroundColor)

        val textColorAttribute = if (isMyMessage) {
            com.google.android.material.R.attr.colorOnPrimary
        } else {
            com.google.android.material.R.attr.colorOnSecondary
        }
        val textColor = TypedValue().apply {
            holder.itemView.context.theme.resolveAttribute(textColorAttribute, this, true)
        }.data
        holder.messageText.setTextColor(textColor)

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
