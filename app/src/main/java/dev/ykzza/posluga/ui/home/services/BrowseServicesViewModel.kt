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
): ViewModel() {

    private val _servicesLoaded = MutableLiveData<UiState<List<Service>>>()
    val servicesLoaded: LiveData<UiState<List<Service>>>
        get() = _servicesLoaded

    fun loadServices() {
        _servicesLoaded.value = UiState.Loading
        repository.getServices {
            _servicesLoaded.value = it
        }
    }
}