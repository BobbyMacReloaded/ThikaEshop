package com.example.thikaeshop.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.ChatMessage
import com.example.thikaeshop.ui.components.SafetyWarningDialog
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.ChatUiState
import com.example.thikaeshop.ui.viewmodels.ChatViewModel
import com.example.thikaeshop.ui.viewmodels.MessageSendState
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    receiverName: String,
    sellerId: String,  // ← UPDATED: Added sellerId parameter
    onBackClick: () -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val messageSendState by viewModel.messageSendState.collectAsState()

    // Track if warning dialog should be shown
    var showWarningDialog by remember { mutableStateOf(false) }
    var pendingSuspiciousMessage by remember { mutableStateOf("") }
    var pendingDetectionResult by remember { mutableStateOf<com.example.thikaeshop.utils.DetectionResult?>(null) }

    // ====== DETERMINE USER ROLE ======
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // If current user is the seller, role is SELLER, otherwise BUYER
    val userRole = if (currentUserId == sellerId) {
        Log.d("ChatDetailScreen", "🔵 User is SELLER")
        "SELLER"
    } else {
        Log.d("ChatDetailScreen", "🔵 User is BUYER")
        "BUYER"
    }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    // Handle AI detection results
    LaunchedEffect(messageSendState) {
        when (val state = messageSendState) {
            is MessageSendState.Suspicious -> {
                // Show warning dialog
                pendingSuspiciousMessage = state.message
                pendingDetectionResult = state.detectionResult
                showWarningDialog = true
            }
            is MessageSendState.Safe -> {
                // Message was sent safely, clear input
                messageText = ""
            }
            else -> { /* Idle state - do nothing */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = receiverName,
                        fontSize = 16.sp,
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
                ),
                actions = {
                    // Show role badge
                    Surface(
                        color = EShopColors.Orange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = userRole,
                            color = EShopColors.Orange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Protected Chat",
                        tint = EShopColors.Orange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
        ) {
            // ====== Safety banner at top of chat ======
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EShopColors.Orange.copy(alpha = 0.15f),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safety",
                        tint = EShopColors.Orange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 Protected chat - Keep transactions on platform",
                        fontSize = 11.sp,
                        color = EShopColors.White70,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when {
                    uiState is ChatUiState.Loading && messages.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = EShopColors.Orange)
                            }
                        }
                    }
                    uiState is ChatUiState.Error && messages.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (uiState as ChatUiState.Error).message,
                                    color = EShopColors.Error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Message Input
            MessageInputRow(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(chatId, messageText, userRole)  // ← PASS role
                    }
                },
                isLoading = uiState is ChatUiState.Loading
            )
        }
    }

    // Safety Warning Dialog
    if (showWarningDialog && pendingDetectionResult != null) {
        SafetyWarningDialog(
            onDismiss = {
                showWarningDialog = false
                pendingSuspiciousMessage = ""
                pendingDetectionResult = null
                viewModel.resetMessageSendState()
            },
            onSendAnyway = {
                viewModel.sendMessageAfterWarning(chatId, pendingSuspiciousMessage)
                showWarningDialog = false
                pendingSuspiciousMessage = ""
                pendingDetectionResult = null
            },
            detectionResult = pendingDetectionResult!!,
            userRole = userRole  // ← PASS role
        )
    }
}

@Composable
fun MessageInputRow(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...", color = EShopColors.White50) },
            shape = RoundedCornerShape(24.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EShopColors.Orange,
                unfocusedBorderColor = EShopColors.White30,
                focusedTextColor = EShopColors.White,
                unfocusedTextColor = EShopColors.White
            )
        )

        FloatingActionButton(
            onClick = onSendClick,
            containerColor = EShopColors.Orange,
            modifier = Modifier.size(48.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = EShopColors.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = EShopColors.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isSentByMe = message.senderId == FirebaseAuth.getInstance().currentUser?.uid
    val timeString = message.timestamp?.let {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it.toDate())
    } ?: ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isSentByMe) 16.dp else 4.dp,
                bottomEnd = if (isSentByMe) 4.dp else 16.dp
            ),
            color = if (isSentByMe) EShopColors.Orange else EShopColors.White10
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (isSentByMe) EShopColors.White else EShopColors.White80
                )
                Text(
                    text = timeString,
                    fontSize = 9.sp,
                    color = EShopColors.White50,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}