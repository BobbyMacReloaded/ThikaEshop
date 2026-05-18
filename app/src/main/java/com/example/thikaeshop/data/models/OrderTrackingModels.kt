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
    val icon: String
)

// Sample data for demo
object SampleOrderTracking {
    fun getSampleOrder(orderId: String): OrderTracking {
        return OrderTracking(
            orderId = orderId,
            status = OrderStatus.OUT_FOR_DELIVERY,
            statusHistory = listOf(
                StatusUpdate(OrderStatus.PROCESSING, System.currentTimeMillis() - 3600000, "Order confirmed"),
                StatusUpdate(OrderStatus.SHIPPED, System.currentTimeMillis() - 1800000, "Item packed and ready"),
                StatusUpdate(OrderStatus.OUT_FOR_DELIVERY, System.currentTimeMillis(), "Rider on the way"),
                StatusUpdate(OrderStatus.DELIVERED, System.currentTimeMillis() + 1800000, "Delivery completed")
            ),
            deliveryLocation = DeliveryLocation(
                area = "Landless, Thika",
                landmark = "Near Blue Water Tank, opposite the church"
            ),
            riderInfo = RiderInfo(
                name = "James Mwangi",
                phone = "+254712345678",
                rating = 4.8,
                currentLocation = "2 km away - Near Thika Town"
            ),
            estimatedDeliveryTime = "15-20 minutes",
            items = listOf(
                OrderItem("1", "Programming Textbook", 1, 450, "📚")
            )
        )
    }
}