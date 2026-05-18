package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    var shimmerOffset by remember { mutableStateOf(Offset(-300f, 0f)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            shimmerOffset = Offset(300f, 0f)
            delay(1000)
            shimmerOffset = Offset(-300f, 0f)
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    ),
                    start = shimmerOffset,
                    end = shimmerOffset + Offset(300f, 0f)
                )
            )
    )
}

@Composable
fun ProductCardShimmer() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .width(160.dp)
            .padding(8.dp)
    ) {
        ShimmerLoading(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        ShimmerLoading(modifier = Modifier.fillMaxWidth().height(16.dp))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        ShimmerLoading(modifier = Modifier.width(80.dp).height(12.dp))
    }
}