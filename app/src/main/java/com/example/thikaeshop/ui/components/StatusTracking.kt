package com.example.thikaeshop.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.OrderStatus
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun StatusTimeline(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OrderStatus.PROCESSING to "📦 Order\nPlaced",
        OrderStatus.SHIPPED to "✅ Order\nConfirmed",
        OrderStatus.OUT_FOR_DELIVERY to "🚚 Out for\nDelivery",
        OrderStatus.DELIVERED to "📍 Delivered"
    )

    val currentStepIndex = steps.indexOfFirst { it.first == currentStatus }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, (status, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Step Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    index <= currentStepIndex -> EShopColors.Orange
                                    else -> EShopColors.White20
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (status) {
                                OrderStatus.PROCESSING -> "📦"
                                OrderStatus.SHIPPED -> "✅"
                                OrderStatus.OUT_FOR_DELIVERY -> "🚚"
                                OrderStatus.DELIVERED -> "📍"
                                else -> "📦"
                            },
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = if (index <= currentStepIndex) EShopColors.White else EShopColors.White50,
                        fontWeight = if (index == currentStepIndex) FontWeight.Bold else FontWeight.Normal
                    )

                    if (index == currentStepIndex) {
                        Text(
                            text = "Current",
                            fontSize = 8.sp,
                            color = EShopColors.Orange
                        )
                    }
                }

                // Connector line (except after last)
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(2.dp)
                            .background(
                                if (index < currentStepIndex) EShopColors.Orange
                                else EShopColors.White20
                            )
                    )
                }
            }
        }
    }
}