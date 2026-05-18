package com.example.thikaeshop.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AvailableRider
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AssignRiderDialog(
    orderId: String,
    availableRiders: List<AvailableRider>,
    onAssign: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRiderId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Assign Rider - $orderId",
                fontSize = 18.sp,
                color = EShopColors.White
            )
        },
        text = {
            Column {
                Text(
                    text = "Select a rider for this delivery:",
                    fontSize = 14.sp,
                    color = EShopColors.White50,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(availableRiders.filter { it.isAvailable }) { rider ->
                        AvailableRiderCard(
                            rider = rider,
                            onSelect = { selectedRiderId = rider.id },
                            isSelected = selectedRiderId == rider.id
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedRiderId != null) {
                        onAssign(selectedRiderId!!)
                    }
                },
                enabled = selectedRiderId != null
            ) {
                Text("Assign", color = if (selectedRiderId != null) EShopColors.Orange else EShopColors.White50)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = EShopColors.Error)
            }
        },
        containerColor = EShopColors.DarkCard
    )
}