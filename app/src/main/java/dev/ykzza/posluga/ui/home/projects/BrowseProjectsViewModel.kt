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
class BrowseProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository
): ViewModel() {

    private val _projectsLoaded = MutableLiveData<UiState<List<Project>>>()
    val projectsLoaded: LiveData<UiState<List<Project>>>
        get() = _projectsLoaded

    fun loadServices() {
        _projectsLoaded.value = UiState.Loading
        repository.getProjects {
            _projectsLoaded.value = it
        }
    }
}