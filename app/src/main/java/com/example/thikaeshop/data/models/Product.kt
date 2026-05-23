package com.example.thikaeshop.data.models

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    val imageUrl: String = "",  // Firebase Storage URL
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val sellerRating: Double = 0.0,
    val isSecondHand: Boolean = false,
    val category: String = "",
    val location: String = "",
    val landmark: String = "",
    val condition: String = "", // New, Like New, Good, Fair
    val createdAt: Timestamp = Timestamp.now(),
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false  // For admin promoted products
)