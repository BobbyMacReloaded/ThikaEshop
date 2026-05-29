package com.example.thikaeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.thikaeshop.ui.theme.EShopColors

@Composable
fun ProductImage(
    imageUrl: String?,                      // accepts String, String?, or ""
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // KEY FIX: convert blank string "" to null so Coil goes straight to error slot
    // instead of attempting to load an empty URL and hanging on a placeholder forever
    val safeUrl = imageUrl?.takeIf { it.isNotBlank() }

    SubcomposeAsyncImage(
        model = safeUrl,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EShopColors.White10),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = EShopColors.Orange,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        error = {
            // Shown when safeUrl is null (blank/missing) OR when the network request fails
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EShopColors.White10),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ImageNotSupported,
                    contentDescription = "No image",
                    tint = EShopColors.White30,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}