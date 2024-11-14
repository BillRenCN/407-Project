package com.cs407.project.ui.listing

data class ListingModel(
    val name: String = "Unnamed item",
    val description: String = "No description",
    val itemId: Int? = null,
    val price: Double = 0.1
)