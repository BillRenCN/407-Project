package com.cs407.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {
    // Insert a user into the database
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Get a single item by its ID
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User

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

}