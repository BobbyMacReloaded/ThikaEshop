package com.example.thikaeshop.ui.subscription

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.SubscriptionBenefits
import com.example.thikaeshop.data.models.SubscriptionTier
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.SubscriptionUiState
import com.example.thikaeshop.ui.viewmodels.SubscriptionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit = {},
    onSelectTier: (SubscriptionTier) -> Unit = {},
    viewModel: SubscriptionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // M-Pesa phone number dialog state
    var showMpesaDialog by remember { mutableStateOf(false) }
    var mpesaNumber by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf<SubscriptionTier?>(null) }
    var showStkSimulation by remember { mutableStateOf(false) }
    var stkPin by remember { mutableStateOf("") }

    // Handle payment states
    LaunchedEffect(uiState) {
        when (uiState) {
            is SubscriptionUiState.PaymentSuccess -> {
                Toast.makeText(
                    context,
                    "🎉 Subscription activated!",
                    Toast.LENGTH_LONG
                ).show()
                onBackClick()
            }
            is SubscriptionUiState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as SubscriptionUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Pro", color = EShopColors.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EShopColors.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EShopColors.DarkBg)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("🎓", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Get Discovered on Campus",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Boost your sales with visibility tools built for students, by students",
                    fontSize = 13.sp,
                    color = EShopColors.White50,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
                )
            }

            // Current status
            if (uiState is SubscriptionUiState.Loaded) {
                val sub = (uiState as SubscriptionUiState.Loaded).subscription
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Plan", fontSize = 11.sp, color = EShopColors.White50)
                            Text(
                                "${sub.tierEnum.emoji} ${sub.tierEnum.displayName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                        }
                        if (sub.tierEnum != SubscriptionTier.FREE) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EShopColors.Success)
                        }
                    }
                }
            }

            // Pricing cards
            TierCard(
                tier = SubscriptionTier.PRO,
                highlight = true,
                onSubscribe = {
                    selectedTier = SubscriptionTier.PRO
                    showMpesaDialog = true
                }
            )
            TierCard(
                tier = SubscriptionTier.PRO_PLUS,
                highlight = false,
                onSubscribe = {
                    selectedTier = SubscriptionTier.PRO_PLUS
                    showMpesaDialog = true
                }
            )

            // The unique feature spotlight
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EShopColors.Gold.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = EShopColors.Gold)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Study Squads + Buyer Intent",
                            fontWeight = FontWeight.Bold,
                            color = EShopColors.White,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "See what your classmates are searching for before they even list it. " +
                                "Join your course's Study Squad and get first access to deals from sellers in your year group. " +
                                "This data only exists because everyone here is a verified student — not on Jumia, not on OLX.",
                        fontSize = 12.sp,
                        color = EShopColors.White60,
                        lineHeight = 18.sp
                    )
                }
            }

            // Comparison table
            Text(
                "Compare Plans",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EShopColors.White
            )
            ComparisonTable()

            Spacer(Modifier.height(24.dp))
        }
    }

    // M-Pesa Number Dialog
    if (showMpesaDialog && selectedTier != null) {
        AlertDialog(
            onDismissRequest = { showMpesaDialog = false },
            title = { Text("Enter M-Pesa Number", color = EShopColors.White) },
            text = {
                OutlinedTextField(
                    value = mpesaNumber,
                    onValueChange = { mpesaNumber = it },
                    placeholder = { Text("0712345678", color = EShopColors.White50) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EShopColors.Orange,
                        unfocusedBorderColor = EShopColors.White30,
                        focusedTextColor = EShopColors.White,
                        unfocusedTextColor = EShopColors.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mpesaNumber.isNotBlank()) {
                            // Close M-Pesa dialog
                            viewModel.startSubscriptionPayment(selectedTier!!, mpesaNumber)
                            showMpesaDialog = false
                            stkPin = "" // Reset PIN
                        }
                    }
                ) {
                    Text("Pay KSh ${selectedTier!!.priceKsh}")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMpesaDialog = false }) {
                    Text("Cancel", color = EShopColors.Orange)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ============================================================
    // STK PUSH SIMULATION DIALOG - LOOKS REAL, NO REAL MONEY!
    // ============================================================
    if (showStkSimulation && selectedTier != null) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    "M-Pesa STK Push",
                    color = EShopColors.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // M-Pesa Logo
                    Text("💳", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Enter your M-Pesa PIN to confirm",
                        fontSize = 14.sp,
                        color = EShopColors.White50
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Fake PIN input
                    OutlinedTextField(
                        value = stkPin,
                        onValueChange = { if (it.length <= 4) stkPin = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("****", color = EShopColors.White50) },
                        label = { Text("M-Pesa PIN", color = EShopColors.White50) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EShopColors.Orange,
                            unfocusedBorderColor = EShopColors.White30,
                            focusedTextColor = EShopColors.White,
                            unfocusedTextColor = EShopColors.White
                        )
                    )

                    // Fake amount display
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Amount:", color = EShopColors.White50)
                            Text(
                                "KSh ${selectedTier?.priceKsh ?: 0}",
                                color = EShopColors.Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "To: Campus Pro Subscription",
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "🔒 Test Mode: No real money will be deducted",
                        fontSize = 10.sp,
                        color = EShopColors.Orange
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (stkPin.length == 4) {
                            // Simulate payment processing
                            showStkSimulation = false
                            // Call the actual subscription activation
                            viewModel.activateTestSubscription(selectedTier!!)
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter a valid 4-digit PIN",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EShopColors.Orange),
                    enabled = stkPin.length == 4
                ) {
                    Text("Confirm Payment", color = EShopColors.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStkSimulation = false
                        Toast.makeText(
                            context,
                            "Payment cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text("Cancel", color = EShopColors.Error)
                }
            },
            containerColor = EShopColors.DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TierCard(
    tier: SubscriptionTier,
    highlight: Boolean,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) EShopColors.Orange.copy(alpha = 0.12f) else EShopColors.White10
        ),
        border = if (highlight) BorderStroke(2.dp, EShopColors.Orange) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (highlight) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EShopColors.Orange
                ) {
                    Text(
                        "MOST POPULAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tier.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    tier.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "KSh ${tier.priceKsh}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (highlight) EShopColors.Orange else EShopColors.Gold
                )
                Text(
                    " /month",
                    fontSize = 13.sp,
                    color = EShopColors.White50,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            val benefits = SubscriptionBenefits.benefits.filter {
                if (tier == SubscriptionTier.PRO) it.pro else it.proPlus
            }
            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EShopColors.Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(benefit.label, fontSize = 12.sp, color = EShopColors.White70)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (highlight) EShopColors.Orange else EShopColors.Gold
                )
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Subscribe via M-Pesa", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ComparisonTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("Feature", modifier = Modifier.weight(2f), fontSize = 11.sp, color = EShopColors.White50, fontWeight = FontWeight.Bold)
                Text("Free", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = EShopColors.White50, textAlign = TextAlign.Center)
                Text("Pro", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = EShopColors.Orange, textAlign = TextAlign.Center)
                Text("Pro+", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = EShopColors.Gold, textAlign = TextAlign.Center)
            }
            HorizontalDivider(color = EShopColors.White20)
            SubscriptionBenefits.benefits.forEach { b ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(b.label, modifier = Modifier.weight(2f), fontSize = 11.sp, color = EShopColors.White70)
                    CheckCell(b.free, Modifier.weight(0.7f))
                    CheckCell(b.pro, Modifier.weight(0.7f))
                    CheckCell(b.proPlus, Modifier.weight(0.7f))
                }
            }
        }
    }
}

@Composable
private fun CheckCell(value: Boolean, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (value) {
            Icon(Icons.Default.Check, contentDescription = null, tint = EShopColors.Success, modifier = Modifier.size(16.dp))
        } else {
            Icon(Icons.Default.Close, contentDescription = null, tint = EShopColors.White20, modifier = Modifier.size(16.dp))
        }
    }
}