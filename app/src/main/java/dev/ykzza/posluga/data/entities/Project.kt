package dev.ykzza.posluga.data.entities

data class Project(
    var projectId: String,
    val title: String,
    val description: String,
    val category: String,
    val subCategory: String,
    val authorId: String,
    val date: String,
    val price: Int,
    val state: String,
    val city: String,
    var images: List<String> = listOf()
) {
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        "",
        "",
        emptyList(),
    )
}