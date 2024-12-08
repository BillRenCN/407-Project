package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// For some reason if the entity is separate from the DAO it will fail to compile sometimes
@Entity(tableName = "messages", primaryKeys = ["senderId", "receiverId"])
data class Message(
    val senderId: Int,
    val receiverId: Int,
    val message: String,
    val timestamp: Long
)

@Dao
interface MessagesDao {
    // Insert an message into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(message: Message): Long

    // Get all messages by its composite userId keys
    @Query("SELECT * FROM messages WHERE senderId = :senderId AND receiverId = :receiverId")
    suspend fun getMessagesBySenderAndReceiver(senderId: Int, receiverId: Int): List<Message>

    // No deleting is intentional
}
