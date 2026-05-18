package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AdminTransaction
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AdminTransactionCard(
    transaction: AdminTransaction,
    modifier: Modifier = Modifier
) {
    val statusColor = when (transaction.status) {
        "Delivered" -> EShopColors.Success
        "Processing" -> EShopColors.Warning
        "Shipped" -> EShopColors.Orange
        "Cancelled" -> EShopColors.Error
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
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = "Transaction",
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(modifier = Modifier.weight(1f)) {
                // Order ID
                Text(
                    text = transaction.id,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Buyer and Seller
                Text(
                    text = "Buyer: ${transaction.buyer}",
                    fontSize = 12.sp,
                    color = EShopColors.White50
                )
                Text(
                    text = "Seller: ${transaction.seller}",
                    fontSize = 12.sp,
                    color = EShopColors.White50
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = transaction.date,
                    fontSize = 10.sp,
                    color = EShopColors.White30
                )
            }

            // Amount and Status
            Column(horizontalAlignment = Alignment.End) {
                // Amount
                Text(
                    text = "KSh ${transaction.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.Gold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = transaction.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Escrow Indicator
                if (transaction.escrowHeld) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔒",
                            fontSize = 10.sp
                        )
                        Text(
                            text = " Escrow",
                            fontSize = 9.sp,
                            color = EShopColors.Gold
                        )
                    }
                }
            }
        }
    }
}