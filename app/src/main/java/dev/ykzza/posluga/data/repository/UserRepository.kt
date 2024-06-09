package dev.ykzza.posluga.data.repository

import android.net.Uri
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.util.UiState

interface UserRepository {

    fun getUserData(
        userId: String,
        result: (UiState<User>) -> Unit
    )

    fun uploadImage(
        imageUri: Uri,
        userId: String,
        result: (UiState<String>) -> Unit
    )

    fun updateUserData(
        userId: String,
        updates: HashMap<String, Any>,
        result: (UiState<String>) -> Unit
    )

    fun updateUserDataWithImage(
        imageUri: Uri,
        userId: String,
        updates: HashMap<String, Any>,
        result: (UiState<String>) -> Unit
    )

    fun updateProfilePicUrl(
        imageUri: String,
        userId: String,
        result: (UiState<String>) -> Unit
    )

    fun removeProfilePic(
        userId: String,
        result: (UiState<String>) -> Unit
    )

    fun getUserServiceCount(
        userId: String,
        result: (UiState<Int>) -> Unit
    )

    fun getUserProjectCount(
        userId: String,
        result: (UiState<Int>) -> Unit
    )

    fun getUserReviewsCount(
        userId: String,
        result: (UiState<Int>) -> Unit
    )

    fun isServiceFavourite(
        userId: String,
        serviceId: String,
        result: (UiState<Boolean>) -> Unit
    )

    fun favouritesChange(
        userId: String,
        serviceId: String,
        changes: Boolean,
        result: (UiState<String>) -> Unit
    )

    suspend fun getUsersListByIds(
        usersId: List<String>,
        result: (List<User>) -> Unit
    )
}