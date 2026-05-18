package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.data.models.AdminUser
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun AdminUserCard(
    user: AdminUser,
    onSuspend: () -> Unit,
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
                Icon(Icons.Default.Person, contentDescription = null, tint = EShopColors.Gold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EShopColors.White)
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = EShopColors.Success, modifier = Modifier.size(14.dp))
                    }
                }
                Text(user.email, fontSize = 11.sp, color = EShopColors.White50)
                Text(user.phone, fontSize = 11.sp, color = EShopColors.White50)
                Text("Joined: ${user.joinDate}", fontSize = 10.sp, color = EShopColors.White30)
            }

            if (!user.isSuspended) {
                Button(
                    onClick = onSuspend,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Error.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.Block, contentDescription = "Suspend", tint = EShopColors.Error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Suspend", fontSize = 11.sp, color = EShopColors.Error)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EShopColors.Error.copy(alpha = 0.15f)
                ) {
                    Text("Suspended", fontSize = 11.sp, color = EShopColors.Error, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
        }
    }
}