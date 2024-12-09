package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

// For some reason if the entity is separate from the DAO it will fail to compile sometimes
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val userId: Int,
   val imageUrl: String? = null // Optional URL for the image
)

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

    // Search for item using title
    @Query("SELECT * FROM items WHERE title LIKE '%' || :query || '%'")
    suspend fun searchItemsByTitle(query: String): List<Item>

    // Delete an item by its ID
    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    @Query("SELECT * FROM items WHERE title LIKE '%' || :query || '%' AND userId = :userId")
    suspend fun searchItemsByTitleAndUserId(query: String, userId: Int): List<Item>

}
