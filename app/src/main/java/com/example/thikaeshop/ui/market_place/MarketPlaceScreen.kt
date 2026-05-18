package com.example.thikaeshop.ui.market_place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.components.SearchBar
import com.example.thikaeshop.ui.components.cards.ProductCard
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.data.models.Products

// Sample products list
val sampleProducts = listOf(
    Products(
        id = "1",
        title = "Programming Textbook - Kotlin",
        price = 450,
        originalPrice = 1200,
        description = "Like new condition. No highlights or marks.",
        icon = "📚",
        sellerId = "seller_001",
        sellerName = "John Kimani",
        sellerIcon = "👨‍🎓",
        sellerRating = 4.8,
        sellerReviews = 23,
        location = "Landless, Thika",
        landmark = "Near Blue Water Tank",
        condition = "Like New",
        isSecondHand = true
    ),
    Products(
        id = "2",
        title = "Fresh Vegetables",
        price = 250,
        originalPrice = null,
        description = "Fresh from local farm",
        icon = "🥬",
        sellerId = "seller_002",
        sellerName = "Local Vendor",
        sellerIcon = "🛒",
        sellerRating = 4.5,
        sellerReviews = 12,
        location = "Kiandutu, Thika",
        landmark = "Next to Shop",
        condition = null,
        isSecondHand = false
    ),
    Products(
        id = "3",
        title = "Bed Sheet Set",
        price = 1800,
        originalPrice = 3000,
        description = "Single size, good condition",
        icon = "🛏️",
        sellerId = "seller_003",
        sellerName = "Peter Otieno",
        sellerIcon = "👨‍🎓",
        sellerRating = 4.3,
        sellerReviews = 8,
        location = "Landless, Thika",
        landmark = "Behind Hostel",
        condition = "Good",
        isSecondHand = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Get all products (for marketplace - show both new and second-hand or just new goods)
    val allProducts = sampleProducts

    // Filter products based on search and category
    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = searchText.text.isEmpty() ||
                product.title.contains(searchText.text, ignoreCase = true) ||
                product.description.contains(searchText.text, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" ||
                (selectedCategory == "Food" && product.icon == "🥬") ||
                (selectedCategory == "Electronics" && product.icon == "📱") ||
                (selectedCategory == "Household" && product.icon == "🛏️") ||
                (selectedCategory == "Fashion")
        matchesSearch && matchesCategory
    }

    val categories = listOf("All", "Food", "Electronics", "Household", "Fashion")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Marketplace",
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
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = EShopColors.Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Search new goods...",
                modifier = Modifier.padding(16.dp)
            )

            // Category Chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EShopColors.Orange,
                            containerColor = EShopColors.White10,
                            selectedLabelColor = EShopColors.White,
                            labelColor = EShopColors.White50
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛍️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No products found",
                            fontSize = 16.sp,
                            color = EShopColors.White50
                        )
                        Text(
                            text = "Try adjusting your search",
                            fontSize = 12.sp,
                            color = EShopColors.White30
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Price", color = EShopColors.White) },
            text = {
                Column {
                    listOf("All Prices", "Under KSh 500", "KSh 500 - 2000", "Above KSh 2000").forEach { range ->
                        TextButton(
                            onClick = {
                                // Apply price filter
                                showFilterDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(range, color = EShopColors.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cancel", color = EShopColors.Orange)
                }
            },
            containerColor = EShopColors.DarkCard
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMarketplaceScreen() {
    MaterialTheme {
        MarketplaceScreen()
    }
}