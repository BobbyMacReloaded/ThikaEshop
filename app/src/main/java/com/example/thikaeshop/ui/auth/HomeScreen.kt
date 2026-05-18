package com.example.thikaeshop.ui.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.components.SearchBar
import com.example.thikaeshop.ui.components.cards.ProductCard
import com.example.thikaeshop.ui.productDetails.ProductDetailScreen
import com.example.thikaeshop.ui.sell.SellScreen
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.HomeUiState
import com.example.thikaeshop.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit = {},
    onSellClick: () -> Unit = {},
    onPinDropClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onChatClick: () -> Unit = {},
    onChatWithSeller: (String, String) -> Unit = { _, _ -> },  // ← ADD THIS
    viewModel: HomeViewModel = HomeViewModel()
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val uiState by viewModel.uiState.collectAsState()

    var showSellScreen by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchText.text) {
        delay(500)
        viewModel.searchProducts(searchText.text)
    }

    if (showSellScreen) {
        SellScreen(
            onSubmit = { item ->
                showSellScreen = false
            },
            onBackClick = { showSellScreen = false }
        )
    }
    else if (selectedProductId != null) {
        ProductDetailScreen(
            productId = selectedProductId!!,
            onBackClick = { selectedProductId = null },
            onBuyNowClick = { /* Navigate to checkout */ },
            onContactSellerClick = { sellerId, sellerName ->
                onChatWithSeller(sellerId, sellerName)  // ← FIXED
            }
        )
    }
    else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onPinDropClick,
                    containerColor = EShopColors.Orange,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Pin Drop", tint = EShopColors.White)
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                    .padding(paddingValues)
                    .padding(16.dp)
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, Student! 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )
                        Text(
                            text = "Shop safely in Thika",
                            fontSize = 12.sp,
                            color = EShopColors.White50
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SearchBar(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = "Search products..."
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (uiState) {
                    is HomeUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = EShopColors.Orange)
                        }
                    }
                    is HomeUiState.Success -> {
                        val state = uiState as HomeUiState.Success

                        Text(
                            text = "Categories",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoryRow(onCategoryClick = { category ->
                            viewModel.filterByCategory(category)
                        })

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 Featured for You",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Text(
                                text = "See All",
                                fontSize = 12.sp,
                                color = EShopColors.Orange,
                                modifier = Modifier.clickable { }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.featuredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        selectedProductId = product.id
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Quick Actions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        QuickActionsRow(
                            onSellClick = { showSellScreen = true },
                            onChatClick = onChatClick
                        )
                    }
                    is HomeUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: ${(uiState as HomeUiState.Error).message}", color = EShopColors.Error)
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadProducts() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        "📚 Textbooks" to "Textbooks",
        "📱 Electronics" to "Electronics",
        "👕 Fashion" to "Fashion",
        "🏠 Household" to "Household",
        "🔄 Second Hand" to "SecondHand"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { (display, categoryId) ->
            Surface(
                modifier = Modifier.clickable { onCategoryClick(categoryId) },
                shape = RoundedCornerShape(28.dp),
                color = EShopColors.White10,
                border = BorderStroke(1.dp, EShopColors.White30)
            ) {
                Text(
                    text = display,
                    fontSize = 13.sp,
                    color = EShopColors.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onSellClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.Add,
            title = "Sell Item",
            subtitle = "Post your product",
            onClick = onSellClick,
            color = EShopColors.Orange,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Default.Receipt,
            title = "My Orders",
            subtitle = "Track deliveries",
            onClick = { },
            color = EShopColors.Gold,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Messages",
            subtitle = "Chat with buyers",
            onClick = onChatClick,
            color = EShopColors.Success,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = EShopColors.White
            )

            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = EShopColors.White50
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen()
    }
}