package com.example.thikaeshop.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ChatUser(
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val isAdmin: Boolean = false,
    val avatar: String = ""
)

data class ChatRoom(
    @DocumentId
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Timestamp? = null,
    val unreadCount: Map<String, Int> = emptyMap()
)

data class ChatMessage(
    @DocumentId
    val messageId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null,
    val isRead: Boolean = false
)

// For UI display
data class ChatPreview(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int,
    val isOnline: Boolean = false
)