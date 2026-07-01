package com.example.thikaeshop.ui.profile

import android.util.Log // For Event Logging
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.ui.components.EmptyState
import com.example.thikaeshop.ui.components.LandmarkCard
import com.example.thikaeshop.ui.components.ListingCard
import com.example.thikaeshop.ui.components.ProfileMenuItem
import com.example.thikaeshop.ui.components.profile.*
import com.example.thikaeshop.ui.editlisting.EditListingScreen
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.ProfileUiState
import com.example.thikaeshop.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.delay

// =====================================================================
// 1. CLASS-BASED VALIDATOR (Refactored to Boolean for Bulletproof Logic)
// =====================================================================
class ProfileValidator {
    fun isDeletionNotesValid(notes: String): Boolean {
        return notes.length >= 3
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onVerificationClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSellClick: () -> Unit = {},
    onSubscriptionClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Listings", "Saved Landmarks")

    var showEditListing by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedListingToDelete by remember { mutableStateOf("") }
    var loadingTimeout by remember { mutableStateOf(false) }

    // State validation helper variables
    var deleteReason by remember { mutableStateOf("") }
    val validator = remember { ProfileValidator() }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Loading) {
            delay(8000)
            loadingTimeout = true
        } else {
            loadingTimeout = false
        }
    }

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
        if (showEditListing && selectedProduct != null) {
            EditListingScreen(
                listingId = selectedProduct!!.id,
                currentTitle = selectedProduct!!.title,
                currentDescription = selectedProduct!!.description,
                currentPrice = selectedProduct!!.price,
                currentCategory = selectedProduct!!.category,
                currentLocation = selectedProduct!!.location,
                currentLandmark = selectedProduct!!.landmark,
                onBackClick = { showEditListing = false },
                onUpdateSuccess = {
                    showEditListing = false
                    viewModel.loadProfileData()
                }
            )
        } else {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EShopColors.Orange)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading profile...",
                                color = EShopColors.White50,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    val state = uiState as ProfileUiState.Success
                    val userProfile = state.userProfile
                    val myListings = state.myListings
                    val subscription = state.subscription

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                            .padding(paddingValues)
                            // =================================================================
                            // 2. TOUCH GESTURE HANDLING (Swipe to switch tabs)
                            // =================================================================
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        // Swipe Left detector to switch to Saved Landmarks tab
                                        if (dragAmount.x < -20 && selectedTab == 0) {
                                            Log.d("GestureHandler", "Swipe Left Detected")
                                            selectedTab = 1
                                        }
                                        // Swipe Right detector to return to My Listings tab
                                        else if (dragAmount.x > 20 && selectedTab == 1) {
                                            Log.d("GestureHandler", "Swipe Right Detected")
                                            selectedTab = 0
                                        }
                                    }
                                )
                            }
                    ) {
                        item {
                            ProfileHeader(
                                userProfile = userProfile,
                                subscription = subscription
                            )
                        }

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
                                    value = myListings.size.toString(),
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

                        when (selectedTab) {
                            0 -> {
                                if (myListings.isEmpty()) {
                                    item {
                                        EmptyState(
                                            icon = Icons.Default.ShoppingBag,
                                            title = "No Listings",
                                            message = "Sell your first item to see it here",
                                            buttonText = "Start Selling",
                                            onButtonClick = { onSellClick.invoke() }
                                        )
                                    }
                                } else {
                                    items(myListings) { product ->
                                        ListingCard(
                                            listing = product,
                                            onEditClick = {
                                                selectedProduct = product
                                                showEditListing = true
                                            },
                                            onDeleteClick = {
                                                selectedListingToDelete = product.id
                                                showDeleteConfirmation = true
                                            }
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

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                            ) {
                                Column {
                                    ProfileMenuItem(
                                        icon = Icons.Default.Edit,
                                        title = "Edit Profile",
                                        subtitle = "Update your personal information",
                                        onClick = onEditProfileClick
                                    )
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = EShopColors.White20)
                                    ProfileMenuItem(
                                        icon = Icons.Default.Settings,
                                        title = "Settings",
                                        subtitle = "Privacy, notifications, language",
                                        onClick = { }
                                    )
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = EShopColors.White20)
                                    ProfileMenuItem(
                                        icon = Icons.AutoMirrored.Filled.Help,
                                        title = "Help & Support",
                                        subtitle = "FAQs, contact us, report issue",
                                        onClick = { }
                                    )
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = EShopColors.White20)
                                    ProfileMenuItem(
                                        icon = Icons.Default.Info,
                                        title = "Terms & Privacy Policy",
                                        subtitle = "Read our terms and conditions",
                                        onClick = { }
                                    )
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = EShopColors.White20)
                                    ProfileMenuItem(
                                        icon = Icons.Default.Verified,
                                        title = "Student Verification",
                                        subtitle = if (userProfile.isVerified) "Verified ✓" else "Get verified to sell",
                                        onClick = onVerificationClick
                                    )
                                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = EShopColors.White20)
                                    ProfileMenuItem(
                                        icon = Icons.Default.Bolt,
                                        title = "Campus Pro",
                                        subtitle = if (subscription != null && subscription.isActive) {
                                            "🟡 Active - ${subscription.tierEnum.displayName}"
                                        } else {
                                            "Boost your listings & earn more"
                                        },
                                        onClick = onSubscriptionClick
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = onLogout,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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

    // =========================================================================
    // 3. DIALOG INPUT HANDLING & VALIDATION (Updated logic with Boolean check)
    // =========================================================================
    if (showDeleteConfirmation) {
        var validationErrorMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                deleteReason = ""
                validationErrorMessage = ""
            },
            title = {
                Text(
                    text = "Delete Listing",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to delete this listing? This action cannot be undone.",
                        fontSize = 14.sp,
                        color = EShopColors.White50
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Input processing block
                    OutlinedTextField(
                        value = deleteReason,
                        onValueChange = {
                            deleteReason = it
                            // FIX: Checked dynamically via the updated Boolean validator logic
                            validationErrorMessage = if (validator.isDeletionNotesValid(it)) {
                                ""
                            } else {
                                "Reason must be at least 3 characters"
                            }
                        },
                        label = { Text("Reason for deletion", color = EShopColors.White50) },
                        isError = validationErrorMessage.isNotEmpty(),
                        supportingText = {
                            if (validationErrorMessage.isNotEmpty()) {
                                Text(text = validationErrorMessage, color = EShopColors.Error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White50
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // FIX: Directly evaluates clean True/False status instead of buggy strings
                        if (validator.isDeletionNotesValid(deleteReason)) {
                            Log.d("EventLogger", "Form Submitted: Deleting Listing $selectedListingToDelete")
                            viewModel.deleteListing(selectedListingToDelete) {
                                showDeleteConfirmation = false
                                deleteReason = ""
                                viewModel.loadProfileData()
                            }
                        } else {
                            validationErrorMessage = "Please provide a valid reason."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Error)
                ) {
                    Text("Delete", color = EShopColors.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    deleteReason = ""
                    validationErrorMessage = ""
                }) {
                    Text("Cancel", color = EShopColors.Orange)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProfileHeader(
    userProfile: UserProfile,
    subscription: com.example.thikaeshop.data.models.Subscription? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(EShopColors.Orange, EShopColors.Gold)))
                // ========================================================
                // 4. LONG PRESS INTERACTION BADGE (From Week 8 Notes)
                // ========================================================
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Log.d("GestureHandler", "Displaying Avatar Context Menu for ID: ${userProfile.studentId}")
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👨‍🎓",
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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

        if (subscription != null && subscription.isActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(EShopColors.Orange.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Bolt,
                    contentDescription = "Campus Pro",
                    tint = EShopColors.Orange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "⚡ ${subscription.tierEnum.displayName} 🟡 Active",
                    fontSize = 10.sp,
                    color = EShopColors.Orange,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    MaterialTheme {
        ProfileScreen()
    }
}