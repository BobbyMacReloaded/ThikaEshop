package com.example.thikaeshop.data.models


data class RatingReview(
    val orderId: String,
    val productId: String,
    val productName: String,
    val productIcon: String,
    val rating: Int = 0,
    val review: String = "",
    val sellerRating: Int = 0
)

data class SubmittedRating(
    val id: String,
    val userId: String,
    val orderId: String,
    val productId: String,
    val productRating: Int,
    val sellerRating: Int,
    val review: String,
    val timestamp: Long
)