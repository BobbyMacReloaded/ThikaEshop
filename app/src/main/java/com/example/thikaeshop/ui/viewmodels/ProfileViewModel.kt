package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.models.LandmarkItem
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val userProfile: UserProfile,
        val myListings: List<Product>,
        val savedLandmarks: List<LandmarkItem>
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val currentUserId = repository.currentUserId

                // Get user profile from Supabase
                var userProfile = repository.getUserProfile(currentUserId)

                // If user doesn't exist, create default profile
                if (userProfile == null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    userProfile = UserProfile(
                        userId = currentUserId,
                        name = if (currentUserId == "guest_user") "Guest User" else "Student User",
                        email = "user@example.com",
                        phoneNumber = "+254700000000",
                        studentId = "STU/2024/001",
                        isVerified = false,
                        profileImageUrl = "",
                        rating = 0.0,
                        totalOrders = 0,
                        totalSpent = 0
                    )
                    repository.saveUserProfile(userProfile)
                }

                // Get user's products
                val allProducts = repository.getAllProducts()
                val userProducts = allProducts.filter { it.sellerId == currentUserId }

                // Calculate totals
                val totalValue = userProducts.sumOf { it.price }

                // Update profile with calculated values
                val updatedProfile = userProfile.copy(
                    totalOrders = userProfile.totalOrders,
                    totalSpent = totalValue
                )

                _uiState.value = ProfileUiState.Success(
                    userProfile = updatedProfile,
                    myListings = userProducts,
                    savedLandmarks = emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun deleteListing(listingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(listingId)
                loadProfileData()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete"
            }
        }
    }

    fun updateListing(
        listingId: String,
        title: String,
        description: String,
        price: Int,
        category: String,
        location: String,
        landmark: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
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
                loadProfileData()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update"
            }
        }
    }
}