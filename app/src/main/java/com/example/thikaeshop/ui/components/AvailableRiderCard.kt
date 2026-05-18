package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AvailableRider
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AvailableRiderCard(
    rider: AvailableRider,
    onSelect: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EShopColors.Orange.copy(alpha = 0.15f) else EShopColors.White10
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EShopColors.Orange) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rider Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(EShopColors.Gold.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rider Info
            Column(modifier = Modifier.weight(1f)) {
                Text(rider.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(12.dp))
                    Text(" ${rider.rating} ★", fontSize = 11.sp, color = EShopColors.White50)
                    Text(" • ", fontSize = 11.sp, color = EShopColors.White50)
                    Text(rider.currentLocation, fontSize = 11.sp, color = EShopColors.White50)
                }
                if (rider.isAvailable) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EShopColors.Success)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Available", fontSize = 10.sp, color = EShopColors.Success)
                    }
                }
            }

            // Selection Indicator
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = EShopColors.Success)
            } else {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EShopColors.Orange)
                ) {
                    Text("Select", fontSize = 11.sp)
                }
            }
        }
    }
}