package com.example.thikaeshop.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.ReportedItem
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun ReportedItemCard(
    report: ReportedItem,
    onDismiss: () -> Unit,
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
                    .background(EShopColors.Error.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Report, contentDescription = null, tint = EShopColors.Error)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(report.productName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                Text("Seller: ${report.seller}", fontSize = 12.sp, color = EShopColors.White50)
                Text("Reported by: ${report.reportedBy}", fontSize = 11.sp, color = EShopColors.White50)
                Text("Reason: ${report.reason}", fontSize = 11.sp, color = EShopColors.Error)
            }

            Column {
                if (report.status == "Pending") {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Success.copy(alpha = 0.15f))
                    ) {
                        Text("Dismiss", fontSize = 11.sp, color = EShopColors.Success)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onRemove,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Error.copy(alpha = 0.15f))
                    ) {
                        Text("Remove", fontSize = 11.sp, color = EShopColors.Error)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = EShopColors.Success.copy(alpha = 0.15f)
                    ) {
                        Text("Reviewed", fontSize = 11.sp, color = EShopColors.Success, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}