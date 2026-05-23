package com.example.thikaeshop.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.ChatMessage
import com.example.thikaeshop.data.models.ChatRoom
import com.example.thikaeshop.data.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class ChatsLoaded(val chats: List<ChatRoom>) : ChatUiState()
    data class MessagesLoaded(val messages: List<ChatMessage>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chats: StateFlow<List<ChatRoom>> = _chats.asStateFlow()

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

    fun sendMessage(chatId: String, text: String) {
        if (chatId.isBlank() || text.isBlank()) return
        viewModelScope.launch {
            try {
                repository.sendMessage(chatId, text)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "sendMessage error: ${e.message}", e)
                val msg = if (e.message?.contains("offline") == true || e.message?.contains("UNAVAILABLE") == true)
                    "No internet connection. Message not sent."
                else
                    e.message ?: "Failed to send message"
                _uiState.value = ChatUiState.Error(msg)
            }
        }
    }
}
