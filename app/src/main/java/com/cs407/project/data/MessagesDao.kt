package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// For some reason if the entity is separate from the DAO it will fail to compile sometimes
@Entity(tableName = "messages", primaryKeys = ["senderId", "receiverId", "timestamp"])
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
    @Query("SELECT * FROM messages WHERE (senderId = :user1Id AND receiverId = :user2Id) OR (senderId = :user2Id AND receiverId = :user1Id)")
    suspend fun getMessagesByParticipants(user1Id: Int, user2Id: Int): List<Message>

    // Get a list of all conversations involving a user
    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId")
    suspend fun getConversationsForUser(userId: Int): List<Message>

    // No deleting is intentional
}
