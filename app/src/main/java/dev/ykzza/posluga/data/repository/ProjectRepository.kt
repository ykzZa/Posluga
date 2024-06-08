package dev.ykzza.posluga.data.repository

import android.net.Uri
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.util.UiState

interface ProjectRepository {

    fun postProject(
        project: Project,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    )

    fun getProject(
        projectId: String,
        result: (UiState<Project>) -> Unit
    )

    fun getUserProjects(
        userId: String,
        result: (UiState<List<Project>>) -> Unit
    )

    fun deleteProject(
        projectId: String,
        result: (UiState<String>) -> Unit
    )

    fun getProjects(
        searchQuery: String?,
        descriptionSearch: Boolean,
        minPrice: Int?,
        maxPrice: Int?,
        category: String?,
        subCategory: String?,
        state: String?,
        city: String?,
        result: (UiState<List<Project>>) -> Unit
    )

    suspend fun uploadImages(
        userId: String,
        imagesUrl: List<Uri>,
        result: (UiState<List<String>>) -> Unit
    )
}