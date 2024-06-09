package dev.ykzza.posluga.data.entities

data class Chat(
    var chatId: String,
    var members: List<String>
) {
    constructor() : this(
        "",
        emptyList()
    )
}