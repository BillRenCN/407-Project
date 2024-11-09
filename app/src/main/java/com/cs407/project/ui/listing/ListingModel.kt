package com.cs407.project.ui.listing

data class ListingModel(
    val name: String = "Unnamed item",
    val description: String = "No description",
    val user: Int = 0,
    val imageId: Int? = null,
)