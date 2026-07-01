package com.example.thikaeshop

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.admin.AdminPanelScreen
import com.example.thikaeshop.ui.auth.HomeScreen
import com.example.thikaeshop.ui.auth.LoginScreen
import com.example.thikaeshop.ui.chat.ChatDetailScreen
import com.example.thikaeshop.ui.chat.ChatListScreen
import com.example.thikaeshop.ui.components.BottomNavBar
import com.example.thikaeshop.ui.market_place.MarketplaceScreen
import com.example.thikaeshop.ui.orders.OrdersScreen
import com.example.thikaeshop.ui.ordertracking.OrderTrackingScreen
import com.example.thikaeshop.ui.pindrop.PinDropScreen
import com.example.thikaeshop.ui.productDetails.ProductDetailScreen
import com.example.thikaeshop.ui.profile.EditProfileScreen
import com.example.thikaeshop.ui.profile.ProfileScreen
import com.example.thikaeshop.ui.rating.RatingScreen
import com.example.thikaeshop.ui.second_hand.StudentExchangeScreen
import com.example.thikaeshop.ui.sell.SellScreen
import com.example.thikaeshop.ui.subscription.SubscriptionScreen
import com.example.thikaeshop.ui.theme.ThikaEshopTheme
import com.example.thikaeshop.ui.verification.StudentVerificationScreen
import com.example.thikaeshop.ui.viewmodels.*
import com.example.thikaeshop.utils.ChatHelper
import com.example.thikaeshop.utils.SimplePrefs
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    lateinit var simplePrefs: SimplePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        simplePrefs = SimplePrefs(this)
        setContent {
            ThikaEshopTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var showAdminPanel by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableIntStateOf(0) }

                // ViewModels
                val studentVerificationViewModel: StudentVerificationViewModel = viewModel()
                val homeViewModel: HomeViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()
                val loginViewModel: LoginViewModel = viewModel()
                val subscriptionViewModel: SubscriptionViewModel = viewModel()

                // Navigation States
                var showVerification by remember { mutableStateOf(false) }
                var showEditProfile by remember { mutableStateOf(false) }
                var showOrderTracking by remember { mutableStateOf(false) }
                var showRating by remember { mutableStateOf(false) }
                var showSellScreen by remember { mutableStateOf(false) }
                var showSubscription by remember { mutableStateOf(false) }
                var selectedOrderId by remember { mutableStateOf("") }
                var selectedProductName by remember { mutableStateOf("") }
                var selectedProductIcon by remember { mutableStateOf("") }
                var selectedProductId by remember { mutableStateOf<String?>(null) }
                var showChatList by remember { mutableStateOf(false) }
                var showChatDetail by remember { mutableStateOf(false) }
                var selectedChatId by remember { mutableStateOf("") }
                var selectedChatName by remember { mutableStateOf("") }
                var selectedSellerId by remember { mutableStateOf("") }  // ← ADDED: Store seller ID for role detection
                val coroutineScope = rememberCoroutineScope()
                var showPinDrop by remember { mutableStateOf(false) }

                // ============================================================
                // NAVIGATION LOGIC
                // ============================================================
                if (showAdminPanel) {
                    AdminPanelScreen(
                        onBackClick = {
                            showAdminPanel = false
                            loginViewModel.resetState()
                            isLoggedIn = false
                        },
                        onLogout = {
                            showAdminPanel = false
                            loginViewModel.resetState()
                            isLoggedIn = false
                        }
                    )
                }
                else if (showSellScreen) {
                    SellScreen(
                        onSubmit = {
                            showSellScreen = false
                            homeViewModel.loadProducts()
                            homeViewModel.loadRecommendations()
                        },
                        onBackClick = { showSellScreen = false },
                        onNavigateToSubscription = {
                            showSellScreen = false
                            showSubscription = true
                        }
                    )
                }
                else if (showSubscription) {
                    SubscriptionScreen(
                        onBackClick = { showSubscription = false },
                        onSelectTier = { tier ->
                            Toast.makeText(
                                this@MainActivity,
                                "Selected ${tier.displayName} for KSh ${tier.priceKsh}/month",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        viewModel = subscriptionViewModel
                    )
                }
                else if (showChatList) {
                    ChatListScreen(
                        onBackClick = { showChatList = false },
                        onChatClick = { chatId, name, otherUserId ->  // ← UPDATED: Added otherUserId parameter
                            Log.d("MainActivity", "🟢 Chat clicked: $chatId, $name, $otherUserId")
                            selectedChatId = chatId
                            selectedChatName = name
                            selectedSellerId = otherUserId  // ← SET seller ID from chat
                            showChatDetail = true
                            showChatList = false
                        }
                    )
                }
                else if (showPinDrop) {
                    PinDropScreen(
                        onLocationSelected = { lat, lng, landmark ->
                            showPinDrop = false
                        },
                        onBackClick = { showPinDrop = false }
                    )
                }
                else if (showChatDetail) {
                    ChatDetailScreen(
                        chatId = selectedChatId,
                        receiverName = selectedChatName,
                        sellerId = selectedSellerId,  // ← UPDATED: Pass sellerId instead of userRole
                        onBackClick = {
                            Log.d("MainActivity", "🔙 Back from chat")
                            showChatDetail = false
                            showChatList = true
                        }
                    )
                }
                else if (selectedProductId != null) {
                    ProductDetailScreen(
                        productId = selectedProductId!!,
                        onBackClick = {
                            selectedProductId = null
                        },
                        onBuyNowClick = {
                            // Navigate to checkout
                        },
                        onContactSellerClick = { sellerId, sellerName ->
                            Log.d("MainActivity", "🟢 Contact Seller Clicked")
                            Log.d("MainActivity", "   sellerId: '$sellerId'")
                            Log.d("MainActivity", "   sellerName: '$sellerName'")
                            Log.d("MainActivity", "   Current User: ${FirebaseAuth.getInstance().currentUser?.uid}")

                            coroutineScope.launch {
                                try {
                                    // Check if user is logged in
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    if (currentUser == null) {
                                        Log.e("MainActivity", "❌ User not logged in")
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Please login first to chat with seller",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }

                                    // Check if sellerId is valid
                                    if (sellerId.isEmpty()) {
                                        Log.e("MainActivity", "❌ Seller ID is empty")
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Seller information not available",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }

                                    // Check if sellerId equals current user ID
                                    if (sellerId == currentUser.uid) {
                                        Log.e("MainActivity", "❌ Cannot chat with yourself!")
                                        Toast.makeText(
                                            this@MainActivity,
                                            "You cannot chat with yourself",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }

                                    // Get or create chat
                                    Log.d("MainActivity", "🔄 Calling ChatHelper.getOrCreateChat...")
                                    val chatId = ChatHelper.getOrCreateChat(sellerId, sellerName)
                                    Log.d("MainActivity", "🟢 Chat ID returned: '$chatId'")

                                    if (chatId.isNotEmpty()) {
                                        // ====== NAVIGATION ======
                                        selectedChatId = chatId
                                        selectedChatName = sellerName
                                        selectedSellerId = sellerId  // ← ADDED: Store seller ID
                                        selectedProductId = null
                                        showChatDetail = true

                                        Log.d("MainActivity", "✅ Navigated to chat: $chatId with seller: $sellerId")
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Chat opened successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.e("MainActivity", "❌ ChatHelper returned empty chatId")

                                        // ====== CHECK WHY IT FAILED ======
                                        try {
                                            val directChatId = ChatHelper.getChatId(currentUser.uid, sellerId)
                                            Log.d("MainActivity", "🔍 Generated chatId: '$directChatId'")

                                            val chatExists = ChatHelper.chatExists(directChatId)
                                            Log.d("MainActivity", "🔍 Chat exists in Firestore: $chatExists")

                                            if (chatExists) {
                                                Log.d("MainActivity", "🔄 Forcing navigation to existing chat")
                                                selectedChatId = directChatId
                                                selectedChatName = sellerName
                                                selectedSellerId = sellerId  // ← ADDED: Store seller ID
                                                selectedProductId = null
                                                showChatDetail = true
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Could not create chat. Please try again.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "❌ Error checking chat: ${e.message}", e)
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Error: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "❌ Error in chat creation", e)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                }
                else if (isLoggedIn) {
                    if (showVerification) {
                        StudentVerificationScreen(
                            onBackClick = { showVerification = false },
                            onVerificationComplete = {
                                showVerification = false
                            },
                            viewModel = studentVerificationViewModel
                        )
                    }
                    else if (showRating) {
                        RatingScreen(
                            orderId = selectedOrderId,
                            productName = selectedProductName,
                            productIcon = selectedProductIcon,
                            onBackClick = { showRating = false },
                            onSubmitSuccess = {
                                showRating = false
                            }
                        )
                    }
                    else if (showOrderTracking) {
                        OrderTrackingScreen(
                            orderId = selectedOrderId,
                            onBackClick = { showOrderTracking = false },
                            onNavigateToRating = {
                                showOrderTracking = false
                                showRating = true
                            }
                        )
                    }
                    else {
                        Scaffold(
                            bottomBar = {
                                BottomNavBar(
                                    onItemSelected = { selectedTab = it }
                                )
                            }
                        ) { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                when (selectedTab) {
                                    0 -> HomeScreen(
                                        onProductClick = { productId ->
                                            selectedProductId = productId
                                        },
                                        onPinDropClick = { showPinDrop = true },
                                        onCategoryClick = { },
                                        onProfileClick = { selectedTab = 4 },
                                        onSeeAllClick = { selectedTab = 2 },
                                        onChatClick = { showChatList = true },
                                        onChatWithSeller = { sellerId, sellerName ->
                                            coroutineScope.launch {
                                                try {
                                                    val chatId = ChatHelper.getOrCreateChat(sellerId, sellerName)
                                                    if (chatId.isNotEmpty()) {
                                                        selectedChatId = chatId
                                                        selectedChatName = sellerName
                                                        selectedSellerId = sellerId  // ← ADDED: Store seller ID
                                                        showChatDetail = true
                                                    } else {
                                                        Log.e("MainActivity", "Could not create chat with seller $sellerId")
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("MainActivity", "onChatWithSeller error: ${e.message}")
                                                }
                                            }
                                        },
                                        viewModel = homeViewModel
                                    )
                                    1 -> StudentExchangeScreen(
                                        onBackClick = { selectedTab = 0 },
                                        onProductClick = { productId ->
                                            selectedProductId = productId
                                        },
                                        onSellClick = { showSellScreen = true }
                                    )
                                    2 -> MarketplaceScreen(
                                        onBackClick = { selectedTab = 0 },
                                        onProductClick = { productId ->
                                            selectedProductId = productId
                                        }
                                    )
                                    3 -> {
                                        OrdersScreen(
                                            onBackClick = { selectedTab = 0 },
                                            onOrderClick = { orderId ->
                                                selectedOrderId = orderId
                                                selectedProductName = "Programming Textbook"
                                                selectedProductIcon = "📚"
                                                showOrderTracking = true
                                            },
                                            onRateOrder = { orderId, productName, productIcon ->
                                                selectedOrderId = orderId
                                                selectedProductName = productName
                                                selectedProductIcon = productIcon
                                                showRating = true
                                            }
                                        )
                                    }
                                    4 -> {
                                        LaunchedEffect(selectedTab) {
                                            if (selectedTab == 4) {
                                                profileViewModel.loadProfileData()
                                            }
                                        }

                                        if (showEditProfile) {
                                            EditProfileScreen(
                                                onBackClick = { showEditProfile = false },
                                                onSaveComplete = {
                                                    showEditProfile = false
                                                    profileViewModel.loadProfileData()
                                                }
                                            )
                                        } else {
                                            ProfileScreen(
                                                onBackClick = { selectedTab = 0 },
                                                onSubscriptionClick = { showSubscription = true },
                                                onLogout = {
                                                    FirebaseAuth.getInstance().signOut()
                                                    loginViewModel.clearAll()
                                                    profileViewModel.clearData()
                                                    homeViewModel.clearData()
                                                    isLoggedIn = false
                                                    selectedTab = 0
                                                    showEditProfile = false
                                                    showVerification = false
                                                    showSellScreen = false
                                                    showChatList = false
                                                    showChatDetail = false
                                                    showPinDrop = false
                                                    showOrderTracking = false
                                                    showRating = false
                                                    selectedProductId = null
                                                    selectedOrderId = ""
                                                    selectedSellerId = ""  // ← ADDED: Clear seller ID on logout
                                                },
                                                onVerificationClick = { showVerification = true },
                                                onEditProfileClick = { showEditProfile = true },
                                                onSellClick = { showSellScreen = true }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onAdminSuccess = {
                            isLoggedIn = true
                            showAdminPanel = true
                            profileViewModel.loadProfileData()
                        },
                        onGuestSuccess = { isLoggedIn = true },
                        viewModel = loginViewModel,
                        simplePrefs = simplePrefs
                    )
                }
            }
        }
    }
}