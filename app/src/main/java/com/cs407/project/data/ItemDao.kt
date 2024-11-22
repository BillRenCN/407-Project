package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): Item?
}
