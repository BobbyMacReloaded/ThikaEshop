package com.example.thikaeshop.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.theme.EShopColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    productId: String = "",
    productName: String = "Programming Textbook",
    productPrice: Int = 450,
    quantity: Int = 1,
    isSecondHand: Boolean = true,
    onBackClick: () -> Unit = {},
    onOrderPlaced: () -> Unit = {}
) {
    var selectedPayment by remember { mutableStateOf("M-Pesa") }
    var showPaymentDropdown by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val totalAmount = productPrice * quantity
    val deliveryFee = 100
    val grandTotal = totalAmount + deliveryFee

    val paymentMethods = listOf("M-Pesa", "Cash on Delivery")

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍 Delivery Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )
                        Text(
                            text = "Change",
                            fontSize = 12.sp,
                            color = EShopColors.Orange,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Landless, Thika",
                            fontSize = 14.sp,
                            color = EShopColors.White
                        )
                    }

                    Text(
                        text = "📍 Near Blue Water Tank",
                        fontSize = 12.sp,
                        color = EShopColors.White50,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
            }

            // Order Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛒 Order Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Product row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EShopColors.Orange.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📚", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = productName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EShopColors.White
                                )
                                Text(
                                    text = "Quantity: $quantity",
                                    fontSize = 12.sp,
                                    color = EShopColors.White50
                                )
                            }
                        }
                        Text(
                            text = "KSh $totalAmount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.Gold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = EShopColors.White30)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontSize = 14.sp, color = EShopColors.White50)
                        Text("KSh $totalAmount", fontSize = 14.sp, color = EShopColors.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", fontSize = 14.sp, color = EShopColors.White50)
                        Text("KSh $deliveryFee", fontSize = 14.sp, color = EShopColors.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = EShopColors.White30)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )
                        Text(
                            text = "KSh $grandTotal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.Gold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 Payment Method",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // M-Pesa Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = "M-Pesa" }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "M-Pesa",
                            onClick = { selectedPayment = "M-Pesa" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = EShopColors.Orange
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "M-Pesa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EShopColors.White
                            )
                            Text(
                                text = "Pay with M-Pesa STK Push",
                                fontSize = 11.sp,
                                color = EShopColors.White50
                            )
                        }
                    }

                    // Cash on Delivery Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = "Cash on Delivery" }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "Cash on Delivery",
                            onClick = { selectedPayment = "Cash on Delivery" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = EShopColors.Orange
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cash on Delivery",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EShopColors.White
                            )
                            Text(
                                text = "Pay when you receive the item",
                                fontSize = 11.sp,
                                color = EShopColors.White50
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Escrow Info (for second-hand items)
            if (isSecondHand) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🔒 Escrow Protection Active",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.Gold
                            )
                            Text(
                                text = "Payment held securely until you confirm receipt of item",
                                fontSize = 11.sp,
                                color = EShopColors.Gold.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Place Order Button
            Button(
                onClick = {
                    isProcessing = true
                    // Simulate payment processing
                    // In real app, this will call M-Pesa API
                    androidx.compose.runtime.snapshots.SnapshotStateList<kotlinx.coroutines.Job>()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = EShopColors.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...", color = EShopColors.White)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place Order • KSh $grandTotal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms notice
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

@Preview(showBackground = true)
@Composable
fun PreviewCheckoutScreen() {
    MaterialTheme {
        CheckoutScreen()
    }
}