package com.example.thikaeshop.ui.checkout

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.CheckoutUiState
import com.example.thikaeshop.ui.viewmodels.CheckoutViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    productId: String,
    productName: String,
    productPrice: Int,
    productImageUrl: String?,
    sellerId: String?,
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
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    val context = LocalContext.current

    // M-Pesa dialog states
    var showMpesaDialog by remember { mutableStateOf(false) }
    var mpesaNumber by remember { mutableStateOf("") }

    // Handle payment status
    LaunchedEffect(paymentStatus) {
        when (paymentStatus) {
            is CheckoutViewModel.PaymentStatus.Success -> {
                Toast.makeText(context, "Payment initiated! Check your phone for STK Push", Toast.LENGTH_LONG).show()
                onOrderPlaced()
            }
            is CheckoutViewModel.PaymentStatus.Error -> {
                val errorMsg = (paymentStatus as CheckoutViewModel.PaymentStatus.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    // Handle order placement (for Cash on Delivery)
    LaunchedEffect(uiState) {
        when (uiState) {
            is CheckoutUiState.Success -> {
                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
                isProcessing = false
                onOrderPlaced()
            }
            is CheckoutUiState.Error -> {
                Toast.makeText(context, (uiState as CheckoutUiState.Error).message, Toast.LENGTH_LONG).show()
                isProcessing = false
            }
            is CheckoutUiState.Loading -> {}
            else -> {}
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EShopColors.DarkBg)
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
                        Icon(Icons.Default.LocationOn, tint = EShopColors.Gold, modifier = Modifier.size(20.dp), contentDescription = "")
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
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { selectedPayment = "M-Pesa" }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "M-Pesa",
                            onClick = { selectedPayment = "M-Pesa" },
                            colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("M-Pesa", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EShopColors.White)
                            Text("Pay with M-Pesa STK Push", fontSize = 11.sp, color = EShopColors.White50)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { selectedPayment = "Cash on Delivery" }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "Cash on Delivery",
                            onClick = { selectedPayment = "Cash on Delivery" },
                            colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange)
                        )
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
                        Icon(Icons.Default.Security, tint = EShopColors.Gold, modifier = Modifier.size(24.dp), contentDescription = "")
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("🔒 Escrow Protection Active", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                            Text("Payment held securely until you confirm receipt", fontSize = 11.sp, color = EShopColors.Gold.copy(alpha = 0.8f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Place Order Button (for Cash on Delivery)
            // M-Pesa button is shown separately
            if (selectedPayment == "Cash on Delivery") {
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
                        Icon(Icons.Default.Lock, modifier = Modifier.size(18.dp), contentDescription = "")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Place Order • KSh $grandTotal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    }
                }
            }

            // M-Pesa Payment Button
// In your CheckoutScreen, replace the M-Pesa button section with:

            if (selectedPayment == "M-Pesa") {
                Button(
                    onClick = { showMpesaDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                    enabled = !isProcessing && paymentStatus !is CheckoutViewModel.PaymentStatus.Loading
                ) {
                    when (paymentStatus) {
                        is CheckoutViewModel.PaymentStatus.Loading -> {
                            CircularProgressIndicator(color = EShopColors.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing Payment...", color = EShopColors.White)
                        }
                        else -> {
                            Icon(Icons.Default.Lock, modifier = Modifier.size(18.dp), contentDescription = "")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay with M-Pesa • KSh $grandTotal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                        }
                    }
                }
            }

// M-Pesa Number Dialog
            if (showMpesaDialog) {
                AlertDialog(
                    onDismissRequest = { showMpesaDialog = false },
                    title = { Text("Enter M-Pesa Number", color = EShopColors.White) },
                    text = {
                        OutlinedTextField(
                            value = mpesaNumber,
                            onValueChange = { mpesaNumber = it },
                            placeholder = { Text("0712345678", color = EShopColors.White50) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EShopColors.Orange,
                                unfocusedBorderColor = EShopColors.White30,
                                focusedTextColor = EShopColors.White,
                                unfocusedTextColor = EShopColors.White
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (mpesaNumber.isNotBlank()) {
                                    // Call M-Pesa payment (does NOT save order yet)
                                    viewModel.processMpesaPayment(
                                        phone = mpesaNumber,
                                        amount = grandTotal,
                                        orderId = "ORDER-${System.currentTimeMillis()}"
                                    )
                                    showMpesaDialog = false
                                }
                            },
                            enabled = mpesaNumber.isNotBlank()
                        ) {
                            Text("Pay Now")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showMpesaDialog = false }) {
                            Text("Cancel", color = EShopColors.Orange)
                        }
                    },
                    containerColor = EShopColors.DarkCard,
                    shape = RoundedCornerShape(16.dp)
                )
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

    // M-Pesa Number Dialog
    if (showMpesaDialog) {
        AlertDialog(
            onDismissRequest = { showMpesaDialog = false },
            title = { Text("Enter M-Pesa Number", color = EShopColors.White) },
            text = {
                OutlinedTextField(
                    value = mpesaNumber,
                    onValueChange = { mpesaNumber = it },
                    placeholder = { Text("0712345678", color = EShopColors.White50) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EShopColors.Orange,
                        unfocusedBorderColor = EShopColors.White30,
                        focusedTextColor = EShopColors.White,
                        unfocusedTextColor = EShopColors.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mpesaNumber.isNotBlank()) {
                            viewModel.processMpesaPayment(mpesaNumber, grandTotal, "ORDER-${System.currentTimeMillis()}")
                            showMpesaDialog = false
                        }
                    }
                ) {
                    Text("Pay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMpesaDialog = false }) {
                    Text("Cancel", color = EShopColors.Orange)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}