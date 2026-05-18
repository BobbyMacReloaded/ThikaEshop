package com.example.thikaeshop.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    gradient: List<Color> = EShopColors.PrimaryGradient
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = textStyle.copy(
            brush = Brush.linearGradient(
                colors = gradient,
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, 0f)
            )
        )
    )
}