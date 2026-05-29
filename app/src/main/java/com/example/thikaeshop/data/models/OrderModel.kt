package com.example.thikaeshop.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────
// 1. INSERT – used only when saving a new order to Supabase.
//    No `created_at` / `updated_at` – Postgres sets them automatically.
//    No Firebase Timestamp anywhere.
// ─────────────────────────────────────────────────────────────
@Serializable
data class OrderInsert(
    val id: String,
    @SerialName("order_number")  val orderNumber: String,
    @SerialName("buyer_id")      val buyerId: String,
    @SerialName("seller_id")     val sellerId: String,
    @SerialName("product_id")    val productId: String,
    @SerialName("product_name")  val productName: String,
    @SerialName("product_image_url") val productImageUrl: String,
    val quantity: Int,
    @SerialName("total_amount")  val totalAmount: Int,
    val status: String = "processing",
    @SerialName("delivery_location") val deliveryLocation: String,
    val landmark: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("is_escrow_held") val isEscrowHeld: Boolean = true
)

// ─────────────────────────────────────────────────────────────
// 2. DISPLAY – used when reading back orders for the list screen.
//    Maps directly from the `orders` table columns.
//    `createdAt` is nullable because Supabase returns ISO-8601 strings.
// ─────────────────────────────────────────────────────────────
@Serializable
data class OrderDisplay(
    val id: String,
    @SerialName("order_number")  val orderNumber: String,
    @SerialName("buyer_id")      val buyerId: String,
    @SerialName("seller_id")     val sellerId: String,
    @SerialName("product_id")    val productId: String,
    @SerialName("product_name")  val productName: String,
    @SerialName("product_image_url") val productImageUrl: String = "",
    val quantity: Int,
    @SerialName("total_amount")  val totalAmount: Int,
    val status: String,
    @SerialName("delivery_location") val deliveryLocation: String = "",
    val landmark: String = "",
    @SerialName("payment_method") val paymentMethod: String = "",
    @SerialName("is_escrow_held") val isEscrowHeld: Boolean = false,
    // Supabase returns timestamps as ISO-8601 strings (e.g. "2025-01-15T10:30:00+00:00")
    @SerialName("created_at")    val createdAt: String? = null
)

// ─────────────────────────────────────────────────────────────
// 3. TRACKING DATA – full detail view fetched by order ID.
//    Same columns as OrderDisplay plus optional rider / location fields.
//    The statusHistory is built in the ViewModel (not stored in DB yet).
// ─────────────────────────────────────────────────────────────
@Serializable
data class OrderTrackingData(
    val id: String,
    @SerialName("order_number")  val orderNumber: String,
    @SerialName("buyer_id")      val buyerId: String,
    @SerialName("seller_id")     val sellerId: String,
    @SerialName("product_id")    val productId: String,
    @SerialName("product_name")  val productName: String,
    @SerialName("product_image_url") val productImageUrl: String = "",
    val quantity: Int,
    @SerialName("total_amount")  val totalAmount: Int,
    val status: String,
    @SerialName("delivery_location") val deliveryLocation: String = "",
    val landmark: String = "",
    @SerialName("payment_method") val paymentMethod: String = "",
    @SerialName("is_escrow_held") val isEscrowHeld: Boolean = false,
    @SerialName("created_at")    val createdAt: String? = null,
    @SerialName("updated_at")    val updatedAt: String? = null,
    // Optional rider fields – nullable so existing rows without them still decode fine
    @SerialName("rider_name")    val riderName: String? = null,
    @SerialName("rider_phone")   val riderPhone: String? = null,
    @SerialName("rider_rating")  val riderRating: Double? = null,
    @SerialName("rider_location") val riderLocation: String? = null
)