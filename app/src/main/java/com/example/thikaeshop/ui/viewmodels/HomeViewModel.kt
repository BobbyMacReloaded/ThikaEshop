package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Product  // ← Make sure this import exists
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val products: List<Product>,           // ← Must be List<Product>
        val featuredProducts: List<Product>    // ← Must be List<Product>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val products = repository.getAllProducts()
                _uiState.value = HomeUiState.Success(
                    products = products,
                    featuredProducts = products.take(4)
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load products")
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val results = if (query.isEmpty()) {
                    repository.getAllProducts()
                } else {
                    repository.searchProducts(query)
                }
                _uiState.value = HomeUiState.Success(
                    products = results,
                    featuredProducts = results.take(4)
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val filtered = when (category) {
                    "SecondHand" -> repository.getSecondHandProducts()
                    "Textbooks", "Electronics", "Fashion", "Household", "Food" ->
                        repository.getProductsByCategory(category)
                    else -> repository.getAllProducts()
                }
                _uiState.value = HomeUiState.Success(
                    products = filtered,
                    featuredProducts = filtered.take(4)
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Filter failed")
            }
        }
    }
}