package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

// For some reason if the entity is separate from the DAO it will fail to compile sometimes
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int=0,
    val username: String,
    val password: String,
    val email: String,
    val sales: Int,
    val rating: Double,
    val date: Long,
    val description: String,
    val imageUrl: String? = null
)

@Dao
interface UserDao {
    // Insert a user into the database
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Get a single item by its ID
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserFromUsername(username: String): User?

    @Query("SELECT * FROM users WHERE userId=:id")
    suspend fun getById(id: Int): User

    @Query("DELETE FROM users WHERE userId=:userId")
    suspend fun deleteUser(userId: Int)

    // Check if a user exists by username
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username LIMIT 1)")
    suspend fun userExistsByUsername(username: String): Boolean

    // Check if a user exists by email
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email LIMIT 1)")
    suspend fun userExistsByEmail(email: String): Boolean

    @Query("SELECT password FROM users WHERE email = :email LIMIT 1")
    suspend fun getPasswordHashByEmail(email: String): String?

    @Query("SELECT password FROM users WHERE username = :username LIMIT 1")
    suspend fun getPasswordHashByUsername(username: String): String?

    @Query("SELECT date FROM users WHERE username = :username LIMIT 1")
    suspend fun getDateByUsername(username: String): Long

    @Query("SELECT userId FROM users WHERE username = :username LIMIT 1")
    suspend fun getIdByUsername(username: String): Int

    @Query("UPDATE users SET password = :newPassword WHERE username = :username")
    suspend fun updatePasswordByUsername(username: String, newPassword: String): Int

    @Query("UPDATE users SET username = :newUsername WHERE username = :currentUsername")
    suspend fun updateUsername(currentUsername: String, newUsername: String): Int

    @Query("SELECT imageUrl FROM users WHERE username = :username LIMIT 1")
    suspend fun getImageUrlByUsername(username: String): String?

    @Query("SELECT imageUrl FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getImageUrlByUserId(userId: Int): String?

    @Query("UPDATE users SET imageUrl = :newImageUrl WHERE username = :username")
    suspend fun updateImageUrlByUsername(username: String, newImageUrl: String): Int

    @Query("UPDATE users SET imageUrl = :newImageUrl WHERE userId = :userId")
    suspend fun updateImageUrlByUserId(userId: Int, newImageUrl: String): Int
}