package com.cs407.project.ui.trade_feedback


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.data.Review

class AllReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<AllReviewAdapter.ReviewViewHolder>() {

    // Create the ViewHolder for each review item
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewIcon: ImageView = itemView.findViewById(R.id.review_icon)
        val reviewUser: TextView = itemView.findViewById(R.id.review_user)
        val reviewRating: RatingBar = itemView.findViewById(R.id.ratingBar2)
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
        holder.reviewRating.rating = review.rating
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        return reviews.size
    }
}