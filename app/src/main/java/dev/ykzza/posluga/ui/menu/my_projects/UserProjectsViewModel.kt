package dev.ykzza.posluga.ui.menu.my_projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.data.repository.ProjectRepository
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _userProjects = MutableLiveData<UiState<List<Project>>>()
    val userProjects: LiveData<UiState<List<Project>>>
        get() = _userProjects

    private val _projectDeleted = MutableLiveData<UiState<String>>()
    val projectDeleted: LiveData<UiState<String>>
        get() = _projectDeleted

    fun getUserProjects(
        userId: String
    ) {
        _userProjects.value = UiState.Loading
        repository.getUserProjects(
            userId
        ) {
            _userProjects.value = it
        }
    }

    fun deleteProject(
        projectId: String
    ) {
        _projectDeleted.value = UiState.Loading
        repository.getProject(projectId) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    viewModelScope.launch {
                        repository.deleteImages(uiState.data.images) {
                            repository.deleteProject(
                                projectId
                            ) {
                                _projectDeleted.value = it
                            }
                        }
                    }
                }

                else -> {
                    _projectDeleted.value = UiState.Error("Failed to delete project!")
                }
            }
        }
    }
}