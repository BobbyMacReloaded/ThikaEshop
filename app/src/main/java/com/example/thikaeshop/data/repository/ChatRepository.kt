package com.example.thikaeshop.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.example.thikaeshop.data.models.ChatMessage
import com.example.thikaeshop.data.models.ChatRoom
import com.example.thikaeshop.data.models.ChatUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private fun getChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    // Get or create chat room
    suspend fun getOrCreateChat(otherUserId: String): String {
        if (currentUserId.isEmpty()) return ""
        val chatId = getChatId(currentUserId, otherUserId)
        return try {
            val chatRef = firestore.collection("chats").document(chatId)
            val snapshot = chatRef.get().await()
            if (!snapshot.exists()) {
                val chatRoom = ChatRoom(
                    chatId = chatId,
                    participants = listOf(currentUserId, otherUserId),
                    lastMessage = "",
                    lastMessageTime = Timestamp.now(),
                    unreadCount = mapOf(currentUserId to 0, otherUserId to 0)
                )
                chatRef.set(chatRoom).await()
            }
            chatId
        } catch (e: Exception) {
            Log.e("ChatRepository", "getOrCreateChat failed: ${e.message}")
            ""
        }
    }

    // Get all chats for current user (real-time) — never throws into the coroutine
    fun getUserChats(): Flow<List<ChatRoom>> {
        if (currentUserId.isEmpty()) return emptyFlow()
        return callbackFlow {
            val listener = firestore.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ChatRepository", "getUserChats error: ${error.message}")
                        // Send empty list instead of closing the flow — keeps app alive
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val chats = snapshot?.documents?.mapNotNull { doc ->
                        runCatching { doc.toObject(ChatRoom::class.java) }.getOrNull()
                    } ?: emptyList()
                    trySend(chats)
                }
            awaitClose { listener.remove() }
        }
    }

    // Get messages for a specific chat (real-time) — never throws into the coroutine
    fun getMessages(chatId: String): Flow<List<ChatMessage>> {
        if (chatId.isBlank() || currentUserId.isEmpty()) return emptyFlow()
        return callbackFlow {
            val listener = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ChatRepository", "getMessages error: ${error.message}")
                        // Send empty list rather than crashing
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val messages = snapshot?.documents?.mapNotNull { doc ->
                        runCatching { doc.toObject(ChatMessage::class.java) }.getOrNull()
                    } ?: emptyList()
                    trySend(messages)
                }
            awaitClose { listener.remove() }
        }
    }

    // Send a message
    suspend fun sendMessage(chatId: String, text: String) {
        if (chatId.isBlank() || currentUserId.isEmpty()) return
        try {
            val messageId = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document().id

            val message = ChatMessage(
                messageId = messageId,
                senderId = currentUserId,
                text = text,
                timestamp = Timestamp.now(),
                isRead = false
            )

            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(message)
                .await()

            val otherUserId = getOtherParticipant(chatId)
            firestore.collection("chats").document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to text,
                        "lastMessageTime" to Timestamp.now(),
                        "unreadCount.$otherUserId" to com.google.firebase.firestore.FieldValue.increment(1)
                    )
                ).await()
        } catch (e: Exception) {
            Log.e("ChatRepository", "sendMessage failed: ${e.message}")
            throw e  // Let ViewModel show error to user
        }
    }

    // Mark messages as read — fully silent on failure (non-critical)
    suspend fun markAsRead(chatId: String) {
        if (currentUserId.isEmpty() || chatId.isBlank()) return
        try {
            firestore.collection("chats")
                .document(chatId)
                .update("unreadCount.$currentUserId", 0)
                .await()

            val messages = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("isRead", false)
                .get()
                .await()

            for (doc in messages.documents) {
                val senderId = doc.getString("senderId") ?: ""
                if (senderId != currentUserId) {
                    runCatching { doc.reference.update("isRead", true).await() }
                }
            }
        } catch (e: Exception) {
            Log.w("ChatRepository", "markAsRead failed (non-critical): ${e.message}")
            // Silently ignore — unread count is cosmetic
        }
    }

    // Get user details
    suspend fun getUser(userId: String): ChatUser? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.toObject(ChatUser::class.java)
        } catch (e: Exception) {
            Log.w("ChatRepository", "getUser failed: ${e.message}")
            null
        }
    }

    private suspend fun getOtherParticipant(chatId: String): String {
        return try {
            val chat = firestore.collection("chats").document(chatId).get().await()
            val participants = chat.toObject(ChatRoom::class.java)?.participants ?: emptyList()
            participants.find { it != currentUserId } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
