package com.example.thikaeshop.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("seller_id")
    val sellerId: String? = null,

    @SerialName("seller_name")
    val sellerName: String? = null,

    @SerialName("seller_phone")
    val sellerPhone: String? = null,

    @SerialName("seller_rating")
    val sellerRating: Double = 0.0,

    @SerialName("is_second_hand")
    val isSecondHand: Boolean = false,

    val category: String = "",
    val location: String = "",
    val landmark: String = "",
    val condition: String = "",

    @SerialName("created_at")
    val createdAt: String = "",

    @SerialName("is_available")
    val isAvailable: Boolean = true,

    @SerialName("is_featured")
    val isFeatured: Boolean = false
)