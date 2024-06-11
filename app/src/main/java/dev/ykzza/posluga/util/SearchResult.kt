package dev.ykzza.posluga.util

data class SearchResult<T>(
    val item: T,
    val matchScore: Int
)