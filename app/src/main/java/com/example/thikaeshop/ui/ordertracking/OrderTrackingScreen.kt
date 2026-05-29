package com.example.thikaeshop.ui.ordertracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.OrderStatus
import com.example.thikaeshop.ui.components.ProductImage
import com.example.thikaeshop.ui.components.StatusTimeline
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.OrderTrackingUiState
import com.example.thikaeshop.ui.viewmodels.OrderTrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String = "ORD-001",
    onBackClick: () -> Unit = {},
    onNavigateToRating: () -> Unit = {},
    viewModel: OrderTrackingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    // Fix: Assign to local variable before when statement
    val currentState = uiState

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        }
    ) { paddingValues ->
        when (currentState) {
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
                // Now safe to access currentState.tracking
                val tracking = currentState.tracking

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
                                    text = tracking.orderId.take(12),
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

                    // Product Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🛒 Product Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            tracking.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Product Image
                                    ProductImage(
                                        imageUrl = item.imageUrl,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EShopColors.White
                                        )
                                        Text(
                                            text = "Quantity: ${item.quantity}",
                                            fontSize = 13.sp,
                                            color = EShopColors.White50
                                        )
                                        Text(
                                            text = "Price: KSh ${item.price}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = EShopColors.Gold
                                        )
                                    }

                                    Text(
                                        text = "KSh ${item.price * item.quantity}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EShopColors.Gold
                                    )
                                }
                                if (item != tracking.items.last()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = EShopColors.White20)
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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

                    // Estimated Delivery Time
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

                    // Rider Info
                    if (tracking.status == OrderStatus.OUT_FOR_DELIVERY && tracking.riderInfo != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🚚 Rider Information",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(EShopColors.Orange),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👨‍🛵", fontSize = 24.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = tracking.riderInfo.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = EShopColors.White
                                        )
                                        Text(
                                            text = "📞 ${tracking.riderInfo.phone}",
                                            fontSize = 12.sp,
                                            color = EShopColors.White50
                                        )
                                        Text(
                                            text = "⭐ ${tracking.riderInfo.rating} ★",
                                            fontSize = 11.sp,
                                            color = EShopColors.Gold
                                        )
                                        Text(
                                            text = "📍 ${tracking.riderInfo.currentLocation}",
                                            fontSize = 11.sp,
                                            color = EShopColors.White50
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Rate Order Button
                    if (tracking.status == OrderStatus.DELIVERED) {
                        Button(
                            onClick = onNavigateToRating,
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
                        Text("Error: ${currentState.message}", color = EShopColors.Error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadOrder(orderId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

