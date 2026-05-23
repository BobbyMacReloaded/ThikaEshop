package com.example.thikaeshop.ui.productDetails

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.ui.checkout.CheckoutScreen
import com.example.thikaeshop.ui.components.ProductImage
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String = "",
    onBackClick: () -> Unit = {},
    onBuyNowClick: () -> Unit = {},
    onContactSellerClick: (String, String) -> Unit = { _, _ -> },
    viewModel: ProductDetailViewModel = viewModel()
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var quantity by remember { mutableIntStateOf(1) }
    var showCheckout by remember { mutableStateOf(false) }

    // Load product when ID changes
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            viewModel.loadProduct(productId)
        }
    }

    if (showCheckout && product != null) {
        CheckoutScreen(
            productName = product!!.title,
            productPrice = product!!.price,
            quantity = quantity,
            isSecondHand = product!!.isSecondHand,
            onBackClick = {
                showCheckout = false
            },
            onOrderPlaced = {
                showCheckout = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Product Details",
                            color = EShopColors.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = EShopColors.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = EShopColors.White
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = EShopColors.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = EShopColors.DarkBg
                    )
                )
            },
            bottomBar = {
                if (product != null) {
                    BottomBar(
                        price = product!!.price,
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        onBuyNow = {
                            showCheckout = true
                            onBuyNowClick()
                        },
                        isSecondHand = product!!.isSecondHand
                    )
                }
            }
        ) { paddingValues ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EShopColors.Orange)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: $error", color = EShopColors.Error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadProduct(productId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                product != null -> {
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
                            .verticalScroll(rememberScrollState())
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Product Image
                        ProductImage(
                            imageUrl = product!!.imageUrl,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )

                        if (product!!.isSecondHand) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = EShopColors.Gold.copy(alpha = 0.15f)
                                )
                            ) {
                                Text(
                                    text = "🔄 Student Exchange - Second Hand Item",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.Gold,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = EShopColors.White10
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = product!!.title,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "KSh ${product!!.price}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.Gold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                HorizontalDivider(
                                    thickness = DividerDefaults.Thickness,
                                    color = EShopColors.White30
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Condition (for second-hand)
                                if (product!!.isSecondHand && product!!.condition.isNotBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EShopColors.Success,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Condition: ${product!!.condition}",
                                            fontSize = 14.sp,
                                            color = EShopColors.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Seller Info
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(EShopColors.Orange),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "👤",
                                            fontSize = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = product!!.sellerName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = EShopColors.White
                                        )
                                        Text(
                                            text = "${product!!.sellerRating} ★",
                                            fontSize = 11.sp,
                                            color = EShopColors.White50
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    OutlinedButton(
                                        onClick = {
                                            onContactSellerClick(
                                                product!!.sellerId,
                                                product!!.sellerName
                                            )
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Chat,
                                            contentDescription = "Chat",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Chat",
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                HorizontalDivider(
                                    thickness = DividerDefaults.Thickness,
                                    color = EShopColors.White30
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Description",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EShopColors.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product!!.description,
                                    fontSize = 14.sp,
                                    color = EShopColors.White50,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                HorizontalDivider(
                                    thickness = DividerDefaults.Thickness,
                                    color = EShopColors.White30
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = EShopColors.Gold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = product!!.location,
                                        fontSize = 14.sp,
                                        color = EShopColors.White
                                    )
                                }

                                if (product!!.landmark.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = "Landmark",
                                            tint = EShopColors.Gold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📍 ${product!!.landmark}",
                                            fontSize = 13.sp,
                                            color = EShopColors.White50
                                        )
                                    }
                                }

                                // Escrow info for second-hand
                                if (product!!.isSecondHand) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
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
                                                text = "✅ Payment held in escrow until you confirm receipt",
                                                fontSize = 11.sp,
                                                color = EShopColors.Gold
                                            )
                                        }
                                    }
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
fun BottomBar(
    price: Int,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onBuyNow: () -> Unit,
    isSecondHand: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
        color = EShopColors.DarkBg.copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Qty:",
                    color = EShopColors.White50,
                    fontSize = 14.sp
                )

                IconButton(
                    onClick = {
                        if (quantity > 1) {
                            onQuantityChange(quantity - 1)
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EShopColors.White10)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = EShopColors.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = {
                        onQuantityChange(quantity + 1)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EShopColors.White10)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = EShopColors.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Total: KSh ${price * quantity}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onBuyNow,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EShopColors.Orange
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "Buy Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductDetailScreen() {
    MaterialTheme {
        ProductDetailScreen()
    }
}