package dev.ykzza.posluga.data.entities

data class Review(
    var reviewId: String = "",
    val userId: String,
    val authorId: String,
    val rating: Int,
    val title: String,
    val text: String,
    val date: String
) {
    constructor() : this(
        "",
        "",
        "",
        0,
        "",
        "",
        "",
    )
}