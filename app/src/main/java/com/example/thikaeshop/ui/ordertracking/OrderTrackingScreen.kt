package com.example.thikaeshop.ui.ordertracking

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.OrderStatus
import com.example.thikaeshop.ui.components.DeliveryInfoCard
import com.example.thikaeshop.ui.components.StatusTimeline
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.OrderTrackingUiState
import com.example.thikaeshop.ui.viewmodels.OrderTrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String = "ORD-001",
    onBackClick: () -> Unit = {},
    onNavigateToRating: () -> Unit = {},  // ← USE THIS instead of onConfirmReceipt
    viewModel: OrderTrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrderTracking(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Track Order",
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
        when (uiState) {
            is OrderTrackingUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EShopColors.Orange)
                }
            }
            is OrderTrackingUiState.Success -> {
                val tracking = (uiState as OrderTrackingUiState.Success).tracking

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Order ID Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Order ID",
                                    fontSize = 11.sp,
                                    color = EShopColors.White50
                                )
                                Text(
                                    text = tracking.orderId,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when (tracking.status) {
                                    OrderStatus.DELIVERED -> EShopColors.Success.copy(alpha = 0.15f)
                                    OrderStatus.CANCELLED -> EShopColors.Error.copy(alpha = 0.15f)
                                    else -> EShopColors.Orange.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = when (tracking.status) {
                                        OrderStatus.PROCESSING -> "Processing"
                                        OrderStatus.SHIPPED -> "Shipped"
                                        OrderStatus.OUT_FOR_DELIVERY -> "Out for Delivery"
                                        OrderStatus.DELIVERED -> "Delivered"
                                        OrderStatus.CANCELLED -> "Cancelled"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when (tracking.status) {
                                        OrderStatus.DELIVERED -> EShopColors.Success
                                        OrderStatus.CANCELLED -> EShopColors.Error
                                        else -> EShopColors.Orange
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StatusTimeline(currentStatus = tracking.status)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Delivery Location Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📍 Delivery Location",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tracking.deliveryLocation.area,
                                fontSize = 13.sp,
                                color = EShopColors.White
                            )
                            Text(
                                text = tracking.deliveryLocation.landmark,
                                fontSize = 11.sp,
                                color = EShopColors.Gold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estimated Delivery Time Card
                    if (tracking.status != OrderStatus.DELIVERED && tracking.status != OrderStatus.CANCELLED) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = EShopColors.Gold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Estimated Delivery",
                                        fontSize = 11.sp,
                                        color = EShopColors.Gold
                                    )
                                    Text(
                                        text = tracking.estimatedDeliveryTime,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EShopColors.Gold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Rider Info (if out for delivery)
                    if (tracking.status == OrderStatus.OUT_FOR_DELIVERY && tracking.riderInfo != null) {
                        DeliveryInfoCard(
                            riderInfo = tracking.riderInfo,
                            onCallClick = { /* Open phone dialer */ },
                            onChatClick = { /* Open chat */ }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Order Items
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🛒 Order Items",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            tracking.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.icon, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = item.name,
                                                fontSize = 13.sp,
                                                color = EShopColors.White
                                            )
                                            Text(
                                                text = "Qty: ${item.quantity}",
                                                fontSize = 11.sp,
                                                color = EShopColors.White50
                                            )
                                        }
                                    }
                                    Text(
                                        text = "KSh ${item.price}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EShopColors.Gold
                                    )
                                }
                                if (item != tracking.items.last()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = EShopColors.White20)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rate Order Button (for delivered items)
                    if (tracking.status == OrderStatus.DELIVERED) {
                        Button(
                            onClick = onNavigateToRating,  // ← USE THIS
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Success)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rate This Order",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            is OrderTrackingUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${(uiState as OrderTrackingUiState.Error).message}", color = EShopColors.Error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadOrderTracking(orderId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrderTrackingScreen() {
    MaterialTheme {
        OrderTrackingScreen()
    }
}