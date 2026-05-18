package com.example.thikaeshop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun SearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = EShopColors.White50) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EShopColors.Gold) },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = EShopColors.White50,
                    modifier = Modifier.clickable { onValueChange(TextFieldValue("")) }
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EShopColors.Orange,
            unfocusedBorderColor = EShopColors.White30,
            focusedTextColor = EShopColors.White,
            unfocusedTextColor = EShopColors.White,
            cursorColor = EShopColors.Orange
        ),
        singleLine = true
    )
}