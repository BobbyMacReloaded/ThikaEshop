package com.example.thikaeshop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "likeScale"
    )

    Card(
        modifier = modifier
            .width(170.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = EShopColors.GlassWhite,
            contentColor = EShopColors.White
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(EShopColors.OrangeLight, EShopColors.Orange)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        // Orange placeholder while loading
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(EShopColors.OrangeLight, EShopColors.Orange)
                                    )
                                )
                        )
                    },
                    error = {
                        // Show icon if image fails to load
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(EShopColors.OrangeLight, EShopColors.Orange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ImageNotSupported,
                                contentDescription = "No image",
                                tint = EShopColors.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                )

                // Second-hand badge on top of image
                if (product.isSecondHand) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .background(EShopColors.Gold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🔄 Used",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.DarkBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = EShopColors.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.description ?: "",
                fontSize = 11.sp,
                color = EShopColors.White60,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KSh ${product.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )

                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isLiked) EShopColors.Orange else EShopColors.White40,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { isLiked = !isLiked }
                )
            }
        }
    }
}