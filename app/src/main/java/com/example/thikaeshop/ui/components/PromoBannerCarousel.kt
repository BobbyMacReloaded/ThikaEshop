package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.SellerBanner
import com.example.thikaeshop.ui.theme.EShopColors

/**
 * Homepage banner carousel — only shows banners from Pro+ subscribers.
 * Place this in HomeScreen between the search bar and Categories section.
 */
@Composable
fun PromoBannerCarousel(
    banners: List<SellerBanner>,
    onBannerClick: (SellerBanner) -> Unit
) {
    if (banners.isEmpty()) return

    Column {
        Text(
            "✨ Featured Sellers",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EShopColors.White50,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(banners) { banner ->
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EShopColors.DarkCard),
                    onClick = { onBannerClick(banner) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(EShopColors.Orange, EShopColors.Gold)
                                )
                            )
                    ) {
                        if (!banner.bannerImageUrl.isNullOrEmpty()) {
                            ProductImage(
                                imageUrl = banner.bannerImageUrl,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)
                                )))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                banner.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Text(
                                banner.subtitle,
                                fontSize = 11.sp,
                                color = EShopColors.White80
                            )
                        }
                    }
                }
            }
        }
    }
}
