package com.example.thikaeshop.ui.productDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.SampleData
import com.example.thikaeshop.ui.checkout.CheckoutScreen
import com.example.thikaeshop.ui.theme.EShopColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String = "",
    onBackClick: () -> Unit = {},
    onBuyNowClick: () -> Unit = {},
    onContactSellerClick: (String, String) -> Unit = { _, _ -> }
) {

    val product = SampleData(productId)

    var quantity by remember { mutableIntStateOf(1) }
    var showCheckout by remember { mutableStateOf(false) }

    if (showCheckout) {

        CheckoutScreen(
            productName = product.title,
            productPrice = product.price,
            quantity = quantity,
            isSecondHand = product.isSecondHand,
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

                BottomBar(
                    price = product.price,
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    onBuyNow = {
                        showCheckout = true
                        onBuyNowClick()
                    },
                    isSecondHand = product.isSecondHand
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
                                EShopColors.LightBg
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // Product Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            EShopColors.Orange.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = product.icon,
                            fontSize = 80.sp
                        )

                        Text(
                            text = "Product Image",
                            fontSize = 12.sp,
                            color = EShopColors.White50
                        )
                    }

                    if (product.isSecondHand) {

                        Card(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),

                            shape = RoundedCornerShape(8.dp),

                            colors = CardDefaults.cardColors(
                                containerColor = EShopColors.Gold
                            )
                        ) {

                            Text(
                                text = "🔄 Student Exchange",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.DarkBg,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
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
                            text = product.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Text(
                                text = "KSh ${product.price}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.Gold
                            )

                            product.originalPrice?.let {

                                Text(
                                    text = "KSh $it",
                                    fontSize = 14.sp,
                                    color = EShopColors.White50,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            thickness = DividerDefaults.Thickness,
                            color = EShopColors.White30
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        product.condition?.let {

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
                                    text = "Condition: $it",
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
                                    text = product.sellerIcon,
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = product.sellerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EShopColors.White
                                )

                                Text(
                                    text = "${product.sellerRating} ★ (${product.sellerReviews} reviews)",
                                    fontSize = 11.sp,
                                    color = EShopColors.White50
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            OutlinedButton(
                                onClick = {
                                    onContactSellerClick(
                                        product.sellerId,
                                        product.sellerName
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
                            text = product.description,
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
                                text = product.location,
                                fontSize = 14.sp,
                                color = EShopColors.White
                            )
                        }

                        product.landmark?.let {

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
                                    text = "📍 $it",
                                    fontSize = 13.sp,
                                    color = EShopColors.White50
                                )
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