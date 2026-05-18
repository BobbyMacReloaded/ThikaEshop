package com.example.thikaeshop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.LandmarkItem
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun LandmarkCard(
    landmark: LandmarkItem,
    onDeleteClick: () -> Unit = {},
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
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = EShopColors.Gold,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)  // Fixed: removed parentheses
            ) {
                Text(
                    text = landmark.area,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EShopColors.White
                )
                Text(
                    text = landmark.description,
                    fontSize = 12.sp,
                    color = EShopColors.White50
                )
                Text(
                    text = landmark.timesUsed,
                    fontSize = 10.sp,
                    color = EShopColors.Gold
                )
            }

            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = EShopColors.Error,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDeleteClick() }
            )
        }
    }
}