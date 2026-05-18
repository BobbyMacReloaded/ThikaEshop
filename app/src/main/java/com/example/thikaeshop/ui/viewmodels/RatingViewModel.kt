package com.example.thikaeshop.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RatingUiState {
    object Idle : RatingUiState()
    object Loading : RatingUiState()
    object Success : RatingUiState()
    data class Error(val message: String) : RatingUiState()
}

class RatingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RatingUiState>(RatingUiState.Idle)
    val uiState: StateFlow<RatingUiState> = _uiState

    fun submitRating(
        orderId: String,
        productRating: Int,
        sellerRating: Int,
        review: String
    ) {
        viewModelScope.launch {
            _uiState.value = RatingUiState.Loading
            delay(1500) // Simulate API call

            if (productRating == 0) {
                _uiState.value = RatingUiState.Error("Please rate the product")
                return@launch
            }

            // Success
            _uiState.value = RatingUiState.Success
        }
    }
}