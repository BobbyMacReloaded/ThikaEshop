package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.DeliveryLocation
import com.example.thikaeshop.data.models.OrderItem
import com.example.thikaeshop.data.models.OrderStatus
import com.example.thikaeshop.data.models.OrderTracking
import com.example.thikaeshop.data.models.OrderTrackingData
import com.example.thikaeshop.data.models.RiderInfo
import com.example.thikaeshop.data.models.StatusUpdate
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI state ──────────────────────────────────────────────────────────────────
sealed class OrderTrackingUiState {
    object Loading : OrderTrackingUiState()
    data class Success(val tracking: OrderTracking) : OrderTrackingUiState()
    data class Error(val message: String) : OrderTrackingUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class OrderTrackingViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<OrderTrackingUiState>(OrderTrackingUiState.Loading)
    val uiState: StateFlow<OrderTrackingUiState> = _uiState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.value = OrderTrackingUiState.Loading
            try {
                val data = repository.getOrderById(orderId)
                if (data != null) {
                    _uiState.value = OrderTrackingUiState.Success(data.toOrderTracking())
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
            try {
                repository.updateOrderStatus(orderId, newStatus)
                // Reload so the UI reflects the change
                loadOrder(orderId)
            } catch (_: Exception) { /* optionally surface error */ }
        }
    }
}

// ── Mapping helper ─────────────────────────────────────────────────────────────
/**
 * Converts an [OrderTrackingData] (Supabase row) into the existing [OrderTracking] UI model.
 *
 * statusHistory is reconstructed from the current status – all statuses up to and
 * including the current one are marked as complete.  You can later persist real
 * history in a separate `order_status_history` table and join it here.
 */
private fun OrderTrackingData.toOrderTracking(): OrderTracking {

    val currentStatus = status.toOrderStatus()

    // Build a progressive history up to the current step
    val allSteps = listOf(
        OrderStatus.PROCESSING       to "Order confirmed and being prepared",
        OrderStatus.SHIPPED          to "Item has been packed and handed to rider",
        OrderStatus.OUT_FOR_DELIVERY to "Rider is on the way to you",
        OrderStatus.DELIVERED        to "Order delivered successfully"
    )

    val statusHistory: List<StatusUpdate> = buildList {
        val now = System.currentTimeMillis()
        for ((step, message) in allSteps) {
            add(
                StatusUpdate(
                    status    = step,
                    timestamp = now,          // real timestamps need a history table
                    message   = message
                )
            )
            if (step == currentStatus) break  // stop at current status
        }
    }

    val deliveryLocation = DeliveryLocation(
        area        = deliveryLocation,
        landmark    = landmark,
        coordinates = null                    // extend later if you store lat/lng
    )

    val riderInfo: RiderInfo? = if (riderName != null) {
        RiderInfo(
            name            = riderName,
            phone           = riderPhone ?: "",
            rating          = riderRating ?: 0.0,
            currentLocation = riderLocation ?: "En route"
        )
    } else null

    val estimatedDeliveryTime = when (currentStatus) {
        OrderStatus.PROCESSING       -> "30-60 minutes"
        OrderStatus.SHIPPED          -> "1-2 hours"
        OrderStatus.OUT_FOR_DELIVERY -> "15-30 minutes"
        OrderStatus.DELIVERED        -> "Delivered"
        OrderStatus.CANCELLED        -> "Cancelled"
    }

    val item = OrderItem(
        id       = productId,
        name     = productName,
        quantity = quantity,
        price    = totalAmount,
        icon     = "📦",

        imageUrl = productImageUrl
    )

    return OrderTracking(
        orderId               = id,
        status                = currentStatus,
        statusHistory         = statusHistory,
        deliveryLocation      = deliveryLocation,
        riderInfo             = riderInfo,
        estimatedDeliveryTime = estimatedDeliveryTime,
        items                 = listOf(item)
    )
}

/** Maps a Supabase status string to the [OrderStatus] enum. */
private fun String.toOrderStatus(): OrderStatus = when (lowercase()) {
    "processing"       -> OrderStatus.PROCESSING
    "shipped"          -> OrderStatus.SHIPPED
    "out_for_delivery" -> OrderStatus.OUT_FOR_DELIVERY
    "delivered"        -> OrderStatus.DELIVERED
    "cancelled"        -> OrderStatus.CANCELLED
    else               -> OrderStatus.PROCESSING
}