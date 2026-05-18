package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Products
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val products: List<Products>, val featuredProducts: List<Products>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Create a list of sample products
    private val allProducts = listOf(
        Products(
            id = "1",
            title = "Programming Textbook - Kotlin",
            price = 450,
            originalPrice = 1200,
            description = "Like new condition. No highlights or marks. Perfect for BIT students.",
            icon = "📚",
            sellerId = "seller_001",
            sellerName = "John Kimani",
            sellerIcon = "👨‍🎓",
            sellerRating = 4.8,
            sellerReviews = 23,
            location = "Landless, Thika",
            landmark = "Near Blue Water Tank",
            condition = "Like New",
            isSecondHand = true
        ),
        Products(
            id = "2",
            title = "Samsung Galaxy A14",
            price = 12000,
            originalPrice = 22000,
            description = "6 months old, original box included, perfect condition",
            icon = "📱",
            sellerId = "seller_002",
            sellerName = "Mary Wanjiku",
            sellerIcon = "👩‍🎓",
            sellerRating = 4.9,
            sellerReviews = 45,
            location = "Kiganjo, Thika",
            landmark = "Opposite Church",
            condition = "Like New",
            isSecondHand = true
        ),
        Products(
            id = "3",
            title = "Fresh Vegetables",
            price = 250,
            originalPrice = null,
            description = "Fresh from local farm, delivered daily",
            icon = "🥬",
            sellerId = "seller_003",
            sellerName = "Local Vendor",
            sellerIcon = "🛒",
            sellerRating = 4.5,
            sellerReviews = 12,
            location = "Kiandutu, Thika",
            landmark = "Next to Shop",
            condition = null,
            isSecondHand = false
        ),
        Products(
            id = "4",
            title = "Bed Sheet Set",
            price = 1800,
            originalPrice = 3000,
            description = "Single size, used for 3 months, still in good condition",
            icon = "🛏️",
            sellerId = "seller_004",
            sellerName = "Peter Otieno",
            sellerIcon = "👨‍🎓",
            sellerRating = 4.3,
            sellerReviews = 8,
            location = "Landless, Thika",
            landmark = "Behind Hostel",
            condition = "Good",
            isSecondHand = true
        ),
        Products(
            id = "5",
            title = "Scientific Calculator",
            price = 800,
            originalPrice = 2500,
            description = "Perfect for engineering students, works perfectly",
            icon = "📟",
            sellerId = "seller_005",
            sellerName = "James Mwangi",
            sellerIcon = "👨‍🎓",
            sellerRating = 4.7,
            sellerReviews = 34,
            location = "Kiganjo, Thika",
            landmark = "Near School Gate",
            condition = "Good",
            isSecondHand = true
        )
    )

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            delay(500) // Simulate network call
            _uiState.value = HomeUiState.Success(
                products = allProducts,
                featuredProducts = allProducts.take(4)
            )
        }
    }

    fun searchProducts(query: String) {
        if (query.isEmpty()) {
            _uiState.value = HomeUiState.Success(
                products = allProducts,
                featuredProducts = allProducts.take(4)
            )
        } else {
            val filtered = allProducts.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            _uiState.value = HomeUiState.Success(
                products = filtered,
                featuredProducts = filtered.take(4)
            )
        }
    }

    fun filterByCategory(category: String) {
        val filtered = when (category) {
            "SecondHand" -> allProducts.filter { it.isSecondHand }
            "New" -> allProducts.filter { !it.isSecondHand }
            else -> allProducts
        }
        _uiState.value = HomeUiState.Success(
            products = filtered,
            featuredProducts = filtered.take(4)
        )
    }
}