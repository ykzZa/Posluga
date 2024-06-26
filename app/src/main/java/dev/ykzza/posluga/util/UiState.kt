package dev.ykzza.posluga.util

sealed class UiState<out T> {

    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Error(val errorMessage: String): UiState<Nothing>()
}