package com.example.thikaeshop.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ChatHelper {

    private val firestore = FirebaseFirestore.getInstance()

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    suspend fun getOrCreateChat(otherUserId: String, otherUserName: String): String {
        // SAFETY CHECK: If no user is logged in, return empty string
        if (currentUserId.isEmpty()) {
            Log.e("ChatHelper", "No user logged in!")
            return ""
        }

        // SAFETY CHECK: Don't create chat with self
        if (currentUserId == otherUserId) {
            Log.e("ChatHelper", "Cannot create chat with self!")
            return ""
        }

        val chatId = getChatId(currentUserId, otherUserId)

        return try {
            val chatRef = firestore.collection("chats").document(chatId)
            val snapshot = chatRef.get().await()

            if (!snapshot.exists()) {
                val chatRoom = mapOf(
                    "chatId" to chatId,
                    "participants" to listOf(currentUserId, otherUserId),
                    "lastMessage" to "",
                    "lastMessageTime" to com.google.firebase.Timestamp.now(),
                    "unreadCount" to mapOf(currentUserId to 0, otherUserId to 0)
                )
                chatRef.set(chatRoom).await()
                Log.d("ChatHelper", "Created new chat room: $chatId")
            } else {
                Log.d("ChatHelper", "Existing chat room: $chatId")
            }
            chatId
        } catch (e: Exception) {
            Log.e("ChatHelper", "Error creating chat: ${e.message}")
            ""
        }
    }
}