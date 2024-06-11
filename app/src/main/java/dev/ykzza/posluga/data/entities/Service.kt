package dev.ykzza.posluga.data.entities

import com.google.firebase.Timestamp


data class Service(
    var serviceId: String,
    val title : String,
    val description: String,
    val category: String,
    val subCategory: String,
    val authorId: String,
    val date: Timestamp,
    val price: Int,
    val state: String,
    val city: String,
    var images: List<String> = listOf()
)
{
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        Timestamp.now(),
        0,
        "",
        "",
        emptyList(),
    )
}