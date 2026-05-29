package com.example.thikaeshop.data.models


enum class OrderStatus {
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

data class OrderTracking(
    val orderId: String,
    val status: OrderStatus,
    val statusHistory: List<StatusUpdate>,
    val deliveryLocation: DeliveryLocation,
    val riderInfo: RiderInfo? = null,
    val estimatedDeliveryTime: String,
    val items: List<OrderItem>
)

data class StatusUpdate(
    val status: OrderStatus,
    val timestamp: Long,
    val message: String
)

data class DeliveryLocation(
    val area: String,
    val landmark: String,
    val coordinates: Pair<Double, Double>? = null
)

data class RiderInfo(
    val name: String,
    val phone: String,
    val rating: Double,
    val currentLocation: String
)

data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Int,
    val icon: String,
    val imageUrl: String?
) {

}

