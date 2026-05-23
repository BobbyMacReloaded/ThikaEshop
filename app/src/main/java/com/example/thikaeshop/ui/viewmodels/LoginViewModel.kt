package com.example.thikaeshop.ui.viewmodels

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore  // ← ADD THIS IMPORT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class OtpSent(val phoneNumber: String) : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class AdminSuccess(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    object Guest : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()  // ← ADD THIS
    private var verificationId by mutableStateOf("")
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private var currentPhoneNumber = ""

    // Admin phone numbers - ONLY THESE NUMBERS GET ADMIN ACCESS
    private val adminNumbers = listOf(
        "+254708789116",   // Your admin number
        "+254722345678"     // Backup admin number
    )

    // Store activity reference for phone auth
    private var currentActivity: Activity? = null

    fun setActivity(activity: Activity) {
        currentActivity = activity
    }

    fun sendOtp(phoneNumber: String) {
        currentPhoneNumber = formatPhoneNumber(phoneNumber)
        _uiState.value = LoginUiState.Loading

        val activity = currentActivity
        if (activity == null) {
            _uiState.value = LoginUiState.Error("Activity not set. Please try again.")
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(currentPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.value = LoginUiState.Error(e.message ?: "Verification failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginViewModel.verificationId = verificationId
                    resendToken = token
                    _uiState.value = LoginUiState.OtpSent(currentPhoneNumber)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otpCode: String) {
        _uiState.value = LoginUiState.Loading

        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        signInWithPhoneAuthCredential(credential)
        // In LoginViewModel.kt, inside verifyOtp function after successful login
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("FIREBASE_UID", "User ID: ${currentUser?.uid}")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val userId = user?.uid ?: ""
                    val phoneNumber = user?.phoneNumber ?: ""

                    // Create user in Firestore
                    createUserInFirestore(userId, phoneNumber, adminNumbers.contains(phoneNumber))

                    // Check if this phone number is an admin
                    if (adminNumbers.contains(phoneNumber)) {
                        _uiState.value = LoginUiState.AdminSuccess(userId)
                    } else {
                        _uiState.value = LoginUiState.Success(userId)
                    }
                } else {
                    _uiState.value = LoginUiState.Error(task.exception?.message ?: "Verification failed")
                }
            }
    }

    fun continueAsGuest() {
        _uiState.value = LoginUiState.Guest
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        currentPhoneNumber = ""
        verificationId = ""
        resendToken = null
    }

    fun resendOtp() {
        val activity = currentActivity
        if (activity == null || verificationId.isEmpty()) {
            _uiState.value = LoginUiState.Error("Unable to resend code")
            return
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(currentPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.value = LoginUiState.Error(e.message ?: "Resend failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginViewModel.verificationId = verificationId
                    resendToken = token
                    _uiState.value = LoginUiState.OtpSent(currentPhoneNumber)
                }
            })

        resendToken?.let {
            optionsBuilder.setForceResendingToken(it)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun createUserInFirestore(userId: String, phoneNumber: String, isAdmin: Boolean) {
        val userMap = hashMapOf(
            "userId" to userId,
            "phone" to phoneNumber,
            "name" to "User",
            "isAdmin" to isAdmin,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("LoginViewModel", "User created in Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("LoginViewModel", "Failed to create user: ${e.message}")
            }
    }

    private fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return if (cleaned.startsWith("0")) {
            "+254${cleaned.drop(1)}"
        } else if (cleaned.startsWith("254")) {
            "+$cleaned"
        } else {
            "+254$cleaned"
        }
    }
}