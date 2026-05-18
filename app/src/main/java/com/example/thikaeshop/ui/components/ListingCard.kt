package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.ListingItem
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun ListingCard(
    listing: ListingItem,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val statusColor = when (listing.status) {
        "Active" -> EShopColors.Success
        "Sold" -> EShopColors.Gold
        else -> EShopColors.White50
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EShopColors.Orange.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(listing.icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listing.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EShopColors.White
                )
                Text(
                    text = listing.price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )
            }

            // Status and Actions
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = listing.status,
                        fontSize = 10.sp,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = EShopColors.White50,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onEditClick() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = EShopColors.Error,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onDeleteClick() }
                    )
                }
            }
        }
    }
}