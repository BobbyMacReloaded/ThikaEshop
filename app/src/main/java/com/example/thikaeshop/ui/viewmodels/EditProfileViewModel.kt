package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EditProfileUiState {
    object Idle : EditProfileUiState()
    object Loading : EditProfileUiState()
    data class Success(val message: String) : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState

    fun updateProfile(
        name: String,
        email: String,
        phoneNumber: String,
        studentId: String
    ) {
        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            delay(1500) // Simulate API call

            if (name.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
                _uiState.value = EditProfileUiState.Error("Please fill all required fields")
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.value = EditProfileUiState.Error("Please enter a valid email address")
                return@launch
            }

            // Success - profile updated
            _uiState.value = EditProfileUiState.Success("Profile updated successfully!")
        }
    }
}