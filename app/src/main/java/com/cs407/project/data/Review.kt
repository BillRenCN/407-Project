package com.cs407.project.data

import androidx.room.Entity

@Entity(
    tableName = "reviews",
    primaryKeys = ["sellerId", "buyerId"]
)
data class Review(
    val sellerId: Int,
    val buyerId: Int,
    val itemId: Int,
    val rate: Int,
    val comment: String,
    val date: Long, // Timestamp for the review date
    val address: String // Address related to the review
)