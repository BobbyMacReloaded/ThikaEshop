package com.example.thikaeshop.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Subscription
import com.example.thikaeshop.data.models.SubscriptionTier
import com.example.thikaeshop.data.repository.SubscriptionRepository
import com.example.thikaeshop.network.PaymentApiClient
import com.example.thikaeshop.network.PaymentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    data class Loaded(val subscription: Subscription) : SubscriptionUiState()
    object ProcessingPayment : SubscriptionUiState()
    data class PaymentSuccess(val subscription: Subscription) : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
}

class SubscriptionViewModel : ViewModel() {

    private val repository = SubscriptionRepository()

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState

    private var selectedTier: SubscriptionTier? = null

    val currentTier: SubscriptionTier
        get() = (_uiState.value as? SubscriptionUiState.Loaded)?.subscription?.tierEnum ?: SubscriptionTier.FREE

    init {
        loadSubscription()
    }

    fun loadSubscription() {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            try {
                val sub = repository.getMySubscription()
                _uiState.value = SubscriptionUiState.Loaded(sub)
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error(e.message ?: "Failed to load subscription")
            }
        }
    }

    fun startSubscriptionPayment(tier: SubscriptionTier, phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.ProcessingPayment
            selectedTier = tier

            try {
                val orderId = "SUB-${System.currentTimeMillis()}-${tier.toDbValue()}"

                Log.d("SubscriptionVM", "Starting subscription payment for: $orderId")
                Log.d("SubscriptionVM", "Phone: $phoneNumber, Amount: ${tier.priceKsh}")

                val response = PaymentApiClient.instance.initiatePayment(
                    PaymentRequest(
                        phone = phoneNumber,
                        amount = tier.priceKsh,
                        orderId = orderId
                    )
                )

                Log.d("SubscriptionVM", "Payment response: $response")

                if (response.always_active == true || response.success == true) {
                    Log.d("SubscriptionVM", "✅ Always Active mode: activating subscription immediately")
                    confirmSubscription(tier, response.tracking_id ?: "DEMO_${System.currentTimeMillis()}")
                } else if (response.tracking_id != null) {
                    Log.d("SubscriptionVM", "⏳ Real payment - polling...")
                    checkPaymentStatus(orderId)
                } else {
                    _uiState.value = SubscriptionUiState.Error(response.message ?: "Payment initiation failed")
                }
            } catch (e: Exception) {
                Log.e("SubscriptionVM", "Payment error: ${e.message}", e)
                _uiState.value = SubscriptionUiState.Error(e.message ?: "Payment failed")
            }
        }
    }

    fun checkPaymentStatus(orderId: String) {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30

            while (attempts < maxAttempts) {
                delay(2000)
                attempts++

                try {
                    val statusResponse = PaymentApiClient.instance.getPaymentStatus(orderId)
                    Log.d("SubscriptionVM", "Status check #$attempts: ${statusResponse.status}")

                    when (statusResponse.status) {
                        "success" -> {
                            Log.d("SubscriptionVM", "✅ Payment successful!")
                            val tier = selectedTier ?: SubscriptionTier.PRO
                            confirmSubscription(tier, orderId)
                            return@launch
                        }
                        "failed" -> {
                            if (statusResponse.always_active == true) {
                                Log.d("SubscriptionVM", "🔓 Always active: activating despite payment failure")
                                val tier = selectedTier ?: SubscriptionTier.PRO
                                confirmSubscription(tier, orderId)
                            } else {
                                _uiState.value = SubscriptionUiState.Error("Payment failed")
                            }
                            return@launch
                        }
                        else -> {
                            if (attempts >= maxAttempts) {
                                if (selectedTier != null) {
                                    Log.d("SubscriptionVM", "⏰ Timeout - activating anyway (demo mode)")
                                    confirmSubscription(selectedTier!!, orderId)
                                } else {
                                    _uiState.value = SubscriptionUiState.Error("Payment timeout. Please check your M-Pesa.")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SubscriptionVM", "Status check error: ${e.message}")
                }
            }
        }
    }

    fun confirmSubscription(tier: SubscriptionTier, paymentRef: String) {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.ProcessingPayment
            try {
                val sub = repository.activateSubscription(
                    tier = tier,
                    paymentRef = paymentRef,
                    amountPaid = tier.priceKsh
                )
                _uiState.value = SubscriptionUiState.PaymentSuccess(sub)
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error(e.message ?: "Failed to activate subscription")
            }
        }
    }

    fun activateTestSubscription(tier: SubscriptionTier) {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.ProcessingPayment
            try {
                val sub = repository.activateSubscription(
                    tier = tier,
                    paymentRef = "SIM_${System.currentTimeMillis()}",
                    amountPaid = tier.priceKsh
                )
                delay(1000)
                _uiState.value = SubscriptionUiState.PaymentSuccess(sub)
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error(e.message ?: "Failed to activate subscription")
            }
        }
    }

    fun recordSearch(term: String, university: String) {
        viewModelScope.launch {
            repository.recordSearchSignal(term, university)
        }
    }

    fun boostListing(productId: String) {
        viewModelScope.launch {
            repository.boostListing(productId)
        }
    }
}