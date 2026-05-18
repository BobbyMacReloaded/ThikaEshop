package com.example.thikaeshop.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.component.AdminProductCard
import com.example.thikaeshop.ui.components.*
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.AdminUiState
import com.example.thikaeshop.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedSection by remember { mutableStateOf<String?>("user_management") }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Dashboard",
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
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = EShopColors.Error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is AdminUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EShopColors.Orange)
                }
            }
            is AdminUiState.AllDataSuccess -> {
                val data = uiState as AdminUiState.AllDataSuccess

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ========== SECTION 1: USER MANAGEMENT ==========
                    item {
                        ExpandableSection(
                            title = "👥 User Management",
                            icon = Icons.Default.People,
                            isExpanded = expandedSection == "user_management",
                            onExpandClick = {
                                expandedSection = if (expandedSection == "user_management") null else "user_management"
                            }
                        ) {
                            Column {
                                // Pending Verifications
                                Text("Pending Verifications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.pendingVerifications.isEmpty()) {
                                    Text("No pending verifications", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    // FIX: Use Column instead of forEach with individual items
                                    data.pendingVerifications.forEach { verification ->
                                        PendingVerificationCard(
                                            verification = verification,
                                            onApprove = { viewModel.approveVerification(verification.id) },
                                            onReject = { viewModel.rejectVerification(verification.id, "Invalid ID") },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // All Users
                                Text("All Users", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                data.users.forEach { user ->
                                    AdminUserCard(
                                        user = user,
                                        onSuspend = { viewModel.suspendUser(user.id) },
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ========== SECTION 2: PLATFORM MANAGEMENT ==========
                    item {
                        ExpandableSection(
                            title = "📍 Platform Management",
                            icon = Icons.Default.Settings,
                            isExpanded = expandedSection == "platform_management",
                            onExpandClick = {
                                expandedSection = if (expandedSection == "platform_management") null else "platform_management"
                            }
                        ) {
                            Column {
                                // Pending Landmarks
                                Text("Pending Landmarks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.pendingLandmarks.isEmpty()) {
                                    Text("No pending landmarks", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.pendingLandmarks.forEach { landmark ->
                                        PendingLandmarkCard(
                                            landmark = landmark,
                                            onApprove = { viewModel.approveLandmark(landmark.id) },
                                            onReject = { viewModel.rejectLandmark(landmark.id) },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Products
                                Text("All Products", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                data.products.forEach { product ->
                                    AdminProductCard(
                                        product = product,
                                        onRemove = { viewModel.removeProduct(product.id) },
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Reported Items
                                Text("Reported Items", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Error)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.reportedItems.isEmpty()) {
                                    Text("No reported items", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.reportedItems.forEach { report ->
                                        ReportedItemCard(
                                            report = report,
                                            onDismiss = { viewModel.dismissReport(report.id) },
                                            onRemove = { viewModel.removeProduct(report.id) },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ========== SECTION 3: ORDER MANAGEMENT ==========
                    item {
                        ExpandableSection(
                            title = "📦 Order Management",
                            icon = Icons.Default.ShoppingCart,
                            isExpanded = expandedSection == "order_management",
                            onExpandClick = {
                                expandedSection = if (expandedSection == "order_management") null else "order_management"
                            }
                        ) {
                            Column {
                                // Pending Deliveries
                                Text("Pending Deliveries", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.pendingDeliveries.isEmpty()) {
                                    Text("No pending deliveries", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.pendingDeliveries.forEach { delivery ->
                                        PendingDeliveryCard(
                                            delivery = delivery,
                                            onAssign = { /* Show assign dialog */ },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Assigned Deliveries
                                Text("Assigned Deliveries", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                data.assignedDeliveries.forEach { delivery ->
                                    AssignedDeliveryCard(
                                        delivery = delivery,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // All Transactions
                                Text("All Transactions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                data.transactions.forEach { transaction ->
                                    AdminTransactionCard(
                                        transaction = transaction,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Disputes
                                Text("Disputes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Error)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.disputes.isEmpty()) {
                                    Text("No disputes", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.disputes.forEach { dispute ->
                                        AdminDisputeCard(
                                            dispute = dispute,
                                            onResolve = { decision -> viewModel.resolveDispute(dispute.id, decision) },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ========== SECTION 4: RIDER MANAGEMENT ==========
                    item {
                        ExpandableSection(
                            title = "🛵 Rider Management",
                            icon = Icons.Default.DeliveryDining,
                            isExpanded = expandedSection == "rider_management",
                            onExpandClick = {
                                expandedSection = if (expandedSection == "rider_management") null else "rider_management"
                            }
                        ) {
                            Column {
                                Text("Available Riders", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.availableRiders.isEmpty()) {
                                    Text("No available riders", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.availableRiders.forEach { rider ->
                                        AvailableRiderCard(
                                            rider = rider,
                                            onSelect = { },
                                            isSelected = false,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("Rider Applications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (data.riderApplications.isEmpty()) {
                                    Text("No pending applications", fontSize = 12.sp, color = EShopColors.White50)
                                } else {
                                    data.riderApplications.forEach { application ->
                                        RiderApplicationCard(
                                            application = application,
                                            onApprove = { viewModel.approveRider(application.id) },
                                            onReject = { viewModel.rejectRider(application.id) },
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ========== SECTION 5: ANALYTICS ==========
                    item {
                        ExpandableSection(
                            title = "📊 Analytics",
                            icon = Icons.Default.Analytics,
                            isExpanded = expandedSection == "analytics",
                            onExpandClick = {
                                expandedSection = if (expandedSection == "analytics") null else "analytics"
                            }
                        ) {
                            Column {
                                // Stats Cards
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AdminStatCard(
                                        value = data.stats.totalUsers.toString(),
                                        label = "Users",
                                        icon = Icons.Default.People,
                                        color = EShopColors.Gold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AdminStatCard(
                                        value = data.stats.totalOrders.toString(),
                                        label = "Orders",
                                        icon = Icons.Default.ShoppingCart,
                                        color = EShopColors.Orange,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AdminStatCard(
                                        value = "KSh ${data.stats.totalRevenue}",
                                        label = "Revenue",
                                        icon = Icons.Default.Payments,
                                        color = EShopColors.Success,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AdminStatCard(
                                        value = data.stats.pendingVerifications.toString(),
                                        label = "Pending Verifications",
                                        icon = Icons.Default.HourglassEmpty,
                                        color = EShopColors.Warning,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AdminStatCard(
                                        value = data.stats.pendingLandmarks.toString(),
                                        label = "Pending Landmarks",
                                        icon = Icons.Default.LocationOn,
                                        color = EShopColors.Gold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AdminStatCard(
                                        value = data.stats.pendingDisputes.toString(),
                                        label = "Disputes",
                                        icon = Icons.Default.Gavel,
                                        color = EShopColors.Error,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is AdminUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${(uiState as AdminUiState.Error).message}", color = EShopColors.Error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAllData() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Unexpected state", color = EShopColors.Error)
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = EShopColors.Orange)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                }
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = EShopColors.Gold
                )
            }

            if (isExpanded) {
                Divider(color = EShopColors.White20)
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}