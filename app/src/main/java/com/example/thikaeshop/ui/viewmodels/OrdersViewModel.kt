package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OrderModel(
    val id: String,
    val orderNumber: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val totalAmount: Int,
    val status: String,
    val deliveryLocation: String,
    val landmark: String,
    val createdAt: String
)

sealed class OrdersUiState {
    object Loading : OrdersUiState()
    data class Success(val orders: List<OrderModel>) : OrdersUiState()
    data class Error(val message: String) : OrdersUiState()
}

class OrdersViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = OrdersUiState.Loading
            try {
                val result = repository.getMyOrders()
                val orders = result.map { order ->
                    OrderModel(
                        id = order["id"] as String,
                        orderNumber = order["order_number"] as String,
                        productName = order["product_name"] as String,
                        productImageUrl = order["product_image_url"] as String? ?: "",
                        quantity = (order["quantity"] as Number).toInt(),
                        totalAmount = (order["total_amount"] as Number).toInt(),
                        status = order["status"] as String,
                        deliveryLocation = order["delivery_location"] as String,
                        landmark = order["landmark"] as String,
                        createdAt = order["created_at"]?.toString()?.take(10) ?: ""
                    )
                }
                _uiState.value = OrdersUiState.Success(orders)
            } catch (e: Exception) {
                _uiState.value = OrdersUiState.Error(e.message ?: "Failed to load orders")
            }
        }
    }
}