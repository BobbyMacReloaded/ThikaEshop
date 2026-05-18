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
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // Create a unique chat ID for two users
    private fun getChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    // Get or create chat room
    suspend fun getOrCreateChat(otherUserId: String): String {
        val chatId = getChatId(currentUserId, otherUserId)

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

        return chatId
    }

    // Get all chats for current user (real-time)
    fun getUserChats(): Flow<List<ChatRoom>> = callbackFlow {
        val listener = firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatRoom::class.java)
                } ?: emptyList()
                trySend(chats)
            }
        awaitClose { listener.remove() }
    }

    // Get messages for a specific chat (real-time)
    fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    // Send a message
    suspend fun sendMessage(chatId: String, text: String) {
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

        // Add message
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .set(message)
            .await()

        // Update chat room last message
        val chatRef = firestore.collection("chats").document(chatId)
        val otherUserId = getOtherParticipant(chatId)

        chatRef.update(
            mapOf(
                "lastMessage" to text,
                "lastMessageTime" to Timestamp.now(),
                "unreadCount.$otherUserId" to com.google.firebase.firestore.FieldValue.increment(1)
            )
        ).await()
    }

    // Mark messages as read
    suspend fun markAsRead(chatId: String) {
        // Reset unread count for current user
        firestore.collection("chats")
            .document(chatId)
            .update("unreadCount.$currentUserId", 0)
            .await()

        // Mark all messages as read
        val messages = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("isRead", false)
            .whereNotEqualTo("senderId", currentUserId)
            .get()
            .await()

        for (doc in messages.documents) {
            doc.reference.update("isRead", true).await()
        }
    }

    // Get user details
    suspend fun getUser(userId: String): ChatUser? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.toObject(ChatUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getOtherParticipant(chatId: String): String {
        val chat = firestore.collection("chats").document(chatId).get().await()
        val participants = chat.toObject(ChatRoom::class.java)?.participants ?: emptyList()
        return participants.find { it != currentUserId } ?: ""
    }
}