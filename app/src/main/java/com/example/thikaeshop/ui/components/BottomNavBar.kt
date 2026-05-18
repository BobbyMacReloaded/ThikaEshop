package com.example.thikaeshop.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thikaeshop.ui.theme.EShopColors

data class NavItem(
    val title: String,
    val icon:ImageVector,
    val selectedIcon: ImageVector = icon
)

@Composable
fun BottomNavBar(
    onItemSelected: (Int) -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Exchange", Icons.Default.SwapHoriz),
        NavItem("Market", Icons.Default.ShoppingCart),
        NavItem("Orders", Icons.Filled.Receipt),
        NavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = EShopColors.DarkBg.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onItemSelected(index)  // This tells the parent which tab was clicked
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (selectedItem == index) EShopColors.Orange else EShopColors.White50
                    )
                },
                label = {
                    Text(
                        item.title,
                        fontSize = 10.sp,
                        color = if (selectedItem == index) EShopColors.Orange else EShopColors.White50
                    )
                }
            )
        }
    }
}