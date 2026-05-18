package com.example.thikaeshop.ui.second_hand

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.sampleSecondHandItems
import com.example.thikaeshop.ui.components.SearchBar
import com.example.thikaeshop.ui.components.SecondHandCard
import com.example.thikaeshop.ui.theme.EShopColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentExchangeScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onSellClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedCondition by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Textbooks", "Electronics", "Household")
    val conditions = listOf("All", "Like New", "Good", "Fair")

    // Filter items
    val filteredItems = sampleSecondHandItems.filter { item ->
        val matchesSearch = searchText.text.isEmpty() ||
                item.title.contains(searchText.text, ignoreCase = true) ||
                item.description.contains(searchText.text, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        val matchesCondition = selectedCondition == "All" || item.condition == selectedCondition
        matchesSearch && matchesCategory && matchesCondition
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
                .background(
                    Brush.verticalGradient(
                        listOf(
                            EShopColors.DarkBg,
                            EShopColors.DarkCard
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Search second-hand items...",
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

            // Escrow Info Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Products Grid
            if (filteredItems.isEmpty()) {
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
                    items(filteredItems) { item ->
                        SecondHandCard(
                            item = item,
                            onClick = { onProductClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}