package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cs407.project.data.Item

@Dao
interface ItemDao {
    // Insert an item into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    // Get a single item by its ID
    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Int): Item?

    // Get all items
    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    // Get all items by a specific user ID
    @Query("SELECT * FROM items WHERE userId = :userId")
    suspend fun getItemsByUserId(userId: Int): List<Item>

    // Delete an item by its ID
    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)
}
