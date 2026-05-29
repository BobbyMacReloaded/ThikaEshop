package com.example.thikaeshop.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun ListingCard(
    listing: Product,  // ← Changed to Product
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("ListingCard","Image URL: ${listing.imageUrl}")
            // Product Image
            ProductImage(
                imageUrl = listing.imageUrl?:"",
                modifier = Modifier.size(50.dp)
            )

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
                    text = "KSh ${listing.price}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )
                Text(
                    text = "Status: ${if (listing.isAvailable) "Active" else "Sold"}",
                    fontSize = 11.sp,
                    color = if (listing.isAvailable) EShopColors.Success else EShopColors.White50
                )
            }

            // Action Buttons
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EShopColors.Gold)
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EShopColors.Error)
                }
            }
        }
    }
}