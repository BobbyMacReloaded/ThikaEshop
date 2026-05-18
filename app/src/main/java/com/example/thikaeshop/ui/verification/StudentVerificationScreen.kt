package com.example.thikaeshop.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.VerificationStatus
import com.example.thikaeshop.ui.components.IdPhotoUpload
import com.example.thikaeshop.ui.components.VerificationStatusCard
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.StudentVerificationViewModel
import com.example.thikaeshop.ui.viewmodels.VerificationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentVerificationScreen(
    existingStatus: VerificationStatus = VerificationStatus.PENDING,
    onBackClick: () -> Unit = {},
    onVerificationComplete: () -> Unit = {},
    viewModel: StudentVerificationViewModel
) {
    var studentId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("Mount Kenya University") }

    val uiState by viewModel.uiState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    // Snackbar state - only need this
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar when submission is successful
    LaunchedEffect(uiState) {
        when (val state = uiState) {

            is VerificationUiState.Submitted -> {

                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )

                onBackClick()
            }

            is VerificationUiState.Success -> {
                if (state.verification.status == VerificationStatus.APPROVED) {
                    onVerificationComplete()
                }
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Verification",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = EShopColors.Success,
                    contentColor = EShopColors.White,
                    actionColor = EShopColors.Gold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Info Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = EShopColors.Gold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Student verification is required to sell items on Student Exchange. This helps build trust in our community.",
                        fontSize = 11.sp,
                        color = EShopColors.Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Card (if already submitted)
            if (uiState is VerificationUiState.Success) {
                val state = uiState as VerificationUiState.Success
                VerificationStatusCard(
                    status = state.verification.status,
                    rejectionReason = state.verification.rejectionReason
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Verification Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verify Your Student Identity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Text(
                        text = "Upload your student ID to start selling",
                        fontSize = 12.sp,
                        color = EShopColors.White50
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ID Photo Upload
                    IdPhotoUpload(
                        imageUri = selectedImageUri,
                        onUploadClick = {
                            // TODO: Open image picker
                            viewModel.selectImage("sample_uri")
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name", color = EShopColors.White50) },
                        placeholder = { Text("As on your student ID", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Student ID
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Student ID Number", color = EShopColors.White50) },
                        placeholder = { Text("e.g., BIT/2024/61120", color = EShopColors.White50) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // University (pre-filled, can be edited)
                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("University/Institution", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            viewModel.submitVerification(studentId, fullName, university)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                        enabled = uiState !is VerificationUiState.Loading
                    ) {
                        if (uiState is VerificationUiState.Loading) {
                            CircularProgressIndicator(
                                color = EShopColors.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Submit Verification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                        }
                    }

                    // Error Message
                    if (uiState is VerificationUiState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (uiState as VerificationUiState.Error).message,
                            fontSize = 12.sp,
                            color = EShopColors.Error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Benefits Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "✅ Benefits of Verification",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Sell items on Student Exchange", fontSize = 11.sp, color = EShopColors.White50)
                    Text("• Build trust with buyers", fontSize = 11.sp, color = EShopColors.White50)
                    Text("• Eligible for escrow protection", fontSize = 11.sp, color = EShopColors.White50)
                    Text("• Verified badge on your profile", fontSize = 11.sp, color = EShopColors.White50)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

