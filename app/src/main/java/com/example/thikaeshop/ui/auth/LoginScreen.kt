package com.example.thikaeshop.ui.auth


import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.components.CustomTextField
import com.example.thikaeshop.ui.components.OutlinedButton
import com.example.thikaeshop.ui.components.PrimaryButton
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.LoginUiState
import com.example.thikaeshop.ui.viewmodels.LoginViewModel
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onGuestSuccess: () -> Unit = {},
    onAdminSuccess: () -> Unit = {},
    viewModel: LoginViewModel
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    // Set activity when composable is first created
    LaunchedEffect(Unit) {
        (context as? Activity)?.let { activity ->
            viewModel.setActivity(activity)
        }
    }
    // Handle state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.OtpSent -> {
                isOtpSent = true
                verificationId = (uiState as LoginUiState.OtpSent).phoneNumber
            }
            is LoginUiState.Success -> onLoginSuccess()
            is LoginUiState.AdminSuccess -> onAdminSuccess()  // ← ADD THIS
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
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.LightBg)))
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

                        Spacer(Modifier.height(16.dp))

                        PrimaryButton(
                            text = "Continue with Phone",
                            onClick = { viewModel.sendOtp(phoneNumber) },
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
                            Box(Modifier.width(80.dp).height(1.dp).background(EShopColors.White30))
                            Text(
                                " OR ",
                                fontSize = 12.sp,
                                color = EShopColors.White50,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Box(Modifier.width(80.dp).height(1.dp).background(EShopColors.White30))
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            text = "Continue with Google",
                            onClick = { /* Google Sign-In */ },
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
