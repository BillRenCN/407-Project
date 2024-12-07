package com.cs407.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int=0,
    val username: String,
    val password: String,
    val email: String,
    val sales: Int,
    val rating: Double,
    val date: Long,
    val description: String
)

