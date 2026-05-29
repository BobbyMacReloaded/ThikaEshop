package com.example.thikaeshop.ui.editlisting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.EditListingUiState
import com.example.thikaeshop.ui.viewmodels.EditListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListingScreen(
    listingId: String,
    currentTitle: String,
    currentDescription: String,
    currentPrice: Int,
    currentCategory: String,
    currentLocation: String,
    currentLandmark: String,
    onBackClick: () -> Unit,
    onUpdateSuccess: () -> Unit,
    viewModel: EditListingViewModel = viewModel()
) {
    var title by remember { mutableStateOf(currentTitle) }
    var description by remember { mutableStateOf(currentDescription) }
    var price by remember { mutableStateOf(currentPrice.toString()) }
    var category by remember { mutableStateOf(currentCategory) }
    var location by remember { mutableStateOf(currentLocation) }
    var landmark by remember { mutableStateOf(currentLandmark) }

    val uiState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is EditListingUiState.Success) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Listing", color = EShopColors.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Edit Product Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Title", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Description", color = EShopColors.White50) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Price (KSh)", color = EShopColors.White50) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Category", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Location", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Landmark", color = EShopColors.White50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.updateListing(
                                listingId = listingId,
                                title = title,
                                description = description,
                                price = price.toIntOrNull() ?: 0,
                                category = category,
                                location = location,
                                landmark = landmark
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                        enabled = uiState !is EditListingUiState.Loading
                    ) {
                        if (uiState is EditListingUiState.Loading) {
                            CircularProgressIndicator(color = EShopColors.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState is EditListingUiState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (uiState as EditListingUiState.Error).message,
                            fontSize = 12.sp,
                            color = EShopColors.Error
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = EShopColors.Success, modifier = Modifier.size(48.dp)) },
            title = { Text("Updated!", color = EShopColors.White) },
            text = { Text("Your listing has been updated successfully.", color = EShopColors.White50) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onUpdateSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
                ) {
                    Text("OK")
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}