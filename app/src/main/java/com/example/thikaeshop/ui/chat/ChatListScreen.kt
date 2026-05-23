package com.example.thikaeshop.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thikaeshop.data.models.ChatRoom
import com.example.thikaeshop.ui.theme.EShopColors
import com.example.thikaeshop.ui.viewmodels.ChatUiState
import com.example.thikaeshop.ui.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onBackClick: () -> Unit = {},
    onChatClick: (String, String) -> Unit = { _, _ -> },
    viewModel: ChatViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val uiState by viewModel.uiState.collectAsState()
    val chats by viewModel.chats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search conversations...", color = EShopColors.White50) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EShopColors.Gold) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EShopColors.Orange,
                    unfocusedBorderColor = EShopColors.White30,
                    focusedTextColor = EShopColors.White,
                    unfocusedTextColor = EShopColors.White
                ),
                singleLine = true
            )

            when (uiState) {
                is ChatUiState.Loading, is ChatUiState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EShopColors.Orange)
                    }
                }
                is ChatUiState.ChatsLoaded -> {
                    if (chats.isEmpty()) {
                        EmptyChatState()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chats) { chat ->
                                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val otherUserId = chat.participants.firstOrNull { it != currentUid } ?: ""
                                ChatRoomCard(
                                    chat = chat,
                                    onClick = {
                                        // Pass otherUserId as the name placeholder; resolved in MainActivity
                                        onChatClick(chat.chatId, otherUserId)
                                    }
                                )
                            }
                        }
                    }
                }
                is ChatUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${(uiState as ChatUiState.Error).message}", color = EShopColors.Error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadChats() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> EmptyChatState()
            }
        }
    }
}

@Composable
fun EmptyChatState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = EShopColors.White30, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No messages yet", fontSize = 16.sp, color = EShopColors.White50)
            Text("Start a conversation with a seller", fontSize = 12.sp, color = EShopColors.White30)
        }
    }
}

@Composable
fun ChatRoomCard(
    chat: ChatRoom,
    onClick: () -> Unit
) {
    val unreadCount = chat.unreadCount.values.sum()
    val timeString = chat.lastMessageTime?.let {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        format.format(it.toDate())
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EShopColors.White10)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(EShopColors.Orange),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "User", // TODO: Get user name from other participant
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EShopColors.White
                )
                Text(
                    text = chat.lastMessage,
                    fontSize = 12.sp,
                    color = EShopColors.White50,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = timeString,
                    fontSize = 10.sp,
                    color = EShopColors.White50
                )
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(
                        containerColor = EShopColors.Error,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Text(unreadCount.toString(), fontSize = 10.sp, color = EShopColors.White)
                    }
                }
            }
        }
    }
}