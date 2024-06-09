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
        result: (List<Chat>) -> Unit
    )

    fun getChatById(
        chatId: String,
        result: (Chat?) -> Unit
    )

    fun createChat(
        chat: Chat,
        result: (Chat?) -> Unit
    )

    fun checkChatExist(
        members: List<String>,
        result: (UiState<Boolean>) -> Unit
    )

    fun getChatByMembers(
        members: List<String>,
        result: (Chat?) -> Unit
    )

    fun loadMessages(
        chatId: String,
        result: (UiState<List<Message>>) -> Unit
    )
}