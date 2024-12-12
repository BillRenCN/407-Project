package com.cs407.project

data class Review(
    val user: String,
    val rating:Float,
    val reviewer: String,
    val date: String,
    val message: String,
    val iconResource: Int
)