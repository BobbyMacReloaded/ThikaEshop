package com.example.thikaeshop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AdminProduct
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AdminProductCard(
    product: AdminProduct,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    .background(EShopColors.Orange.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = EShopColors.Orange)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    if (product.reported) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Report, contentDescription = "Reported", tint = EShopColors.Error, modifier = Modifier.size(14.dp))
                    }
                }
                Text("Seller: ${product.seller}", fontSize = 12.sp, color = EShopColors.White50)
                Text("KSh ${product.price}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
                if (product.isSecondHand) {
                    Text("🔄 Student Exchange", fontSize = 10.sp, color = EShopColors.Gold)
                }
            }

            Button(
                onClick = onRemove,
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Error.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = EShopColors.Error, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove", fontSize = 11.sp, color = EShopColors.Error)
            }
        }
    }
}