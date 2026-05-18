package com.example.thikaeshop.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.ui.components.EmptyState
import com.example.thikaeshop.ui.components.LandmarkCard
import com.example.thikaeshop.ui.components.ListingCard
import com.example.thikaeshop.ui.components.ProfileMenuItem
import com.example.thikaeshop.ui.components.profile.*
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.ProfileUiState
import com.example.thikaeshop.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onVerificationClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},  // ← ADD THIS
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Listings", "Saved Landmarks")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
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
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EShopColors.Orange)
                }
            }
            is ProfileUiState.Success -> {
                val state = uiState as ProfileUiState.Success
                val userProfile = state.userProfile

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                        .padding(paddingValues)
                ) {
                    // Profile Header
                    item {
                        ProfileHeader(userProfile = userProfile)
                    }

                    // Stats Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                value = userProfile.totalOrders.toString(),
                                label = "Orders",
                                icon = Icons.Default.Receipt,
                                color = EShopColors.Orange,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = "KSh ${userProfile.totalSpent/1000}K",
                                label = "Spent",
                                icon = Icons.Default.Payments,
                                color = EShopColors.Gold,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = state.myListings.size.toString(),
                                label = "Listings",
                                icon = Icons.Default.ShoppingBag,
                                color = EShopColors.Success,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = userProfile.rating.toString(),
                                label = "Rating ★",
                                icon = Icons.Default.Star,
                                color = EShopColors.Warning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Tab Row
                    item {
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
                    }

                    // Tab Content
                    when (selectedTab) {
                        0 -> {
                            if (state.myListings.isEmpty()) {
                                item {
                                    EmptyState(
                                        icon = Icons.Default.ShoppingBag,
                                        title = "No Listings",
                                        message = "Sell your first item to see it here",
                                        buttonText = "Start Selling",
                                        onButtonClick = { }
                                    )
                                }
                            } else {
                                items(state.myListings) { listing ->
                                    ListingCard(
                                        listing = listing,
                                        onEditClick = { /* Edit listing */ },
                                        onDeleteClick = { /* Delete listing */ }
                                    )
                                }
                            }
                        }
                        1 -> {
                            if (state.savedLandmarks.isEmpty()) {
                                item {
                                    EmptyState(
                                        icon = Icons.Default.LocationOn,
                                        title = "No Saved Landmarks",
                                        message = "Add delivery locations for faster checkout",
                                        buttonText = "Add Landmark",
                                        onButtonClick = { }
                                    )
                                }
                            } else {
                                items(state.savedLandmarks) { landmark ->
                                    LandmarkCard(
                                        landmark = landmark,
                                        onDeleteClick = { /* Delete landmark */ }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Settings Section
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                        ) {
                            Column {
                                // Edit Profile - NOW USING onEditProfileClick
                                ProfileMenuItem(
                                    icon = Icons.Default.Edit,
                                    title = "Edit Profile",
                                    subtitle = "Update your personal information",
                                    onClick = onEditProfileClick  // ← CHANGED
                                )
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    color = EShopColors.White20
                                )
                                ProfileMenuItem(
                                    icon = Icons.Default.Settings,
                                    title = "Settings",
                                    subtitle = "Privacy, notifications, language",
                                    onClick = { }
                                )
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    color = EShopColors.White20
                                )
                                ProfileMenuItem(
                                    icon = Icons.AutoMirrored.Filled.Help,
                                    title = "Help & Support",
                                    subtitle = "FAQs, contact us, report issue",
                                    onClick = { }
                                )
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    color = EShopColors.White20
                                )
                                ProfileMenuItem(
                                    icon = Icons.Default.Info,
                                    title = "Terms & Privacy Policy",
                                    subtitle = "Read our terms and conditions",
                                    onClick = { }
                                )
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    color = EShopColors.White20
                                )
                                ProfileMenuItem(
                                    icon = Icons.Default.Verified,
                                    title = "Student Verification",
                                    subtitle = if (userProfile.isVerified) "Verified ✓" else "Get verified to sell",
                                    onClick = onVerificationClick
                                )
                            }
                        }
                    }

                    // Logout Button
                    item {
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = EShopColors.Error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logout",
                                color = EShopColors.Error,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${(uiState as ProfileUiState.Error).message}", color = EShopColors.Error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfileData() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(EShopColors.Orange, EShopColors.Gold)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👨‍🎓",
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Verified Badge
        if (userProfile.isVerified) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(EShopColors.Success.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = EShopColors.Success,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Verified Student",
                    fontSize = 10.sp,
                    color = EShopColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userProfile.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = EShopColors.White
        )

        Text(
            text = userProfile.email,
            fontSize = 13.sp,
            color = EShopColors.White50
        )

        Text(
            text = userProfile.phoneNumber,
            fontSize = 13.sp,
            color = EShopColors.White50
        )

        Text(
            text = "ID: ${userProfile.studentId}",
            fontSize = 11.sp,
            color = EShopColors.Gold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Member since ${userProfile.memberSince}",
            fontSize = 11.sp,
            color = EShopColors.White30
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    MaterialTheme {
        ProfileScreen()
    }
}