package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Get all products and find by ID (since Supabase SDK doesn't have get by ID directly)
                val allProducts = repository.getAllProducts()
                val foundProduct = allProducts.find { it.id == productId }
                _product.value = foundProduct
                if (foundProduct == null) {
                    _error.value = "Product not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load product"
            } finally {
                _isLoading.value = false
            }
        }
    }
}