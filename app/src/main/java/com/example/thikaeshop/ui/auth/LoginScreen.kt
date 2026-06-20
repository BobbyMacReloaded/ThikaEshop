package com.example.thikaeshop.ui.auth

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.thikaeshop.ui.components.CustomTextField
import com.example.thikaeshop.ui.components.OutlinedButton
import com.example.thikaeshop.ui.components.PrimaryButton
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.LoginUiState
import com.example.thikaeshop.ui.viewmodels.LoginViewModel
import com.example.thikaeshop.utils.SimplePrefs
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onGuestSuccess: () -> Unit = {},
    onAdminSuccess: () -> Unit = {},
    viewModel: LoginViewModel,
    simplePrefs: SimplePrefs
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Create Credential Manager for Google Sign-In
    val credentialManager = CredentialManager.create(context)

    // Load saved phone number when screen opens
    LaunchedEffect(Unit) {
        val savedPhone = simplePrefs.getSavedPhoneNumber()
        if (savedPhone.isNotEmpty()) {
            phoneNumber = savedPhone
        }
    }

    // Google Sign-In handler
    val googleSignIn = {
        scope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("208033205986-j8gb2rptvrlbh74f6k6sg6fqk8s9itpq.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                Toast.makeText(
                    context,
                    "Signing in with Google...",
                    Toast.LENGTH_SHORT
                ).show()

                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Sign in successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(
                                context,
                                "Sign in failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } catch (e: GetCredentialException) {
                Toast.makeText(
                    context,
                    "Sign in cancelled or failed",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Helper to get Activity from Context
    fun Context.getActivity(): Activity? {
        var currentContext = this
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    // Set Activity for Firebase Phone Auth
    LaunchedEffect(Unit) {
        val activity = context.getActivity()
        activity?.let { viewModel.setActivity(it) }
    }

    // Handle state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.OtpSent -> {
                isOtpSent = true
                verificationId = (uiState as LoginUiState.OtpSent).phoneNumber
            }
            is LoginUiState.Success -> onLoginSuccess()
            is LoginUiState.AdminSuccess -> onAdminSuccess()
            is LoginUiState.Guest -> onGuestSuccess()
            else -> Unit
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(500))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            EShopColors.DarkBg,
                            EShopColors.DarkCard
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                EShopColors.Orange,
                                EShopColors.Gold
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🛒", fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Thika Varsity",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = EShopColors.Gold
            )

            Text(
                text = "Last Mile E-Shop",
                fontSize = 16.sp,
                color = EShopColors.Orange
            )

            Spacer(Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (!isOtpSent) "Sign In" else "Enter Code",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(Modifier.height(24.dp))

                    if (!isOtpSent) {
                        CustomTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "0712345678",
                            leadingIcon = Icons.Default.Call,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(Modifier.height(8.dp))

                        // Remember Me Checkbox
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = EShopColors.Orange)
                                )
                                Text(
                                    text = "Remember Me",
                                    fontSize = 12.sp,
                                    color = EShopColors.White50
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        PrimaryButton(
                            text = "Continue with Phone",
                            onClick = {
                                if (rememberMe) {
                                    viewModel.savePhoneNumber(phoneNumber, simplePrefs)
                                }
                                viewModel.sendOtp(phoneNumber)
                            },
                            isLoading = uiState is LoginUiState.Loading,
                            enabled = phoneNumber.length >= 9
                        )

                        Spacer(Modifier.height(12.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier
                                .width(80.dp)
                                .height(1.dp)
                                .background(EShopColors.White30))
                            Text(
                                " OR ",
                                fontSize = 12.sp,
                                color = EShopColors.White50,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Box(Modifier
                                .width(80.dp)
                                .height(1.dp)
                                .background(EShopColors.White30))
                        }

                        Spacer(Modifier.height(12.dp))

                        // Google Sign-In Button
                        OutlinedButton(
                            text = "Continue with Google",
                            onClick = { googleSignIn() },
                            leadingText = "G"
                        )

                        Text(
                            text = "Browse as Guest (No buying/selling)",
                            fontSize = 12.sp,
                            color = EShopColors.White50,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clickable { viewModel.continueAsGuest() }
                        )

                    } else {
                        CustomTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6) otpCode = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Enter 6-digit code",
                            leadingIcon = Icons.Default.Lock,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(Modifier.height(16.dp))

                        PrimaryButton(
                            text = "Verify & Sign In",
                            onClick = { viewModel.verifyOtp(otpCode) },
                            isLoading = uiState is LoginUiState.Loading,
                            enabled = otpCode.length == 6
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "← Back",
                                fontSize = 12.sp,
                                color = EShopColors.White50,
                                modifier = Modifier.clickable { isOtpSent = false }
                            )
                            Text(
                                text = "Resend Code",
                                fontSize = 12.sp,
                                color = EShopColors.Orange,
                                modifier = Modifier.clickable { viewModel.sendOtp(phoneNumber) }
                            )
                        }
                    }

                    if (uiState is LoginUiState.Error) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            fontSize = 12.sp,
                            color = EShopColors.Error
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "By continuing, you agree to Terms & Privacy Policy",
                fontSize = 10.sp,
                color = EShopColors.White30,
                textAlign = TextAlign.Center
            )
        }
    }
}

