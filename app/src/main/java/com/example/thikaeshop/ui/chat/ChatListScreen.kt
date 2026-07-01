package com.example.thikaeshop.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onChatClick: (String, String, String) -> Unit = { _, _, _ -> },
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load chats when screen opens
    LaunchedEffect(Unit) {
        Log.d("ChatListScreen", "🔄 Loading chats...")
        Log.d("ChatListScreen", "   Current User ID: $currentUserId")
        viewModel.loadChats()
    }

    // Log when chats are updated
    LaunchedEffect(chats) {
        Log.d("ChatListScreen", "📋 Chats updated: ${chats.size} chats")
        chats.forEach { chat ->
            Log.d("ChatListScreen", "   Chat: ${chat.chatId}")
            Log.d("ChatListScreen", "   Participants: ${chat.participants}")
            Log.d("ChatListScreen", "   ParticipantNames: ${chat.participantNames}")
            Log.d("ChatListScreen", "   LastMessage: ${chat.lastMessage}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Chats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EShopColors.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = EShopColors.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EShopColors.DarkBg
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(EShopColors.DarkBg, EShopColors.DarkCard)))
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ChatUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EShopColors.Orange)
                    }
                }
                is ChatUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as ChatUiState.Error).message,
                                color = EShopColors.Error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.loadChats() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {
                    if (chats.isEmpty()) {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "💬",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Chats Yet",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EShopColors.White
                            )
                            Text(
                                text = "Start a conversation with a seller",
                                fontSize = 14.sp,
                                color = EShopColors.White50
                            )
                            Button(
                                onClick = { viewModel.loadChats() },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Refresh")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chats) { chat ->
                                ChatListItem(
                                    chat = chat,
                                    currentUserId = currentUserId,
                                    onClick = {
                                        val otherUserId = chat.participants.find { it != currentUserId } ?: ""
                                        val otherName = chat.participantNames?.get(otherUserId) ?: "User"
                                        Log.d("ChatListScreen", "🔵 Chat clicked: ${chat.chatId}, $otherName, $otherUserId")
                                        onChatClick(chat.chatId, otherName, otherUserId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatRoom,
    currentUserId: String,
    onClick: () -> Unit
) {
    val otherUserId = chat.participants.find { it != currentUserId } ?: ""
    val otherName = chat.participantNames?.get(otherUserId) ?: "User"
    val unreadCount = chat.unreadCount?.get(currentUserId) ?: 0

    val timeString = chat.lastMessageTime?.let {
        val date = it.toDate()
        val today = Calendar.getInstance()
        val messageDay = Calendar.getInstance().apply { time = date }

        if (today.get(Calendar.DAY_OF_YEAR) == messageDay.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unreadCount > 0)
                EShopColors.Orange.copy(alpha = 0.15f)
            else
                EShopColors.White10
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(EShopColors.Orange, EShopColors.Gold)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = otherName.take(1).uppercase(),
                    color = EShopColors.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Chat info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = otherName,
                        fontSize = 16.sp,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (unreadCount > 0) EShopColors.White else EShopColors.White80,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = timeString,
                        fontSize = 11.sp,
                        color = EShopColors.White50
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage.ifEmpty { "No messages yet" },
                        fontSize = 13.sp,
                        color = if (unreadCount > 0) EShopColors.White70 else EShopColors.White40,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (unreadCount > 0) {
                        Badge(
                            containerColor = EShopColors.Orange,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                color = EShopColors.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}