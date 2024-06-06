package dev.ykzza.posluga.ui.home.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.data.repository.ServiceRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class BrowseServicesViewModel @Inject constructor(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _servicesLoaded = MutableLiveData<UiState<List<Service>>>()
    val servicesLoaded: LiveData<UiState<List<Service>>>
        get() = _servicesLoaded

    fun loadServices(
        searchQuery: String? = null,
        descriptionSearch: Boolean = false,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        category: String? = null,
        subCategory: String? = null,
        state: String? = null,
        city: String? = null
    ) {
        _servicesLoaded.value = UiState.Loading
        repository.getServices(
            searchQuery,
            descriptionSearch,
            minPrice,
            maxPrice,
            category,
            subCategory,
            state,
            city,
        ) {
            _servicesLoaded.value = it
        }
    }
}