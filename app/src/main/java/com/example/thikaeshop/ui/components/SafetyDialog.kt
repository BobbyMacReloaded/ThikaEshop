package com.example.thikaeshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.thikaeshop.utils.DetectionResult

@Composable
fun SafetyWarningDialog(
    onDismiss: () -> Unit,
    onSendAnyway: () -> Unit,
    detectionResult: DetectionResult,
    userRole: String // "BUYER" or "SELLER"
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Icon
                Text(
                    text = when (detectionResult.detectedType) {
                        "PHONE_NUMBER" -> "📱"
                        "CONTACT_INTENT" -> "💬"
                        else -> "⚠️"
                    },
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = when (detectionResult.detectedType) {
                        "PHONE_NUMBER" -> "Phone Number Detected!"
                        "CONTACT_INTENT" -> "Contact Sharing Detected!"
                        else -> "⚠️ Safety Warning"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confidence Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (detectionResult.confidence > 0.7)
                        Color(0xFFFF6B35).copy(alpha = 0.2f)
                    else
                        Color(0xFFFFD700).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Confidence: ${(detectionResult.confidence * 100).toInt()}%",
                        color = if (detectionResult.confidence > 0.7)
                            Color(0xFFFF6B35)
                        else
                            Color(0xFFFFD700),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Warning Message (Role-specific)
                val warningMessage = when {
                    userRole == "SELLER" -> """
                        ⚠️ SELLER WARNING:
                        
                        Sharing your phone number:
                        • Bypasses our escrow protection
                        • You lose seller guarantee
                        • No proof of delivery
                        • No dispute resolution
                        
                        Keep transactions on the app to get paid safely!
                    """.trimIndent()

                    detectionResult.detectedType == "PHONE_NUMBER" -> """
                        ⚠️ BUYER WARNING:
                        
                        Getting a seller's phone number:
                        • You lose buyer protection
                        • No escrow guarantee
                        • Risk of fraud
                        • No order tracking
                        
                        Pay through the app to stay protected!
                    """.trimIndent()

                    else -> """
                        ⚠️ SAFETY WARNING:
                        
                        For your safety, please keep all
                        communication and transactions
                        within the app.
                        
                        This protects both buyers and sellers!
                    """.trimIndent()
                }

                Text(
                    text = warningMessage,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel/Go Back
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Go Back")
                    }

                    // Send Anyway (with warning)
                    Button(
                        onClick = onSendAnyway,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35)
                        )
                    ) {
                        Text("Send Anyway")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Small note
                Text(
                    text = "⚠️ Sending may flag your account for review",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}