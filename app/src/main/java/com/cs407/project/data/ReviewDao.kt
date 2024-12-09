package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews WHERE sellerId = :sellerId AND buyerId = :buyerId LIMIT 1")
    suspend fun getReviewBySellerAndBuyer(sellerId: Int, buyerId: Int): Review?
}


