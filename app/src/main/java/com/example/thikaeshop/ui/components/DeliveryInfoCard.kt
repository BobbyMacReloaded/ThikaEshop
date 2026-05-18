package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.RiderInfo
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun DeliveryInfoCard(
    riderInfo: RiderInfo,
    onCallClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🚚 Delivery Rider",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EShopColors.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rider Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(EShopColors.Orange),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍🛵", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = riderInfo.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = EShopColors.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = " ${riderInfo.rating} ★",
                            fontSize = 11.sp,
                            color = EShopColors.White50
                        )
                    }
                }

                // Action Buttons
                Row {
                    IconButton(
                        onClick = onCallClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EShopColors.Success.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call",
                            tint = EShopColors.Success,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(18.dp))
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EShopColors.Orange.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = EShopColors.Orange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rider Current Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EShopColors.White10, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EShopColors.Gold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📍 ${riderInfo.currentLocation}",
                    fontSize = 11.sp,
                    color = EShopColors.White
                )
            }
        }
    }
}