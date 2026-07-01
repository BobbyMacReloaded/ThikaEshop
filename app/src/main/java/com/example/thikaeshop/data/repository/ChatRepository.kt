package com.example.thikaeshop.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        if (currentUserId.isEmpty()) {
            Log.e("ChatRepository", "❌ getOrCreateChat: currentUserId is empty")
            return ""
        }

        if (otherUserId.isEmpty()) {
            Log.e("ChatRepository", "❌ getOrCreateChat: otherUserId is empty")
            return ""
        }

        val chatId = getChatId(currentUserId, otherUserId)
        Log.d("ChatRepository", "🔵 getOrCreateChat: chatId=$chatId")

        return try {
            val chatRef = firestore.collection("chats").document(chatId)
            val snapshot = chatRef.get().await()

            if (!snapshot.exists()) {
                Log.d("ChatRepository", "   Creating new chat...")
                val chatRoom = ChatRoom(
                    chatId = chatId,
                    participants = listOf(currentUserId, otherUserId),
                    participantNames = emptyMap(),  // Will be updated by ChatHelper
                    lastMessage = "",
                    lastMessageTime = Timestamp.now(),
                    unreadCount = mapOf(currentUserId to 0, otherUserId to 0)
                )
                chatRef.set(chatRoom).await()
                Log.d("ChatRepository", "✅ Created new chat: $chatId")
            } else {
                Log.d("ChatRepository", "✅ Chat exists: $chatId")
            }
            chatId
        } catch (e: Exception) {
            Log.e("ChatRepository", "❌ getOrCreateChat failed: ${e.message}", e)
            ""
        }
    }

    // ====== FIXED: Get all chats for current user ======
    fun getUserChats(): Flow<List<ChatRoom>> {
        if (currentUserId.isEmpty()) {
            Log.e("ChatRepository", "❌ getUserChats: currentUserId is empty")
            return emptyFlow()
        }

        Log.d("ChatRepository", "🔵 getUserChats called for user: $currentUserId")

        return callbackFlow {
            // Query chats where user is a participant
            val query = firestore.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .orderBy("lastMessageTime", com.google.firebase.firestore.Query.Direction.DESCENDING)

            Log.d("ChatRepository", "   Query: chats where participants contains $currentUserId")

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatRepository", "❌ getUserChats error: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.e("ChatRepository", "❌ getUserChats: snapshot is null")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                Log.d("ChatRepository", "📋 Received ${snapshot.documents.size} chat documents")

                val chats = snapshot.documents.mapNotNull { doc ->
                    try {
                        Log.d("ChatRepository", "   Document: ${doc.id}")
                        Log.d("ChatRepository", "   Data: ${doc.data}")

                        val chat = doc.toObject(ChatRoom::class.java)
                        if (chat != null) {
                            Log.d("ChatRepository", "✅ Parsed chat: ${chat.chatId}")
                            chat.copy(chatId = doc.id)
                        } else {
                            Log.e("ChatRepository", "❌ Failed to parse chat from: ${doc.id}")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "❌ Error parsing chat: ${e.message}", e)
                        null
                    }
                }

                Log.d("ChatRepository", "✅ Parsed ${chats.size} chats")
                chats.forEach { chat ->
                    Log.d("ChatRepository", "   Chat: ${chat.chatId}, Participants: ${chat.participants}")
                }

                trySend(chats)
            }

            awaitClose {
                Log.d("ChatRepository", "👋 Removing listener")
                listener.remove()
            }
        }
    }

    // Get messages for a specific chat (real-time)
    fun getMessages(chatId: String): Flow<List<ChatMessage>> {
        if (chatId.isBlank() || currentUserId.isEmpty()) {
            Log.e("ChatRepository", "❌ getMessages: chatId or currentUserId is empty")
            return emptyFlow()
        }

        Log.d("ChatRepository", "🔵 getMessages called for chat: $chatId")

        return callbackFlow {
            val listener = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ChatRepository", "❌ getMessages error: ${error.message}", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        Log.e("ChatRepository", "❌ getMessages: snapshot is null")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    Log.d("ChatRepository", "📋 Received ${snapshot.documents.size} messages for chat: $chatId")

                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ChatMessage::class.java)?.copy(messageId = doc.id)
                        } catch (e: Exception) {
                            Log.e("ChatRepository", "❌ Error parsing message: ${e.message}", e)
                            null
                        }
                    }

                    Log.d("ChatRepository", "✅ Parsed ${messages.size} messages")
                    trySend(messages)
                }
            awaitClose {
                Log.d("ChatRepository", "👋 Removing listener for chat: $chatId")
                listener.remove()
            }
        }
    }

    // Send a message
    suspend fun sendMessage(chatId: String, text: String) {
        if (chatId.isBlank() || currentUserId.isEmpty()) {
            Log.e("ChatRepository", "❌ sendMessage: chatId or currentUserId is empty")
            return
        }

        Log.d("ChatRepository", "🔵 sendMessage: chatId=$chatId, text=$text")

        try {
            // Create message document
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

            Log.d("ChatRepository", "   Saving message: $messageId")

            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(message)
                .await()

            Log.d("ChatRepository", "✅ Message saved")

            // Get other participant
            val otherUserId = getOtherParticipant(chatId)
            Log.d("ChatRepository", "   otherUserId: '$otherUserId'")

            // Update chat room
            val updates = mutableMapOf<String, Any>(
                "lastMessage" to text,
                "lastMessageTime" to Timestamp.now()
            )

            if (otherUserId.isNotEmpty()) {
                updates["unreadCount.$otherUserId"] = com.google.firebase.firestore.FieldValue.increment(1)
                Log.d("ChatRepository", "📝 Updating unreadCount for: $otherUserId")
            } else {
                Log.w("ChatRepository", "⚠️ otherUserId is empty, skipping unreadCount update")
            }

            firestore.collection("chats").document(chatId)
                .update(updates)
                .await()

            Log.d("ChatRepository", "✅ Chat updated successfully")
        } catch (e: Exception) {
            Log.e("ChatRepository", "❌ sendMessage failed: ${e.message}", e)
            throw e
        }
    }

    // Mark messages as read
    suspend fun markAsRead(chatId: String) {
        if (currentUserId.isEmpty() || chatId.isBlank()) {
            Log.w("ChatRepository", "⚠️ markAsRead: currentUserId or chatId is empty")
            return
        }

        Log.d("ChatRepository", "🔵 markAsRead: chatId=$chatId")

        try {
            firestore.collection("chats")
                .document(chatId)
                .update("unreadCount.$currentUserId", 0)
                .await()
            Log.d("ChatRepository", "✅ Unread count reset for user: $currentUserId")

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
            Log.d("ChatRepository", "✅ Messages marked as read")
        } catch (e: Exception) {
            Log.w("ChatRepository", "⚠️ markAsRead failed (non-critical): ${e.message}", e)
        }
    }

    // Get user details
    suspend fun getUser(userId: String): ChatUser? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.toObject(ChatUser::class.java)
        } catch (e: Exception) {
            Log.w("ChatRepository", "⚠️ getUser failed: ${e.message}", e)
            null
        }
    }

    // Get other participant
    private suspend fun getOtherParticipant(chatId: String): String {
        return try {
            val chat = firestore.collection("chats").document(chatId).get().await()
            val participants = chat.toObject(ChatRoom::class.java)?.participants ?: emptyList()
            Log.d("ChatRepository", "🔍 getOtherParticipant: participants=$participants, currentUserId=$currentUserId")

            val other = participants.find { it != currentUserId } ?: ""
            Log.d("ChatRepository", "🔍 Other participant: '$other'")
            other
        } catch (e: Exception) {
            Log.e("ChatRepository", "❌ getOtherParticipant error: ${e.message}", e)
            ""
        }
    }
}