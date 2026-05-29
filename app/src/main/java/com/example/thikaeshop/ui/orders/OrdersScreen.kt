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
import com.example.thikaeshop.data.models.Order
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
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled")

    val allOrders = getSampleOrders()

    val filteredOrders = when (selectedTab) {
        0 -> allOrders.filter { it.status != "Delivered" && it.status != "Cancelled" }
        1 -> allOrders.filter { it.status == "Delivered" }
        else -> allOrders.filter { it.status == "Cancelled" }
    }
    val uiState by viewModel.uiState.collectAsState()


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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is OrdersUiState.Loading -> { /* Progress */ }
            is OrdersUiState.Success -> {
                val allOrders = (uiState as OrdersUiState.Success).orders
                val filteredOrders = when (selectedTab) {
                    0 -> allOrders.filter { it.status !in listOf("delivered", "cancelled") }
                    1 -> allOrders.filter { it.status == "delivered" }
                    else -> allOrders.filter { it.status == "cancelled" }
                }
                // Display orders using OrderCard (convert map to data class)
            }
            is OrdersUiState.Error -> { /* Error */ }
        }
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

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = EShopColors.White30,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No orders found",
                            fontSize = 16.sp,
                            color = EShopColors.White50
                        )
                        Text(
                            text = "Your orders will appear here",
                            fontSize = 12.sp,
                            color = EShopColors.White30
                        )
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
                            order = order,
                            onClick = { onOrderClick(order.id) },
                            onRateOrder = {
                                onRateOrder(order.id, order.productName, order.productIcon)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    onRateOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.take(8)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EShopColors.White
                )
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product info
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
                    Text(order.productIcon, fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = EShopColors.White
                    )
                    Text(
                        text = "Quantity: ${order.quantity}",
                        fontSize = 12.sp,
                        color = EShopColors.White50
                    )
                    Text(
                        text = "Total: KSh ${order.totalAmount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery info
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
                        text = order.location,
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
                Text(
                    text = order.date,
                    fontSize = 11.sp,
                    color = EShopColors.White50
                )
            }

            // Progress tracker for active orders
            if (order.status != "Delivered" && order.status != "Cancelled") {
                Spacer(modifier = Modifier.height(12.dp))
                OrderProgressTracker(currentStatus = order.status)
            }

            // RATE ORDER button (for delivered items)
            if (order.status == "Delivered") {
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

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "Processing" -> EShopColors.Orange to "⏳ Processing"
        "Shipped" -> EShopColors.Warning to "📦 Shipped"
        "Delivered" -> EShopColors.Success to "✅ Delivered"
        "Cancelled" -> EShopColors.Error to "❌ Cancelled"
        else -> EShopColors.White50 to status
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OrderProgressTracker(currentStatus: String) {
    val steps = listOf("Order Placed", "Processing", "Shipped", "Delivered")
    val currentStep = when (currentStatus) {
        "Processing" -> 1
        "Shipped" -> 2
        else -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) EShopColors.Orange
                            else EShopColors.White20
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (index) {
                        0 -> Text("📦", fontSize = 12.sp)
                        1 -> Text("⚙️", fontSize = 12.sp)
                        2 -> Text("🚚", fontSize = 12.sp)
                        3 -> Text("📍", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step,
                    fontSize = 8.sp,
                    color = if (index <= currentStep) EShopColors.White60 else EShopColors.White30
                )
            }
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (index < currentStep) EShopColors.Orange
                            else EShopColors.White20
                        )
                )
            }
        }
    }
}

fun getSampleOrders(): List<Order> {
    return listOf(
        Order(
            id = "ORD-001",
            productName = "Programming Textbook - Kotlin",
            productIcon = "📚",
            quantity = 1,
            totalAmount = 450,
            status = "Delivered",  // Changed to Delivered to show Rate button
            location = "Landless, Thika",
            date = "2024-01-15",
            isEscrowHeld = true
        ),
        Order(
            id = "ORD-002",
            productName = "Samsung Galaxy A14",
            productIcon = "📱",
            quantity = 1,
            totalAmount = 12000,
            status = "Shipped",
            location = "Kiganjo, Thika",
            date = "2024-01-14",
            isEscrowHeld = true
        ),
        Order(
            id = "ORD-003",
            productName = "Fresh Vegetables",
            productIcon = "🥬",
            quantity = 2,
            totalAmount = 500,
            status = "Delivered",
            location = "Kiandutu, Thika",
            date = "2024-01-10",
            isEscrowHeld = false
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewOrdersScreen() {
    MaterialTheme {
        OrdersScreen()
    }
}