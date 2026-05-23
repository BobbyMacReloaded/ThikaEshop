package com.example.thikaeshop.ui.pindrop

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.thikaeshop.ui.theme.EShopColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDropScreen(
    onLocationSelected: (lat: Double, lng: Double, landmark: String) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Thika Town coordinates (Landless, Kiganjo, Kiandutu area)
    val thikaTown = LatLng(-1.0385, 37.0839)

    var selectedPosition by remember { mutableStateOf(thikaTown) }
    var landmarkName by remember { mutableStateOf("") }
    var showMarker by remember { mutableStateOf(false) }
    var showConfirmButton by remember { mutableStateOf(false) }

    // Check location permission
    val hasLocationPermission = remember {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(thikaTown, 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pin Drop Location",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        },
        floatingActionButton = {
            if (showMarker) {
                FloatingActionButton(
                    onClick = { showConfirmButton = !showConfirmButton },
                    containerColor = EShopColors.Orange,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Confirm", tint = EShopColors.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
        ) {
            // Google Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission,
                        mapType = MapType.NORMAL
                    ),
                    onMapClick = { latLng ->
                        selectedPosition = latLng
                        showMarker = true
                        showConfirmButton = false
                    }
                ) {
                    if (showMarker) {
                        Marker(
                            state = MarkerState(position = selectedPosition),
                            title = "Delivery Location",
                            snippet = if (landmarkName.isNotEmpty()) landmarkName else "Tap to add landmark name"
                        )
                    }
                }

                // Instruction overlay
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EShopColors.DarkBg.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = EShopColors.Gold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Tap on map to drop a pin",
                            fontSize = 11.sp,
                            color = EShopColors.White
                        )
                    }
                }
            }

            // Landmark Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (showMarker) "📍 Selected Location" else "📍 Tap on map to select location",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showMarker) EShopColors.Gold else EShopColors.White50
                    )

                    if (showMarker) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Coordinates display
                        Text(
                            text = "Lat: ${String.format("%.6f", selectedPosition.latitude)}\nLng: ${String.format("%.6f", selectedPosition.longitude)}",
                            fontSize = 11.sp,
                            color = EShopColors.White50
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Landmark name input
                        OutlinedTextField(
                            value = landmarkName,
                            onValueChange = { landmarkName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., Near Blue Water Tank", color = EShopColors.White50) },
                            label = { Text("Landmark Name", color = EShopColors.White50) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = EShopColors.Gold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EShopColors.Orange,
                                unfocusedBorderColor = EShopColors.White30,
                                focusedTextColor = EShopColors.White,
                                unfocusedTextColor = EShopColors.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Landmark suggestions
                        Text(
                            text = "Suggestions:",
                            fontSize = 11.sp,
                            color = EShopColors.White50
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip("Near Blue Water Tank") { landmarkName = it }
                            SuggestionChip("Opposite Church") { landmarkName = it }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip("Behind Hostel") { landmarkName = it }
                            SuggestionChip("Next to Shop") { landmarkName = it }
                        }

                        if (showConfirmButton) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (landmarkName.isNotBlank()) {
                                        onLocationSelected(
                                            selectedPosition.latitude,
                                            selectedPosition.longitude,
                                            landmarkName
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                                enabled = landmarkName.isNotBlank()
                            ) {
                                Text("Confirm Location", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Info note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = EShopColors.Gold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your pin helps delivery riders find you exactly in areas like Landless, Kiganjo, and Kiandutu",
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick(text) },
        shape = RoundedCornerShape(16.dp),
        color = EShopColors.White10,
        border = androidx.compose.foundation.BorderStroke(1.dp, EShopColors.White30)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = EShopColors.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}