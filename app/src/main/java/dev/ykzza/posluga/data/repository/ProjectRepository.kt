package dev.ykzza.posluga.data.repository

import android.net.Uri
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.util.UiState

interface ProjectRepository {

    fun postProject(
        project: Project,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    )

    fun getProjects(
        result: (UiState<List<Project>>) -> Unit
    )

    suspend fun uploadImages(
        userId: String,
        imagesUrl: List<Uri>,
        result: (UiState<List<String>>) -> Unit
    )
}