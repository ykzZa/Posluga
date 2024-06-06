package dev.ykzza.posluga.data.entities

data class SearchResult<T>(
    val item: T,
    val matchScore: Int
)