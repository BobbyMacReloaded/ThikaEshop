package com.example.thikaeshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.thikaeshop.data.models.VerificationStatus
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun VerificationStatusCard(
    status: VerificationStatus,
    rejectionReason: String? = null,
    modifier: Modifier = Modifier
) {
    // Use separate variables instead of Triple
    val icon = when (status) {
        VerificationStatus.PENDING -> Icons.Default.HourglassEmpty
        VerificationStatus.APPROVED -> Icons.Default.Verified
        VerificationStatus.REJECTED -> Icons.Default.Cancel
    }

    val title = when (status) {
        VerificationStatus.PENDING -> "Pending Verification"
        VerificationStatus.APPROVED -> "Verified Student"
        VerificationStatus.REJECTED -> "Verification Failed"
    }

    val description = when (status) {
        VerificationStatus.PENDING -> "Your ID is being reviewed by admin. This takes 24-48 hours."
        VerificationStatus.APPROVED -> "You can now sell items on Student Exchange!"
        VerificationStatus.REJECTED -> rejectionReason ?: "Please submit a clear photo of your student ID"
    }

    val color = when (status) {
        VerificationStatus.PENDING -> EShopColors.Warning
        VerificationStatus.APPROVED -> EShopColors.Success
        VerificationStatus.REJECTED -> EShopColors.Error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = EShopColors.White50
                )
            }
        }
    }
}