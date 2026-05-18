package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.RiderApplication
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun RiderApplicationCard(
    application: RiderApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit,
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
                    .background(EShopColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = EShopColors.Gold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(application.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                Text(application.phone, fontSize = 12.sp, color = EShopColors.White50)
                Text("ID: ${application.idNumber}", fontSize = 11.sp, color = EShopColors.White50)
                Text("Experience: ${application.experience}", fontSize = 11.sp, color = EShopColors.Gold)
            }

            Row {
                IconButton(
                    onClick = onApprove,
                    modifier = Modifier
                        .size(36.dp)
                        .background(EShopColors.Success.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = EShopColors.Success)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onReject,
                    modifier = Modifier
                        .size(36.dp)
                        .background(EShopColors.Error.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject", tint = EShopColors.Error)
                }
            }
        }
    }
}