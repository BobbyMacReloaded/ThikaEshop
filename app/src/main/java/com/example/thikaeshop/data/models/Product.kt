package com.example.thikaeshop.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

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
    val isFeatured: Boolean = false,

    // ============================================================
    // ★ ADD THESE TWO FIELDS FOR VISIBILITY BOOST
    // ============================================================
    @SerialName("visibility_boost")
    val visibilityBoost: Int = 0,

    @SerialName("boosted_until")
    val boostedUntil: String? = null
) {
    // Check if the product is currently boosted
    val isBoosted: Boolean
        get() {
            if (visibilityBoost <= 0) return false
            val until = boostedUntil ?: return false
            return try {
                Instant.parse(until).isAfter(Instant.now())
            } catch (e: Exception) {
                false
            }
        }
}