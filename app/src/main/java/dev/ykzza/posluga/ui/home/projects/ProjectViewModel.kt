package dev.ykzza.posluga.ui.home.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.data.repository.ProjectRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _project = MutableLiveData<UiState<Project>>()
    val project: LiveData<UiState<Project>>
        get() = _project

    fun getProject(projectId: String) {
        _project.value = UiState.Loading
        repository.getProject(
            projectId
        ) {
            _project.value = it
        }
    }
}