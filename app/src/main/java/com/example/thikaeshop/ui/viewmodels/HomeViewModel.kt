package com.example.thikaeshop.ui.viewmodels

import android.util.Log
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
        loadRecommendations()
    }
    private val _recommendations = MutableStateFlow<List<Product>>(emptyList())
    val recommendations: StateFlow<List<Product>> = _recommendations

    fun loadRecommendations() {
        viewModelScope.launch {
            try {
                val userId = repository.currentUserId
                val recs = repository.getRecommendationsForUser(userId, 6)
                _recommendations.value = recs
            } catch (e: Exception) {
                Log.e("HomeVM", "Failed to load recommendations: ${e.message}")
            }
        }
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
    // =========================================================
// DSA SORTING FUNCTIONS
// =========================================================

    /**
     * Quick Sort for products by price
     * Delegates to repository's implementation
     */
    fun quickSortByPrice(products: List<Product>, ascending: Boolean): List<Product> {
        return repository.quickSortByPrice(products, ascending)
    }

    /**
     * Merge Sort for products by rating
     * Delegates to repository's implementation
     */
    fun mergeSortByRating(products: List<Product>, ascending: Boolean): List<Product> {
        return repository.mergeSortByRating(products, ascending)
    }

    /**
     * Binary Search for product by ID
     */
    fun binarySearchProduct(productId: String): Product? {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            return repository.binarySearchById(currentState.products, productId)
        }
        return null
    }
    fun clearData() {
        _uiState.value = HomeUiState.Loading
        _recommendations.value = emptyList()
    }
}