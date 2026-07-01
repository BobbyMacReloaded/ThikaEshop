package com.example.thikaeshop.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.ChatMessage
import com.example.thikaeshop.data.models.ChatRoom
import com.example.thikaeshop.data.repository.ChatRepository
import com.example.thikaeshop.utils.AIContentDetector
import com.example.thikaeshop.utils.DetectionResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// ====== EXISTING UI STATE ======
sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class ChatsLoaded(val chats: List<ChatRoom>) : ChatUiState()
    data class MessagesLoaded(val messages: List<ChatMessage>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

// ====== NEW: AI Detection States ======
sealed class MessageSendState {
    object Idle : MessageSendState()
    data class Safe(val message: String) : MessageSendState()
    data class Suspicious(
        val message: String,
        val detectionResult: DetectionResult,
        val userRole: String // "BUYER" or "SELLER"
    ) : MessageSendState()
}

// ====== NEW: Safety Violation Data Class ======
data class SafetyViolation(
    val chatId: String,
    val message: String,
    val detectedType: String,
    val confidence: Float,
    val timestamp: Long,
    val userId: String
)

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()
    private val aiDetector = AIContentDetector() // ✅ AI Detector instance

    // ====== EXISTING STATE ======
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chats: StateFlow<List<ChatRoom>> = _chats.asStateFlow()

    // ====== NEW: AI Detection State ======
    private val _messageSendState = MutableStateFlow<MessageSendState>(MessageSendState.Idle)
    val messageSendState: StateFlow<MessageSendState> = _messageSendState.asStateFlow()

    // ====== NEW: Track violations for admin review ======
    private val _violations = MutableStateFlow<List<SafetyViolation>>(emptyList())
    val violations: StateFlow<List<SafetyViolation>> = _violations.asStateFlow()

    // ====== EXISTING FUNCTIONS (UNCHANGED) ======
    fun loadChats() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.value = ChatUiState.Error("You must be logged in to view chats.")
            return
        }
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            repository.getUserChats()
                .catch { e ->
                    Log.e("ChatViewModel", "loadChats error: ${e.message}", e)
                    val msg = if (e.message?.contains("offline") == true || e.message?.contains("UNAVAILABLE") == true)
                        "No internet connection. Please check your network and try again."
                    else
                        e.message ?: "Failed to load chats"
                    _uiState.value = ChatUiState.Error(msg)
                }
                .collect { chatRooms ->
                    _chats.value = chatRooms
                    _uiState.value = ChatUiState.ChatsLoaded(chatRooms)
                }
        }
    }

    fun loadMessages(chatId: String) {
        if (chatId.isBlank()) {
            _uiState.value = ChatUiState.Error("Invalid chat. Please go back and try again.")
            return
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid.isNullOrEmpty()) {
            _uiState.value = ChatUiState.Error("You must be logged in to view messages.")
            return
        }
        viewModelScope.launch {
            repository.getMessages(chatId)
                .catch { e ->
                    Log.e("ChatViewModel", "loadMessages error: ${e.message}", e)
                    val msg = if (e.message?.contains("offline") == true || e.message?.contains("UNAVAILABLE") == true)
                        "No internet connection. Messages will load when you're back online."
                    else
                        e.message ?: "Failed to load messages"
                    _uiState.value = ChatUiState.Error(msg)
                }
                .collect { messageList ->
                    _messages.value = messageList
                    _uiState.value = ChatUiState.MessagesLoaded(messageList)
                    try { repository.markAsRead(chatId) } catch (e: Exception) {
                        Log.w("ChatViewModel", "markAsRead failed: ${e.message}")
                    }
                }
        }
    }

    // ====== MODIFIED: sendMessage with AI Detection ======
    fun sendMessage(chatId: String, text: String, userRole: String = "BUYER") {
        if (chatId.isBlank() || text.isBlank()) {
            _uiState.value = ChatUiState.Error("Message cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                // ====== STEP 1: AI Detection ======
                val detectionResult = aiDetector.isSuspiciousMessage(text)

                // ====== STEP 2: Check if suspicious ======
                if (detectionResult.isSuspicious) {
                    // Show warning - don't send yet
                    _messageSendState.value = MessageSendState.Suspicious(
                        message = text,
                        detectionResult = detectionResult,
                        userRole = userRole
                    )
                    Log.d("ChatViewModel", "⚠️ Suspicious message detected: ${detectionResult.detectedType} (${detectionResult.confidence})")
                } else {
                    // Safe to send
                    sendMessageToRepository(chatId, text)
                    _messageSendState.value = MessageSendState.Safe(text)
                    Log.d("ChatViewModel", "✅ Message sent safely")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "AI detection error: ${e.message}", e)
                // Fallback: Send message anyway if AI fails
                sendMessageToRepository(chatId, text)
            }
        }
    }

    // ====== NEW: Force send message after warning ======
    fun sendMessageAfterWarning(chatId: String, text: String) {
        viewModelScope.launch {
            try {
                // Log the violation for admin review
                val detectionResult = (_messageSendState.value as? MessageSendState.Suspicious)?.detectionResult
                if (detectionResult != null) {
                    logSafetyViolation(chatId, text, detectionResult)
                }

                // Send the message
                sendMessageToRepository(chatId, text)
                _messageSendState.value = MessageSendState.Safe(text)
                Log.d("ChatViewModel", "⚠️ Warning bypassed - message sent")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "sendMessageAfterWarning error: ${e.message}", e)
                _uiState.value = ChatUiState.Error(e.message ?: "Failed to send message")
            }
        }
    }

    // ====== NEW: Log safety violations ======
    fun logSafetyViolation(
        chatId: String,
        message: String,
        detectionResult: DetectionResult
    ) {
        viewModelScope.launch {
            try {
                val violation = SafetyViolation(
                    chatId = chatId,
                    message = message,
                    detectedType = detectionResult.detectedType,
                    confidence = detectionResult.confidence,
                    timestamp = System.currentTimeMillis(),
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
                )
                _violations.value = _violations.value + violation

                // TODO: Save to Firebase/Supabase for admin review
                // repository.logViolation(violation)

                Log.d("ChatViewModel", "📝 Violation logged: ${detectionResult.detectedType}")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to log violation: ${e.message}")
            }
        }
    }

    // ====== NEW: Reset message send state ======
    fun resetMessageSendState() {
        _messageSendState.value = MessageSendState.Idle
    }

    // ====== PRIVATE: Send message to repository ======
    private suspend fun sendMessageToRepository(chatId: String, text: String) {
        try {
            repository.sendMessage(chatId, text)
            Log.d("ChatViewModel", "Message sent successfully")
        } catch (e: Exception) {
            Log.e("ChatViewModel", "sendMessage error: ${e.message}", e)
            val msg = if (e.message?.contains("offline") == true || e.message?.contains("UNAVAILABLE") == true)
                "No internet connection. Message not sent."
            else
                e.message ?: "Failed to send message"
            _uiState.value = ChatUiState.Error(msg)
            throw e // Re-throw so caller knows it failed
        }
    }
}