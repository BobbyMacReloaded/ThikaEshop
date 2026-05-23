package com.example.thikaeshop.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SellUiState {
    object Idle : SellUiState()
    object Loading : SellUiState()
    object Success : SellUiState()
    data class Error(val message: String) : SellUiState()
}

class SellViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<SellUiState>(SellUiState.Idle)
    val uiState: StateFlow<SellUiState> = _uiState

    fun uploadProduct(
        title: String,
        description: String,
        price: Int,
        category: String,
        isSecondHand: Boolean,
        condition: String,
        location: String,
        landmark: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            _uiState.value = SellUiState.Loading
            try {
                repository.saveProduct(
                    title = title,
                    description = description,
                    price = price,
                    category = category,
                    isSecondHand = isSecondHand,
                    condition = condition,
                    location = location,
                    landmark = landmark,
                    imageUri = imageUri
                )
                _uiState.value = SellUiState.Success
            } catch (e: Exception) {
                _uiState.value = SellUiState.Error(e.message ?: "Failed to upload product")
            }
        }
    }
}