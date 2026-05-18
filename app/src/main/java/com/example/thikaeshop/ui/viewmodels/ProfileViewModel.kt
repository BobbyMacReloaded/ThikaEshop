package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.ListingItem
import com.example.thikaeshop.data.models.LandmarkItem
import com.example.thikaeshop.data.models.SampleProfileData
import com.example.thikaeshop.data.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val userProfile: UserProfile,
        val myListings: List<ListingItem>,
        val savedLandmarks: List<LandmarkItem>
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            delay(500) // Simulate network call
            _uiState.value = ProfileUiState.Success(
                userProfile = SampleProfileData.userProfile,
                myListings = SampleProfileData.myListings,
                savedLandmarks = SampleProfileData.savedLandmarks
            )
        }
    }

    fun logout() {
        // Clear user session
        // Will be implemented when Firebase is added
    }

    fun deleteListing(listingId: String) {
        // Delete listing from database
    }

    fun deleteLandmark(landmarkId: String) {
        // Delete saved landmark
    }
}