package com.example.thikaeshop.ui.rating


import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.components.StarRating
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.RatingUiState
import com.example.thikaeshop.ui.viewmodels.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    orderId: String = "ORD-001",
    productName: String = "Programming Textbook",
    productIcon: String = "📚",
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {},
    viewModel: RatingViewModel = viewModel()
) {
    var productRating by remember { mutableStateOf(0) }
    var sellerRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Handle submission success
    LaunchedEffect(uiState) {
        if (uiState is RatingUiState.Success) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rate Your Experience",
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
            // Product Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EShopColors.Orange.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(productIcon, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = productName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )
                        Text(
                            text = "Order #${orderId.take(8)}",
                            fontSize = 11.sp,
                            color = EShopColors.White50
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product Rating Section
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
                        text = "Rate the Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StarRating(
                        rating = productRating,
                        onRatingChange = { productRating = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (productRating) {
                            1 -> "Poor - Not satisfied"
                            2 -> "Fair - Could be better"
                            3 -> "Good - Satisfied"
                            4 -> "Very Good - Happy with purchase"
                            5 -> "Excellent - Perfect!"
                            else -> "Tap a star to rate"
                        },
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seller Rating Section
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
                        text = "Rate the Seller",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StarRating(
                        rating = sellerRating,
                        onRatingChange = { sellerRating = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (sellerRating) {
                            1 -> "Poor communication"
                            2 -> "Needs improvement"
                            3 -> "Average seller"
                            4 -> "Good seller"
                            5 -> "Excellent seller!"
                            else -> "Tap a star to rate"
                        },
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review Text Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Write a Review (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Share your experience with this product...",
                                color = EShopColors.White50
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.submitRating(
                        orderId = orderId,
                        productRating = productRating,
                        sellerRating = sellerRating,
                        review = reviewText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                enabled = productRating > 0 && uiState !is RatingUiState.Loading
            ) {
                if (uiState is RatingUiState.Loading) {
                    CircularProgressIndicator(
                        color = EShopColors.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Rating",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                }
            }

            // Skip option
            TextButton(
                onClick = onSubmitSuccess,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Skip for now",
                    fontSize = 12.sp,
                    color = EShopColors.White50
                )
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
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = EShopColors.Gold,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Thank You!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
            },
            text = {
                Text(
                    text = "Your rating helps other students make better choices.",
                    fontSize = 14.sp,
                    color = EShopColors.White50
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSubmitSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
                ) {
                    Text("Done", color = EShopColors.White)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRatingScreen() {
    MaterialTheme {
        RatingScreen()
    }
}