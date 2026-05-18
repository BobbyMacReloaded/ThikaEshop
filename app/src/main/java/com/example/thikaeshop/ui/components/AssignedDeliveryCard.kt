package com.example.thikaeshop.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AssignedDelivery
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AssignedDeliveryCard(
    delivery: AssignedDelivery,
    modifier: Modifier = Modifier
) {
    val statusColor = when (delivery.status) {
        "assigned" -> EShopColors.Warning
        "picked" -> EShopColors.Orange
        "delivered" -> EShopColors.Success
        else -> EShopColors.White50
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = statusColor)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(delivery.orderId, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                Text("Rider: ${delivery.riderName}", fontSize = 12.sp, color = EShopColors.White50)
                Text("Assigned: ${delivery.assignedAt}", fontSize = 11.sp, color = EShopColors.White50)
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        delivery.status.uppercase(),
                        fontSize = 10.sp,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(12.dp))
                    Text(" ${delivery.estimatedDelivery}", fontSize = 10.sp, color = EShopColors.Gold)
                }
            }
        }
    }
}