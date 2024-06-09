package dev.ykzza.posluga.data.entities

data class Chat(
    val chatId: String,
    val members: List<String>,
    val lastMessage: Message,
)