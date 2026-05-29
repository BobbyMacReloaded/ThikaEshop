package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Order
import com.example.thikaeshop.data.models.OrderDisplay
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrdersUiState {

    object Loading : OrdersUiState()

    data class Success(
        val orders: List<Order>
    ) : OrdersUiState()

    data class Error(
        val message: String
    ) : OrdersUiState()
}

class OrdersViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState =
        MutableStateFlow<OrdersUiState>(
            OrdersUiState.Loading
        )

    val uiState: StateFlow<OrdersUiState> = _uiState

    fun loadOrders() {

        viewModelScope.launch {

            _uiState.value = OrdersUiState.Loading

            try {

                val orders = repository.getMyOrders()

                val mappedOrders = orders.map {
                    it.toOrder()
                }

                _uiState.value =
                    OrdersUiState.Success(mappedOrders)

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    OrdersUiState.Error(
                        e.message ?: "Failed to load orders"
                    )
            }
        }
    }
}


private fun OrderDisplay.toOrder(): Order {

    return Order(
        id = id,
        productName = productName,
        productIcon = "📦",
        quantity = quantity,
        totalAmount = totalAmount,
        status = status.toDisplayStatus(),
        location = deliveryLocation,
        date = createdAt?.take(10) ?: "",
        isEscrowHeld = isEscrowHeld,
        orderNumber = orderNumber
    )
}


private fun String.toDisplayStatus(): String {

    return when (lowercase()) {

        "processing" -> "Processing"

        "shipped" -> "Shipped"

        "out_for_delivery" -> "Out for Delivery"

        "delivered" -> "Delivered"

        "cancelled" -> "Cancelled"

        else -> replaceFirstChar {
            it.uppercase()
        }
    }
}