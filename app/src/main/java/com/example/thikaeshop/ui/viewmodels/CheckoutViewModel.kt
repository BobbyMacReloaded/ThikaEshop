package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.thikaeshop.network.PaymentApiClient
import com.example.thikaeshop.network.PaymentRequest
import kotlinx.coroutines.delay

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

    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.Idle)
    val paymentStatus: StateFlow<PaymentStatus> = _paymentStatus

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


    fun checkPaymentStatus(orderId: String) {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30 // 60 seconds max (30 * 2 seconds)

            while (attempts < maxAttempts) {
                delay(2000)
                attempts++

                try {
                    val statusResponse = PaymentApiClient.instance.getPaymentStatus(orderId)
                    Log.d("CheckoutVM", "Payment status check #$attempts: ${statusResponse.status}")

                    when (statusResponse.status) {
                        "success" -> {
                            _paymentStatus.value = PaymentStatus.Success
                            return@launch
                        }
                        "failed" -> {
                            _paymentStatus.value = PaymentStatus.Error("Payment failed")
                            return@launch
                        }
                        else -> {
                            // Still pending, continue polling
                            if (attempts >= maxAttempts) {
                                _paymentStatus.value = PaymentStatus.Error("Payment timeout. Please check your M-Pesa.")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CheckoutVM", "Status check error: ${e.message}")
                    // Continue polling, don't give up on first error
                }
            }
        }
    }
    fun processMpesaPayment(phone: String, amount: Int, orderId: String) {
        viewModelScope.launch {
            _paymentStatus.value = PaymentStatus.Loading
            try {
                Log.d("Mpesa", "Initiating payment with phone: $phone, amount: $amount, orderId: $orderId")

                val response = PaymentApiClient.instance.initiatePayment(
                    PaymentRequest(phone = phone, amount = amount, orderId = orderId)
                )

                Log.d("Mpesa", "Response: $response")
                Log.d("Mpesa", "Tracking ID: ${response.tracking_id}")

                if (response.tracking_id != null) {
                    _paymentStatus.value = PaymentStatus.Success
                } else {
                    _paymentStatus.value = PaymentStatus.Error(response.message ?: "Payment failed")
                }
            } catch (e: Exception) {
                Log.e("Mpesa", "Error: ${e.message}", e)
                _paymentStatus.value = PaymentStatus.Error(e.message ?: "Network error")
            }
        }
    }

    private fun pollPaymentStatus(orderId: String, amount: Int) {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30 // 60 seconds max (30 * 2 seconds)

            while (attempts < maxAttempts && (_paymentStatus.value as? PaymentStatus.Loading) != null) {
                delay(2000)
                attempts++

                try {
                    val statusResponse = PaymentApiClient.instance.getPaymentStatus(orderId)
                    Log.d("CheckoutVM", "Payment status: ${statusResponse.status}")

                    when (statusResponse.status) {
                        "success" -> {
                            // Payment succeeded! Now save the order
                            saveOrderAfterPayment(orderId, amount)
                            return@launch
                        }
                        "failed" -> {
                            _paymentStatus.value = PaymentStatus.Error("Payment failed")
                            return@launch
                        }
                        else -> {
                            if (attempts >= maxAttempts) {
                                _paymentStatus.value = PaymentStatus.Error("Payment timeout. Please check your M-Pesa.")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CheckoutVM", "Status check error: ${e.message}")
                }
            }
        }
    }

    private fun saveOrderAfterPayment(orderId: String, amount: Int) {
        viewModelScope.launch {
            try {
                // Now save the order to Supabase
                // You need to pass all the checkout data here
                // For now, assuming you store it in a variable

                // After saving order
                _paymentStatus.value = PaymentStatus.Success
            } catch (e: Exception) {
                _paymentStatus.value = PaymentStatus.Error("Payment succeeded but order save failed: ${e.message}")
            }
        }
    }

    sealed class PaymentStatus {
        object Idle : PaymentStatus()
        object Loading : PaymentStatus()
        object Success : PaymentStatus()
        data class Error(val message: String) : PaymentStatus()
    }
    data class PendingOrder(
        val productId: String,
        val productName: String,
        val productPrice: Int,
        val productImageUrl: String,
        val quantity: Int,
        val totalAmount: Int,
        val sellerId: String,
        val deliveryLocation: String,
        val landmark: String,
        val paymentMethod: String
    )

    private var pendingOrder: PendingOrder? = null

    fun prepareOrder(
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
        pendingOrder = PendingOrder(
            productId = productId,
            productName = productName,
            productPrice = productPrice,
            productImageUrl = productImageUrl,
            quantity = quantity,
            totalAmount = totalAmount,
            sellerId = sellerId,
            deliveryLocation = deliveryLocation,
            landmark = landmark,
            paymentMethod = paymentMethod
        )
    }
}