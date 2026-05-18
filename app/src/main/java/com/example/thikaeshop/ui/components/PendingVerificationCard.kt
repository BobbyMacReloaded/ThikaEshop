package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.PendingVerification
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun PendingVerificationCard(
    verification: PendingVerification,
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
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(EShopColors.Orange.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = EShopColors.Gold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = verification.fullName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
                Text(
                    text = "Student ID: ${verification.studentId}",
                    fontSize = 12.sp,
                    color = EShopColors.White50
                )
                Text(
                    text = verification.university,
                    fontSize = 11.sp,
                    color = EShopColors.Gold
                )
                Text(
                    text = "Submitted: ${verification.submittedAt}",
                    fontSize = 10.sp,
                    color = EShopColors.White30
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // ID Photo button
                OutlinedButton(
                    onClick = { /* Show ID photo dialog */ },
                    modifier = Modifier.size(width = 80.dp, height = 32.dp)
                ) {
                    Text("View ID", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    IconButton(
                        onClick = onApprove,
                        modifier = Modifier
                            .size(32.dp)
                            .background(EShopColors.Success.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Approve", tint = EShopColors.Success)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onReject,
                        modifier = Modifier
                            .size(32.dp)
                            .background(EShopColors.Error.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = EShopColors.Error)
                    }
                }
            }
        }
    }
}