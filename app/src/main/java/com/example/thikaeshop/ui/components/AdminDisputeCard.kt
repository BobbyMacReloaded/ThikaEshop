package com.example.thikaeshop.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AdminDispute
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AdminDisputeCard(
    dispute: AdminDispute,
    onResolve: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dispute #${dispute.id}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (dispute.status) {
                        "Pending" -> EShopColors.Warning.copy(alpha = 0.15f)
                        "In Review" -> EShopColors.Orange.copy(alpha = 0.15f)
                        else -> EShopColors.Success.copy(alpha = 0.15f)
                    }
                ) {
                    Text(dispute.status, fontSize = 10.sp, color = EShopColors.Warning, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Order: ${dispute.orderId}", fontSize = 12.sp, color = EShopColors.White50)
            Text("Buyer: ${dispute.buyer} | Seller: ${dispute.seller}", fontSize = 12.sp, color = EShopColors.White50)
            Text("Amount: KSh ${dispute.amount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EShopColors.Gold)
            Text("Reason: ${dispute.reason}", fontSize = 11.sp, color = EShopColors.Error)

            Spacer(modifier = Modifier.height(12.dp))

            if (dispute.status == "Pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onResolve("REFUND_BUYER") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EShopColors.Success)
                    ) {
                        Text("Refund Buyer", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onResolve("PAY_SELLER") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Success)
                    ) {
                        Text("Pay Seller", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}