package com.example.thikaeshop.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object ChatHelper {
    private const val TAG = "ChatHelper"
    private val firestore = FirebaseFirestore.getInstance()

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    suspend fun getOrCreateChat(otherUserId: String, otherUserName: String): String {
        Log.d(TAG, "🔵 getOrCreateChat called")
        Log.d(TAG, "   otherUserId: '$otherUserId'")
        Log.d(TAG, "   otherUserName: '$otherUserName'")

        if (currentUserId.isEmpty()) {
            Log.e(TAG, "❌ No user logged in!")
            return ""
        }
        Log.d(TAG, "   currentUserId: '$currentUserId'")

        if (currentUserId == otherUserId) {
            Log.e(TAG, "❌ Cannot create chat with self!")
            return ""
        }

        if (otherUserId.isEmpty()) {
            Log.e(TAG, "❌ Other user ID is empty!")
            return ""
        }

        val chatId = getChatId(currentUserId, otherUserId)
        Log.d(TAG, "   Generated chatId: '$chatId'")

        return try {
            val chatRef = firestore.collection("chats").document(chatId)

            Log.d(TAG, "   Checking if chat exists...")
            val snapshot = chatRef.get().await()
            Log.d(TAG, "   Chat exists: ${snapshot.exists()}")

            if (!snapshot.exists()) {
                Log.d(TAG, "   Creating new chat room...")
                val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"

                val chatRoom = mapOf(
                    "chatId" to chatId,
                    "participants" to listOf(currentUserId, otherUserId),
                    "participantNames" to mapOf(
                        currentUserId to currentUserName,
                        otherUserId to otherUserName
                    ),
                    "lastMessage" to "",
                    "lastMessageTime" to com.google.firebase.Timestamp.now(),
                    "unreadCount" to mapOf(
                        currentUserId to 0,
                        otherUserId to 0
                    ),
                    "createdAt" to com.google.firebase.Timestamp.now()
                )

                chatRef.set(chatRoom, SetOptions.merge()).await()
                Log.d(TAG, "✅ Created new chat room: $chatId")
            } else {
                Log.d(TAG, "✅ Using existing chat room: $chatId")
            }

            Log.d(TAG, "✅ Returning chatId: '$chatId'")
            chatId

        } catch (e: com.google.firebase.firestore.FirebaseFirestoreException) {
            // FIX: Specifically catch Firestore exceptions and surface the exact code.
            // PERMISSION_DENIED here means Firestore Security Rules are blocking the
            // read/write — this is the most likely cause when the chat document is
            // visible in the Firebase Console but the app can't reach it.
            Log.e(TAG, "❌ Firestore error: ${e.code} — ${e.message}", e)
            if (e.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                Log.e(TAG, "🚫 PERMISSION DENIED — check your Firestore Security Rules for the 'chats' collection. " +
                        "The current rules are likely blocking read/write for this user on this document.")
            }
            ""
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in getOrCreateChat", e)
            Log.e(TAG, "   Exception type: ${e.javaClass.simpleName}")
            Log.e(TAG, "   Message: ${e.message}")
            ""
        }
    }

    suspend fun chatExists(chatId: String): Boolean {
        return try {
            val snapshot = firestore.collection("chats").document(chatId).get().await()
            snapshot.exists()
        } catch (e: com.google.firebase.firestore.FirebaseFirestoreException) {
            Log.e(TAG, "chatExists: Firestore error ${e.code} — ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking chat existence: ${e.message}")
            false
        }
    }

    suspend fun getChatIdDirect(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }
}