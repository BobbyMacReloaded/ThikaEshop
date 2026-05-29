package com.example.thikaeshop.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.OrdersUiState
import com.example.thikaeshop.ui.viewmodels.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit = {},
    onOrderClick: (String) -> Unit = {},
    onRateOrder: (String, String, String) -> Unit = { _, _, _ -> },
    viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled")

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", color = EShopColors.White) },
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EShopColors.DarkCard,
                contentColor = EShopColors.Orange
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) EShopColors.Orange else EShopColors.White50
                            )
                        }
                    )
                }
            }

            // Fix: Assign uiState to a local value before when statement
            val currentState = uiState

            when (currentState) {
                is OrdersUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EShopColors.Orange)
                    }
                }

                is OrdersUiState.Success -> {
                    val allOrders = currentState.orders

                    val filteredOrders = when (selectedTab) {
                        0 -> allOrders.filter { it.status !in listOf("delivered", "cancelled") }
                        1 -> allOrders.filter { it.status == "delivered" }
                        else -> allOrders.filter { it.status == "cancelled" }
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = EShopColors.White30,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No orders found", fontSize = 16.sp, color = EShopColors.White50)
                                Text("Your orders will appear here", fontSize = 12.sp, color = EShopColors.White30)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders) { order ->
                                OrderCard(
                                    id = order.id,
                                    orderNumber = order.orderNumber,
                                    productName = order.productName,
                                    productIcon = order.productIcon,
                                    quantity = order.quantity,
                                    totalAmount = order.totalAmount,
                                    status = order.status,
                                    location = order.location,
                                    date = order.date,
                                    isEscrowHeld = order.isEscrowHeld,
                                    onOrderClick = { onOrderClick(order.id) },
                                    onRateOrder = { onRateOrder(order.id, order.productName, order.productIcon) }
                                )
                            }
                        }
                    }
                }

                is OrdersUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${currentState.message}", color = EShopColors.Error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadOrders() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    id: String,
    orderNumber: String,
    productName: String,
    productIcon: String,
    quantity: Int,
    totalAmount: Int,
    status: String,
    location: String,
    date: String,
    isEscrowHeld: Boolean,
    onOrderClick: () -> Unit,
    onRateOrder: () -> Unit
) {
    val statusColor = when (status.lowercase()) {
        "pending", "processing" -> EShopColors.Warning
        "shipped", "out_for_delivery" -> EShopColors.Orange
        "delivered" -> EShopColors.Success
        "cancelled" -> EShopColors.Error
        else -> EShopColors.White50
    }

    val statusText = when (status.lowercase()) {
        "pending" -> "Pending"
        "processing" -> "Processing"
        "shipped" -> "Shipped"
        "out_for_delivery" -> "Out for Delivery"
        "delivered" -> "Delivered"
        "cancelled" -> "Cancelled"
        else -> status
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOrderClick),
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
                    text = orderNumber.take(12),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EShopColors.White
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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

                Column(modifier = Modifier.weight(1f)) {
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
                    Text(
                        text = "Total: KSh $totalAmount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EShopColors.Gold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = EShopColors.White50
                )
            }

            if (status.lowercase() == "delivered") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRateOrder,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Success)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rate This Order", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrdersScreen() {
    MaterialTheme {
        OrdersScreen()
    }
}