package dev.ykzza.posluga.data.repository

import dev.ykzza.posluga.data.entities.Chat
import dev.ykzza.posluga.data.entities.Message
import dev.ykzza.posluga.util.UiState

interface ChatRepository {

    fun sendMessage(
        message: Message,
        chatId: String,
        result: (UiState<String>) -> Unit
    )

    fun deleteChat(
        chatId: String,
        result: (UiState<String>) -> Unit
    )

    fun getUserChats(
        userId: String,
        result: (UiState<List<Chat>>) -> Unit
    )

    fun getChatById(
        chatId: String,
        result: (UiState<Chat>) -> Unit
    )

    fun createChat(
        chat: Chat,
        result: (UiState<String>) -> Unit
    )
}