package com.example.thikaeshop.ui.sell

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.SellUiState
import com.example.thikaeshop.ui.viewmodels.SellViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    onBackClick: () -> Unit = {},
    onSubmit: () -> Unit = {},
    viewModel: SellViewModel = viewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electronics") }
    var isSecondHand by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf("Like New") }
    var location by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    val categories = listOf("Textbooks", "Electronics", "Fashion", "Household", "Food")
    val conditions = listOf("New", "Like New", "Good", "Fair")

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Handle success
    LaunchedEffect(uiState) {
        if (uiState is SellUiState.Success) {
            onSubmit()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sell Item",
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
            // Image Upload Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Product image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add Photos",
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to add product photo",
                            fontSize = 14.sp,
                            color = EShopColors.White50
                        )
                        Text(
                            text = "Upload high-quality images",
                            fontSize = 11.sp,
                            color = EShopColors.White30
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Product Title", color = EShopColors.White50) },
                placeholder = { Text("e.g., Programming Textbook", color = EShopColors.White50) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EShopColors.Orange,
                    unfocusedBorderColor = EShopColors.White30,
                    focusedTextColor = EShopColors.White,
                    unfocusedTextColor = EShopColors.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description", color = EShopColors.White50) },
                placeholder = { Text("Describe your item...", color = EShopColors.White50) },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EShopColors.Orange,
                    unfocusedBorderColor = EShopColors.White30,
                    focusedTextColor = EShopColors.White,
                    unfocusedTextColor = EShopColors.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Price (KSh)", color = EShopColors.White50) },
                placeholder = { Text("e.g., 500", color = EShopColors.White50) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("KSh", color = EShopColors.Gold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EShopColors.Orange,
                    unfocusedBorderColor = EShopColors.White30,
                    focusedTextColor = EShopColors.White,
                    unfocusedTextColor = EShopColors.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Dropdown
            Text(
                text = "Category",
                fontSize = 14.sp,
                color = EShopColors.White50,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            var expandedCategory by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedCategory = true }
                    .background(EShopColors.White10, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedCategory, color = EShopColors.White)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = EShopColors.White50)
                }
            }
            DropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = EShopColors.White) },
                        onClick = {
                            selectedCategory = category
                            expandedCategory = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Second-hand Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Student Exchange (Second-hand)",
                    fontSize = 14.sp,
                    color = EShopColors.White50
                )
                Switch(
                    checked = isSecondHand,
                    onCheckedChange = { isSecondHand = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EShopColors.Orange,
                        checkedTrackColor = EShopColors.Orange.copy(alpha = 0.5f)
                    )
                )
            }

            // Condition (if second-hand)
            if (isSecondHand) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Condition",
                    fontSize = 14.sp,
                    color = EShopColors.White50,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                var expandedCondition by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCondition = true }
                        .background(EShopColors.White10, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedCondition, color = EShopColors.White)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = EShopColors.White50)
                    }
                }
                DropdownMenu(
                    expanded = expandedCondition,
                    onDismissRequest = { expandedCondition = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    conditions.forEach { condition ->
                        DropdownMenuItem(
                            text = { Text(condition, color = EShopColors.White) },
                            onClick = {
                                selectedCondition = condition
                                expandedCondition = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location (Area)", color = EShopColors.White50) },
                placeholder = { Text("e.g., Landless, Kiganjo, Kiandutu", color = EShopColors.White50) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EShopColors.Gold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EShopColors.Orange,
                    unfocusedBorderColor = EShopColors.White30,
                    focusedTextColor = EShopColors.White,
                    unfocusedTextColor = EShopColors.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Landmark
            OutlinedTextField(
                value = landmark,
                onValueChange = { landmark = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Landmark", color = EShopColors.White50) },
                placeholder = { Text("e.g., Near Blue Water Tank", color = EShopColors.White50) },
                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = EShopColors.Gold) },
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
                    if (selectedImageUri != null && title.isNotBlank() && price.isNotBlank()) {
                        val imageBytes = context.contentResolver
                            .openInputStream(selectedImageUri!!)
                            ?.use { it.readBytes() }

                        if (imageBytes != null) {
                            val ext = context.contentResolver
                                .getType(selectedImageUri!!)
                                ?.substringAfter("/")
                                ?: "jpg"

                            viewModel.uploadProduct(
                                title = title,
                                description = description,
                                price = price.toIntOrNull() ?: 0,
                                category = selectedCategory,
                                isSecondHand = isSecondHand,
                                condition = if (isSecondHand) selectedCondition else "",
                                location = location,
                                landmark = landmark,
                                imageBytes = imageBytes,
                                imageExtension = ext
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                enabled = selectedImageUri != null && title.isNotBlank() && price.isNotBlank() && uiState !is SellUiState.Loading
            ) {
                when (uiState) {
                    is SellUiState.Loading -> {
                        CircularProgressIndicator(
                            color = EShopColors.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...", color = EShopColors.White)
                    }
                    is SellUiState.Success -> {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EShopColors.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Posted Successfully!", color = EShopColors.White)
                    }
                    else -> {
                        Text(
                            text = "Post Listing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )
                    }
                }
            }

            // Error message
            if (uiState is SellUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (uiState as SellUiState.Error).message,
                    fontSize = 12.sp,
                    color = EShopColors.Error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Escrow Info Note
            if (isSecondHand) {
                Spacer(modifier = Modifier.height(16.dp))
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
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔒 Escrow Protection: Payment held until buyer confirms receipt",
                            fontSize = 11.sp,
                            color = EShopColors.Gold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSellScreen() {
    MaterialTheme {
        SellScreen()
    }
}