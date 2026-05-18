package com.example.thikaeshop.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.PendingDelivery
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun PendingDeliveryCard(
    delivery: PendingDelivery,
    onAssign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = delivery.orderId,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EShopColors.Warning.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Pending",
                        fontSize = 10.sp,
                        color = EShopColors.Warning,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buyer Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(delivery.buyerName, fontSize = 12.sp, color = EShopColors.White)
                Text(" • ", fontSize = 12.sp, color = EShopColors.White50)
                Text(delivery.buyerPhone, fontSize = 12.sp, color = EShopColors.White50)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EShopColors.Gold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${delivery.deliveryLocation} - ${delivery.landmark}", fontSize = 12.sp, color = EShopColors.White50)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items
            Text("Items: ${delivery.items}", fontSize = 11.sp, color = EShopColors.White50)

            Spacer(modifier = Modifier.height(8.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KSh ${delivery.totalAmount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )

                Button(
                    onClick = onAssign,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange)
                ) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = "Assign", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Assign Rider", fontSize = 12.sp)
                }
            }
        }
    }
}