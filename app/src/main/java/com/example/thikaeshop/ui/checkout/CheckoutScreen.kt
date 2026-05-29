package com.example.thikaeshop.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.CheckoutUiState
import com.example.thikaeshop.ui.viewmodels.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    productId: String,
    productName: String,
    productPrice: Int,
    productImageUrl: String?,          // ✅ added
    sellerId: String?,                 // ✅ added
    quantity: Int,
    isSecondHand: Boolean,
    onBackClick: () -> Unit,
    onOrderPlaced: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    var selectedPayment by remember { mutableStateOf("M-Pesa") }
    var isProcessing by remember { mutableStateOf(false) }

    val totalAmount = productPrice * quantity
    val deliveryFee = 100
    val grandTotal = totalAmount + deliveryFee

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CheckoutUiState.Success) {
            isProcessing = false
            onOrderPlaced()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = EShopColors.White) },
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
        ) {
            // Delivery Address Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📍 Delivery Location", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                        Text("Change", fontSize = 12.sp, color = EShopColors.Orange, modifier = Modifier.clickable { })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Landless, Thika", fontSize = 14.sp, color = EShopColors.White)
                    }
                    Text("📍 Near Blue Water Tank", fontSize = 12.sp, color = EShopColors.White50, modifier = Modifier.padding(start = 28.dp))
                }
            }

            // Order Summary
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛒 Order Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row {
                            Box(
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(EShopColors.Orange.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) { Text("📚", fontSize = 24.sp) }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(productName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EShopColors.White)
                                Text("Quantity: $quantity", fontSize = 12.sp, color = EShopColors.White50)
                            }
                        }
                        Text("KSh $totalAmount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = EShopColors.White30)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 14.sp, color = EShopColors.White50)
                        Text("KSh $totalAmount", fontSize = 14.sp, color = EShopColors.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Fee", fontSize = 14.sp, color = EShopColors.White50)
                        Text("KSh $deliveryFee", fontSize = 14.sp, color = EShopColors.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = EShopColors.White30)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                        Text("KSh $grandTotal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💳 Payment Method", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth().clickable { selectedPayment = "M-Pesa" }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedPayment == "M-Pesa", onClick = { selectedPayment = "M-Pesa" }, colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("M-Pesa", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EShopColors.White)
                            Text("Pay with M-Pesa STK Push", fontSize = 11.sp, color = EShopColors.White50)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { selectedPayment = "Cash on Delivery" }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedPayment == "Cash on Delivery", onClick = { selectedPayment = "Cash on Delivery" }, colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cash on Delivery", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EShopColors.White)
                            Text("Pay when you receive the item", fontSize = 11.sp, color = EShopColors.White50)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSecondHand) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("🔒 Escrow Protection Active", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                            Text("Payment held securely until you confirm receipt of item", fontSize = 11.sp, color = EShopColors.Gold.copy(alpha = 0.8f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Place Order Button
            Button(
                onClick = {
                    if (isProcessing) return@Button
                    isProcessing = true
                    viewModel.placeOrder(
                        productId = productId,
                        productName = productName,
                        productPrice = productPrice,
                        productImageUrl = productImageUrl ?: "",
                        quantity = quantity,
                        totalAmount = grandTotal,
                        sellerId = sellerId ?: "",
                        deliveryLocation = "Landless, Thika",
                        landmark = "Near Blue Water Tank",
                        paymentMethod = selectedPayment
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = EShopColors.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...", color = EShopColors.White)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Place Order • KSh $grandTotal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By placing order, you agree to our Terms of Service\nand return policy",
                fontSize = 10.sp,
                color = EShopColors.White30,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

