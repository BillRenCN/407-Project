package com.cs407.project.data

import androidx.room.*
import androidx.room.Entity

@Entity
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // 自动生成的主键，默认为0
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

    @Query("SELECT * FROM Review WHERE user = :user")
    suspend fun getReviewsByUser(user: String): List<Review>
}