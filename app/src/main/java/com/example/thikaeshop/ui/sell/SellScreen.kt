package com.example.thikaeshop.ui.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.theme.EShopColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    onSubmit: (SellItem) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("New Goods") }
    var selectedCondition by remember { mutableStateOf("Like New") }
    var location by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showConditionDropdown by remember { mutableStateOf(false) }

    val categories = listOf("New Goods", "Student Exchange")
    val conditions = listOf("New", "Like New", "Good", "Fair")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sell Item", color = EShopColors.White) },
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
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.LightBg)))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Image Upload Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                        text = "Tap to add photos",
                        fontSize = 14.sp,
                        color = EShopColors.White50
                    )
                    Text(
                        text = "Upload up to 5 images",
                        fontSize = 10.sp,
                        color = EShopColors.White30
                    )
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
                placeholder = { Text("Describe your item condition, features, etc.", color = EShopColors.White50) },
                minLines = 3,
                maxLines = 5,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCategoryDropdown = !showCategoryDropdown }
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
                expanded = showCategoryDropdown,
                onDismissRequest = { showCategoryDropdown = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            showCategoryDropdown = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Condition (only show for Student Exchange/Second-hand)
            if (selectedCategory == "Student Exchange") {
                Text(
                    text = "Condition",
                    fontSize = 14.sp,
                    color = EShopColors.White50,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showConditionDropdown = !showConditionDropdown }
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
                    expanded = showConditionDropdown,
                    onDismissRequest = { showConditionDropdown = false }
                ) {
                    conditions.forEach { condition ->
                        DropdownMenuItem(
                            text = { Text(condition) },
                            onClick = {
                                selectedCondition = condition
                                showConditionDropdown = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location (Thika Area)", color = EShopColors.White50) },
                placeholder = { Text("e.g., Landless, near Blue Water Tank", color = EShopColors.White50) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EShopColors.Gold) },
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
                    if (title.isNotBlank() && price.isNotBlank()) {
                        val item = SellItem(
                            title = title,
                            description = description,
                            price = price.toIntOrNull() ?: 0,
                            category = selectedCategory,
                            condition = if (selectedCategory == "Student Exchange") selectedCondition else null,
                            location = location
                        )
                        onSubmit(item)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                enabled = title.isNotBlank() && price.isNotBlank()
            ) {
                Text(
                    text = "Post Listing",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "For Student Exchange items, payment is held in escrow until buyer confirms receipt",
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Data class for sell item
data class SellItem(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val condition: String? = null,
    val location: String
)

@Preview(showBackground = true)
@Composable
fun PreviewSellScreen() {
    MaterialTheme {
        SellScreen()
    }
}