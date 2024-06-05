package dev.ykzza.posluga.data.repository

import android.net.Uri
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.util.UiState

interface ServiceRepository {

    fun postService(
        service: Service,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    )

    fun getServices(
        result: (UiState<List<Service>>) -> Unit
    )

    suspend fun uploadImages(
        userId: String,
        imagesUrl: List<Uri>,
        result: (UiState<List<String>>) -> Unit
    )
}