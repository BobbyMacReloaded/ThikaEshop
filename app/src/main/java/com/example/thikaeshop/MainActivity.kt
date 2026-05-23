package com.example.thikaeshop

import android.os.Bundle
import android.util.Log
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
import com.example.thikaeshop.ui.ordertracking.OrderTrackingScreen
import com.example.thikaeshop.ui.orders.OrdersScreen
import com.example.thikaeshop.ui.pindrop.PinDropScreen
import com.example.thikaeshop.ui.productDetails.ProductDetailScreen
import com.example.thikaeshop.ui.profile.EditProfileScreen
import com.example.thikaeshop.ui.profile.ProfileScreen
import com.example.thikaeshop.ui.rating.RatingScreen
import com.example.thikaeshop.ui.second_hand.StudentExchangeScreen
import com.example.thikaeshop.ui.sell.SellScreen
import com.example.thikaeshop.ui.theme.ThikaEshopTheme
import com.example.thikaeshop.ui.verification.StudentVerificationScreen
import com.example.thikaeshop.ui.viewmodels.*
import com.example.thikaeshop.utils.ChatHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

                // Navigation states
                var showVerification by remember { mutableStateOf(false) }
                var showEditProfile by remember { mutableStateOf(false) }
                var showOrderTracking by remember { mutableStateOf(false) }
                var showRating by remember { mutableStateOf(false) }
                var showSellScreen by remember { mutableStateOf(false) }
                var selectedOrderId by remember { mutableStateOf("") }
                var selectedProductName by remember { mutableStateOf("") }
                var selectedProductIcon by remember { mutableStateOf("") }
                var selectedProductId by remember { mutableStateOf<String?>(null) }
                var showChatList by remember { mutableStateOf(false) }
                var showChatDetail by remember { mutableStateOf(false) }
                var selectedChatId by remember { mutableStateOf("") }
                var selectedChatName by remember { mutableStateOf("") }
                val coroutineScope = rememberCoroutineScope()
                var showPinDrop by remember { mutableStateOf(false) }

                // IMPORTANT: Order matters!
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
                        },
                        onBackClick = { showSellScreen = false }
                    )
                }
                else if (showChatList) {
                    ChatListScreen(
                        onBackClick = { showChatList = false },
                        onChatClick = { chatId, name ->
                            selectedChatId = chatId
                            selectedChatName = name
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
                        onBackClick = {
                            showChatDetail = false
                            showChatList = true
                        }
                    )
                }
                else if (selectedProductId != null) {
                    ProductDetailScreen(
                        productId = selectedProductId!!,
                        onBackClick = { selectedProductId = null },
                        onBuyNowClick = { /* Navigate to checkout */ },
                        onContactSellerClick = { sellerId, sellerName ->
                            coroutineScope.launch {
                                try {
                                    val chatId = ChatHelper.getOrCreateChat(sellerId, sellerName)
                                    if (chatId.isNotEmpty()) {
                                        selectedChatId = chatId
                                        selectedChatName = sellerName
                                        showChatDetail = true
                                        selectedProductId = null
                                    } else {
                                        Log.e("MainActivity", "Failed to create chat")
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error: ${e.message}")
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
                                        onSellClick = { showSellScreen = true },
                                        onPinDropClick = { showPinDrop = true },
                                        onCategoryClick = { },
                                        onChatClick = { showChatList = true },
                                        onChatWithSeller = { sellerId, sellerName ->
                                            coroutineScope.launch {
                                                try {
                                                    val chatId = ChatHelper.getOrCreateChat(sellerId, sellerName)
                                                    if (chatId.isNotEmpty()) {
                                                        selectedChatId = chatId
                                                        selectedChatName = sellerName
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
                                        if (showOrderTracking) {
                                            OrderTrackingScreen(
                                                orderId = selectedOrderId,
                                                onBackClick = { showOrderTracking = false },
                                                onNavigateToRating = {
                                                    showOrderTracking = false
                                                    showRating = true
                                                }
                                            )
                                        } else {
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
                                    }
                                    4 -> {
                                        if (showEditProfile) {
                                            EditProfileScreen(
                                                onBackClick = { showEditProfile = false },
                                                onSaveComplete = {
                                                    showEditProfile = false
                                                }
                                            )
                                        } else {
                                            ProfileScreen(
                                                onBackClick = { selectedTab = 0 },
                                                onLogout = {
                                                    isLoggedIn = false
                                                    selectedTab = 0
                                                },
                                                onVerificationClick = { showVerification = true },
                                                onEditProfileClick = { showEditProfile = true }
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
                        },
                        onGuestSuccess = { isLoggedIn = true },
                        viewModel = loginViewModel
                    )
                }
            }
        }
    }
}