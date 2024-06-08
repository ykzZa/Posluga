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

    fun getService(
        serviceId: String,
        result: (UiState<Service>) -> Unit
    )

    fun getServices(
        searchQuery: String?,
        descriptionSearch: Boolean,
        minPrice: Int?,
        maxPrice: Int?,
        category: String?,
        subCategory: String?,
        state: String?,
        city: String?,
        result: (UiState<List<Service>>) -> Unit
    )

    fun getUserServices(
        userId: String,
        result: (UiState<List<Service>>) -> Unit
    )

    fun deleteService(
        serviceId: String,
        result: (UiState<String>) -> Unit
    )

    suspend fun getServicesByIds(
        idList: List<String>,
        result: (UiState<List<Service>>) -> Unit
    )

    suspend fun uploadImages(
        userId: String,
        imagesUrl: List<Uri>,
        result: (UiState<List<String>>) -> Unit
    )
}