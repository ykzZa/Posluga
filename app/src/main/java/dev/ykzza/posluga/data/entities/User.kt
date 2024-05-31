package dev.ykzza.posluga.data.entities

data class User(
    var id: String,
    var nickname: String,
    val phoneNumber: String,
    val instagram: String,
    val telegram: String
)