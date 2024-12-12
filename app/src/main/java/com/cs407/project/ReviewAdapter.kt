package com.cs407.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // Create the ViewHolder for each review item
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewIcon: ImageView = itemView.findViewById(R.id.review_icon)
        val reviewUser: TextView = itemView.findViewById(R.id.review_user)
        val reviewRating: TextView = itemView.findViewById(R.id.review_rating)
        val reviewDate: TextView = itemView.findViewById(R.id.review_date)
        val reviewMessage: TextView = itemView.findViewById(R.id.review_message)
    }

    // Inflate the layout for each review item and return the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_review, parent, false)
        return ReviewViewHolder(view)
    }

    // Bind the data to the view
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewIcon.setImageResource(review.iconResource )
        holder.reviewUser.text = review.user
        holder.reviewDate.text = review.date
        holder.reviewMessage.text = review.message
        holder.reviewRating.text = review.rating.toString()
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        return reviews.size
    }
}