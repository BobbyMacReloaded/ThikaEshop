package com.example.thikaeshop.ui.second_hand

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sort
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.components.ProductCard
import com.example.thikaeshop.ui.components.SearchBar
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.HomeUiState
import com.example.thikaeshop.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentExchangeScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onSellClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCondition by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Default") }
    var priceRange by remember { mutableStateOf("All Prices") }

    val uiState by viewModel.uiState.collectAsState()

    val conditions = listOf("All", "Like New", "Good", "Fair")
    val sortOptions = listOf("Default", "Price: Low to High", "Price: High to Low", "Rating: High to Low")
    val priceRanges = listOf("All Prices", "Under KSh 500", "KSh 500 - 2000", "Above KSh 2000")

    // Get products from ViewModel and filter for second-hand only
    val allProducts = when (uiState) {
        is HomeUiState.Success -> (uiState as HomeUiState.Success).products
        else -> emptyList()
    }

    // Step 1: Filter to show only SECOND-HAND items
    val secondHandOnly = allProducts.filter { it.isSecondHand }

    // Step 2: Apply price filter
    val priceFiltered = secondHandOnly.filter { product ->
        when (priceRange) {
            "Under KSh 500" -> product.price < 500
            "KSh 500 - 2000" -> product.price in 500..2000
            "Above KSh 2000" -> product.price > 2000
            else -> true
        }
    }

    // Step 3: Apply search filter
    val searchFiltered = priceFiltered.filter { product ->
        searchText.text.isEmpty() ||
                product.title.contains(searchText.text, ignoreCase = true) ||
                product.description.contains(searchText.text, ignoreCase = true)
    }

    // Step 4: Apply condition filter
    val conditionFiltered = searchFiltered.filter { product ->
        selectedCondition == "All" || product.condition.equals(selectedCondition, ignoreCase = true)
    }

    // Step 5: Apply sorting
    val sortedProducts = when (sortOption) {
        "Price: Low to High" -> conditionFiltered.sortedBy { it.price }
        "Price: High to Low" -> conditionFiltered.sortedByDescending { it.price }
        "Rating: High to Low" -> conditionFiltered.sortedByDescending { it.sellerRating }
        else -> conditionFiltered
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Exchange",
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
                    IconButton(onClick = onSellClick) {
                        Icon(Icons.Default.Add, contentDescription = "Sell", tint = EShopColors.Gold)
                    }
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = EShopColors.Gold)
                    }
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
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EShopColors.Orange)
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${(uiState as HomeUiState.Error).message}", color = EShopColors.Error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProducts() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                        .padding(paddingValues)
                ) {
                    // Escrow Info Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = EShopColors.Gold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔒 Escrow Protection: Payment held until you confirm receipt",
                                fontSize = 11.sp,
                                color = EShopColors.Gold
                            )
                        }
                    }

                    // Search Bar
                    SearchBar(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = "Search second-hand items...",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Condition Filter Chips
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conditions) { condition ->
                            FilterChip(
                                selected = selectedCondition == condition,
                                onClick = { selectedCondition = condition },
                                label = { Text(condition, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EShopColors.Orange,
                                    containerColor = EShopColors.White10,
                                    selectedLabelColor = EShopColors.White,
                                    labelColor = EShopColors.White50
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Filters Bar
                    if (priceRange != "All Prices" || sortOption != "Default" || selectedCondition != "All") {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (priceRange != "All Prices") {
                                AssistChip(
                                    onClick = { priceRange = "All Prices" },
                                    label = { Text("💰 $priceRange", fontSize = 11.sp) },
                                    leadingIcon = { Text("✖️", fontSize = 10.sp) }
                                )
                            }
                            if (sortOption != "Default") {
                                AssistChip(
                                    onClick = { sortOption = "Default" },
                                    label = { Text("📊 $sortOption", fontSize = 11.sp) },
                                    leadingIcon = { Text("✖️", fontSize = 10.sp) }
                                )
                            }
                            if (selectedCondition != "All") {
                                AssistChip(
                                    onClick = { selectedCondition = "All" },
                                    label = { Text("🏷️ Condition: $selectedCondition", fontSize = 11.sp) },
                                    leadingIcon = { Text("✖️", fontSize = 10.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Products Count
                    Text(
                        text = "Showing ${sortedProducts.size} second-hand items",
                        fontSize = 12.sp,
                        color = EShopColors.White50,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Products Grid
                    if (sortedProducts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔄", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No second-hand items found",
                                    fontSize = 16.sp,
                                    color = EShopColors.White50
                                )
                                Text(
                                    text = "Be the first to sell your used items!",
                                    fontSize = 12.sp,
                                    color = EShopColors.White30
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onSellClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sell an Item")
                                }
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
                            items(sortedProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sort Dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Sort Products", color = EShopColors.White) },
            text = {
                Column {
                    sortOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sortOption = option
                                    showSortDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = sortOption == option,
                                onClick = {
                                    sortOption = option
                                    showSortDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange)
                            )
                            Text(option, color = EShopColors.White, modifier = Modifier.padding(start = 8.dp))
                        }
                        if (option != sortOptions.last()) {
                            HorizontalDivider(color = EShopColors.White20)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel", color = EShopColors.Orange)
                }
            },
            containerColor = EShopColors.DarkCard
        )
    }

    // Price Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Price", color = EShopColors.White) },
            text = {
                Column {
                    priceRanges.forEach { range ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    priceRange = range
                                    showFilterDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = priceRange == range,
                                onClick = {
                                    priceRange = range
                                    showFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = EShopColors.Orange)
                            )
                            Text(range, color = EShopColors.White, modifier = Modifier.padding(start = 8.dp))
                        }
                        if (range != priceRanges.last()) {
                            HorizontalDivider(color = EShopColors.White20)
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
fun PreviewStudentExchangeScreen() {
    MaterialTheme {
        StudentExchangeScreen()
    }
}