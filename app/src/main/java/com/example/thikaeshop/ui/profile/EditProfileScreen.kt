package com.example.thikaeshop.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.components.ProfileImagePicker
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.EditProfileUiState
import com.example.thikaeshop.ui.viewmodels.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentName: String = "Emmanuel Muriuki",
    currentEmail: String = "emmanueli@mku.ac.ke",
    currentPhone: String = "+254711234567",
    currentStudentId: String = "BIT/2024/61120",
    onBackClick: () -> Unit = {},
    onSaveComplete: () -> Unit = {},
    viewModel: EditProfileViewModel = viewModel()
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var phoneNumber by remember { mutableStateOf(currentPhone) }
    var studentId by remember { mutableStateOf(currentStudentId) }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Handle save result
    LaunchedEffect(uiState) {
        when (uiState) {
            is EditProfileUiState.Success -> {
                showSuccessDialog = true
            }
            is EditProfileUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as EditProfileUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                actions = {
                    // Save button in top bar
                    TextButton(
                        onClick = {
                            viewModel.updateProfile(name, email, phoneNumber, studentId)
                        },
                        enabled = uiState !is EditProfileUiState.Loading
                    ) {
                        if (uiState is EditProfileUiState.Loading) {
                            CircularProgressIndicator(
                                color = EShopColors.Orange,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.Orange
                            )
                        }
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
                    containerColor = EShopColors.Error,
                    contentColor = EShopColors.White
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
            // Profile Image
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImagePicker(
                    onImageClick = {
                        // TODO: Open image picker
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap to change profile photo",
                    fontSize = 11.sp,
                    color = EShopColors.White50
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name", color = EShopColors.White50) },
                        placeholder = { Text("Enter your full name", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address", color = EShopColors.White50) },
                        placeholder = { Text("youremail@example.com", color = EShopColors.White50) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone Number", color = EShopColors.White50) },
                        placeholder = { Text("+254 XXX XXX XXX", color = EShopColors.White50) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        label = { Text("Student ID", color = EShopColors.White50) },
                        placeholder = { Text("e.g., BIT/2024/61120", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        ),
                        readOnly = true,  // Student ID cannot be changed
                        supportingText = {
                            Text(
                                text = "Student ID cannot be changed. Contact support for corrections.",
                                fontSize = 10.sp,
                                color = EShopColors.White30
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
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
                        text = "Your student ID is linked to your verification status. Contact admin if you need to change it.",
                        fontSize = 11.sp,
                        color = EShopColors.Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EShopColors.Success,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Profile Updated!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
            },
            text = {
                Text(
                    text = "Your profile has been updated successfully.",
                    fontSize = 14.sp,
                    color = EShopColors.White50
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSaveComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
                ) {
                    Text("OK", color = EShopColors.White)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    MaterialTheme {
        EditProfileScreen()
    }
}