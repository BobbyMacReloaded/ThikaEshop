package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EditListingUiState {
    object Idle : EditListingUiState()
    object Loading : EditListingUiState()
    object Success : EditListingUiState()
    data class Error(val message: String) : EditListingUiState()
}

class EditListingViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<EditListingUiState>(EditListingUiState.Idle)
    val uiState: StateFlow<EditListingUiState> = _uiState

    fun updateListing(
        listingId: String,
        title: String,
        description: String,
        price: Int,
        category: String,
        location: String,
        landmark: String
    ) {
        viewModelScope.launch {
            _uiState.value = EditListingUiState.Loading
            try {
                repository.updateProduct(
                    productId = listingId,
                    title = title,
                    description = description,
                    price = price,
                    category = category,
                    location = location,
                    landmark = landmark
                )
                _uiState.value = EditListingUiState.Success
            } catch (e: Exception) {
                _uiState.value = EditListingUiState.Error(e.message ?: "Failed to update")
            }
        }
    }
}