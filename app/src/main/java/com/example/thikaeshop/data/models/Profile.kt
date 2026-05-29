package com.example.thikaeshop.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserProfile(
    @SerialName("id")
    val userId: String = "",

    @SerialName("name")
    val name: String = "",

    @SerialName("email")
    val email: String = "",

    @SerialName("phone")
    val phoneNumber: String = "",

    @SerialName("student_id")
    val studentId: String = "",

    @SerialName("is_verified")
    val isVerified: Boolean = false,


    @SerialName("profile_image_url")
    val profileImageUrl: String = "",

    @SerialName("rating")
    val rating: Double = 0.0,

    @SerialName("total_orders")
    val totalOrders: Int = 0,

    @SerialName("total_spent")
    val totalSpent: Int = 0
)

// These can stay as is since they're not going to Supabase
data class ListingItem(
    val id: String,
    val title: String,
    val price: String,
    val icon: String,
    val status: String
)

data class LandmarkItem(
    val id: String,
    val area: String,
    val description: String,
    val timesUsed: String
)

