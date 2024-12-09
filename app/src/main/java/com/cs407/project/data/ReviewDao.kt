package com.cs407.project.data

import androidx.room.*
import androidx.room.Entity

@Entity(primaryKeys = ["user", "reviewer"])
data class Review(
    val user: String,
    val reviewer: String,
    val date: String,
    val message: String,
    val iconResource: Int
)
@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM Review WHERE user = :user AND reviewer = :reviewer")
    suspend fun getReview(user: String, reviewer: String): Review?

    @Query("SELECT * FROM Review")
    suspend fun getAllReviews(): List<Review>

    @Delete
    suspend fun deleteReview(review: Review)
}