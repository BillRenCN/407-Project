package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "review", primaryKeys = ["sellerId", "buyerId"])
data class Review(
    val sellerId: Int,
    val buyerId: Int,
    val itemId: Int,
    val rate: Int,
    val comment: String
)

@Dao
interface ReviewDao {
    // Insert a review into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    // Query all reviews by sellerId
    @Query("SELECT * FROM review WHERE sellerId = :sellerId")
    suspend fun getReviewsBySellerId(sellerId: Int): List<Review>

    // Query all reviews by itemId
    @Query("SELECT * FROM review WHERE itemId = :itemId")
    suspend fun getReviewsByItemId(itemId: Int): List<Review>
}
