package dev.ykzza.posluga.data.entities

import com.google.firebase.Timestamp

data class Message(
    val senderId: String,
    val content: String,
    val date: Timestamp
) {
    constructor() : this(
        "",
        "",
        Timestamp.now()
    )
}