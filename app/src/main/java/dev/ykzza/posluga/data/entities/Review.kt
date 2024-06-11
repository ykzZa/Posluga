package dev.ykzza.posluga.data.entities

import com.google.firebase.Timestamp

data class Review(
    var reviewId: String = "",
    val userId: String,
    val authorId: String,
    val rating: Int,
    val title: String,
    val text: String,
    val date: Timestamp
) {
    constructor() : this(
        "",
        "",
        "",
        0,
        "",
        "",
        Timestamp.now(),
    )
}