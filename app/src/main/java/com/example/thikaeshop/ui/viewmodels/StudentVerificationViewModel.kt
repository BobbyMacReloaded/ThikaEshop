package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.StudentVerification
import com.example.thikaeshop.data.models.VerificationStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class VerificationUiState {
    object Idle : VerificationUiState()
    object Loading : VerificationUiState()
    data class Success(val verification: StudentVerification) : VerificationUiState()
    data class Submitted(val message: String) : VerificationUiState()
    data class Error(val message: String) : VerificationUiState()
}

class StudentVerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<VerificationUiState>(VerificationUiState.Idle)
    val uiState: StateFlow<VerificationUiState> = _uiState

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri

    fun selectImage(uri: String) {
        _selectedImageUri.value = uri
    }

    fun submitVerification(studentId: String, fullName: String, university: String) {
        viewModelScope.launch {
            _uiState.value = VerificationUiState.Loading
            delay(1500) // Simulate API call

            if (studentId.isBlank() || fullName.isBlank() || _selectedImageUri.value == null) {
                _uiState.value = VerificationUiState.Error("Please fill all fields and upload ID photo")
                return@launch
            }

            // Create verification request
            val verification = StudentVerification(
                id = System.currentTimeMillis().toString(),
                userId = "current_user_id",
                studentId = studentId,
                fullName = fullName,
                university = university,
                idPhotoUrl = _selectedImageUri.value ?: "",
                status = VerificationStatus.PENDING
            )

            // IMPORTANT: This triggers the snackbar
            _uiState.value = VerificationUiState.Submitted(
                "✓ Verification submitted! Admin will review within 24 hours"
            )
        }
    }

    fun resetState() {
        _uiState.value = VerificationUiState.Idle
        _selectedImageUri.value = null
    }
}