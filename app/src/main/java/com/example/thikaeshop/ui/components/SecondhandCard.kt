package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.SecondHandItem
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun SecondHandCard(
    item: SecondHandItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Icon and Condition Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(EShopColors.Orange.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 48.sp)

                // Condition badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            when (item.condition) {
                                "Like New" -> EShopColors.Success
                                "Good" -> EShopColors.Gold
                                "Fair" -> EShopColors.Orange
                                else -> EShopColors.White40
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.condition,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.DarkBg
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = EShopColors.White,
                maxLines = 1
            )

            // Price
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "KSh ${item.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )
                item.originalPrice?.let {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "KSh $it",
                        fontSize = 10.sp,
                        color = EShopColors.White50,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Seller info
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.sellerVerified) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = EShopColors.Success,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Text(
                    text = item.sellerName,
                    fontSize = 10.sp,
                    color = EShopColors.White50
                )
                Text(
                    text = " ★ ${item.sellerRating}",
                    fontSize = 10.sp,
                    color = EShopColors.Gold
                )
            }

            // Location with landmark
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EShopColors.Gold,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = item.landmark.take(20) + if (item.landmark.length > 20) "..." else "",
                    fontSize = 9.sp,
                    color = EShopColors.White50,
                    maxLines = 1
                )
            }
        }
    }
}