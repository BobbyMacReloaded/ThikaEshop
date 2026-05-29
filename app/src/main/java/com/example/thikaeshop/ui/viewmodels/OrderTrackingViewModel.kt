package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.OrderStatus
import com.example.thikaeshop.data.models.OrderTracking
import com.example.thikaeshop.data.models.DeliveryLocation
import com.example.thikaeshop.data.models.OrderItem
import com.example.thikaeshop.data.models.StatusUpdate
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrderTrackingUiState {
    object Loading : OrderTrackingUiState()
    data class Success(val tracking: OrderTracking) : OrderTrackingUiState()
    data class Error(val message: String) : OrderTrackingUiState()
}

class OrderTrackingViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _uiState = MutableStateFlow<OrderTrackingUiState>(OrderTrackingUiState.Loading)
    val uiState: StateFlow<OrderTrackingUiState> = _uiState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.value = OrderTrackingUiState.Loading
            try {
                val orderMap = repository.getOrderById(orderId)
                if (orderMap != null) {
                    val tracking = mapToOrderTracking(orderMap)
                    _uiState.value = OrderTrackingUiState.Success(tracking)
                } else {
                    _uiState.value = OrderTrackingUiState.Error("Order not found")
                }
            } catch (e: Exception) {
                _uiState.value = OrderTrackingUiState.Error(e.message ?: "Failed to load order")
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    private fun mapToOrderTracking(map: Map<String, Any>): OrderTracking {
        val orderId = map["id"] as String
        val statusStr = map["status"] as String
        val orderStatus = when (statusStr.lowercase()) {
            "processing" -> OrderStatus.PROCESSING
            "shipped" -> OrderStatus.SHIPPED
            "out_for_delivery" -> OrderStatus.OUT_FOR_DELIVERY
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PROCESSING
        }

        // Build status history (simplified)
        val statusHistory = listOf(
            StatusUpdate(OrderStatus.PROCESSING, 0, "Order placed"),
            StatusUpdate(orderStatus, 0, "Current status: ${statusStr.replace("_", " ")}")
        )

        val deliveryLocation = DeliveryLocation(
            area = map["delivery_location"] as? String ?: "",
            landmark = map["landmark"] as? String ?: "",
            coordinates = null
        )

        // Rider info (placeholder – you can fetch from another table later)
        val riderInfo = null

        val estimatedTime = when (orderStatus) {
            OrderStatus.PROCESSING -> "Processing"
            OrderStatus.SHIPPED -> "1-2 days"
            OrderStatus.OUT_FOR_DELIVERY -> "30-45 minutes"
            OrderStatus.DELIVERED -> "Delivered"
            else -> "Pending"
        }

        val orderItem = OrderItem(
            id = map["product_id"] as? String ?: "",
            name = map["product_name"] as? String ?: "",
            quantity = (map["quantity"] as? Number)?.toInt() ?: 1,
            price = (map["total_amount"] as? Number)?.toInt() ?: 0,
            icon = "📦"
        )

        return OrderTracking(
            orderId = orderId,
            status = orderStatus,
            statusHistory = statusHistory,
            deliveryLocation = deliveryLocation,
            riderInfo = riderInfo,
            estimatedDeliveryTime = estimatedTime,
            items = listOf(orderItem)
        )
    }
}