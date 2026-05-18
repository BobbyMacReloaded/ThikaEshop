package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.OrderTracking
import com.example.thikaeshop.data.models.SampleOrderTracking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrderTrackingUiState {
    object Loading : OrderTrackingUiState()
    data class Success(val tracking: OrderTracking) : OrderTrackingUiState()
    data class Error(val message: String) : OrderTrackingUiState()
}

class OrderTrackingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OrderTrackingUiState>(OrderTrackingUiState.Loading)
    val uiState: StateFlow<OrderTrackingUiState> = _uiState

    fun loadOrderTracking(orderId: String) {
        viewModelScope.launch {
            _uiState.value = OrderTrackingUiState.Loading
            delay(800) // Simulate network call
            try {
                val tracking = SampleOrderTracking.getSampleOrder(orderId)
                _uiState.value = OrderTrackingUiState.Success(tracking)
            } catch (e: Exception) {
                _uiState.value = OrderTrackingUiState.Error(e.message ?: "Failed to load order")
            }
        }
    }
}