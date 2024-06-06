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

    fun loadProjects(
        searchQuery: String? = null,
        descriptionSearch: Boolean = false,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        category: String? = null,
        subCategory: String? = null,
        state: String? = null,
        city: String? = null
    ) {
        _projectsLoaded.value = UiState.Loading
        repository.getProjects(
            searchQuery,
            descriptionSearch,
            minPrice,
            maxPrice,
            category,
            subCategory,
            state,
            city,
        ) {
            _projectsLoaded.value = it
        }
    }
}