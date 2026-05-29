package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

class CheckoutViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState

    fun placeOrder(
        productId: String,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        quantity: Int,
        totalAmount: Int,
        sellerId: String,
        deliveryLocation: String,
        landmark: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            Log.d("CheckoutVM", "placeOrder called")
            _uiState.value = CheckoutUiState.Loading
            try {
                Log.d("CheckoutVM", "Calling repository.saveOrder...")
                val orderId = repository.saveOrder(
                    productId = productId,
                    productName = productName,
                    productImageUrl = productImageUrl,
                    quantity = quantity,
                    totalAmount = totalAmount,
                    sellerId = sellerId,
                    deliveryLocation = deliveryLocation,
                    landmark = landmark,
                    paymentMethod = paymentMethod
                )
                Log.d("CheckoutVM", "saveOrder succeeded, orderId: $orderId")
                _uiState.value = CheckoutUiState.Success
            } catch (e: Exception) {
                Log.e("CheckoutVM", "saveOrder failed: ${e.message}", e)
                _uiState.value = CheckoutUiState.Error(e.message ?: "Order failed")
            }
        }
    }
}