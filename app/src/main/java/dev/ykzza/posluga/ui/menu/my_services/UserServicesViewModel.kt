package dev.ykzza.posluga.ui.menu.my_services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.data.repository.ServiceRepository
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserServicesViewModel @Inject constructor(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _userServices = MutableLiveData<UiState<List<Service>>>()
    val userServices: LiveData<UiState<List<Service>>>
        get() = _userServices

    private val _serviceDeleted = MutableLiveData<UiState<String>>()
    val serviceDeleted: LiveData<UiState<String>>
        get() = _serviceDeleted

    fun getUserServices(
        userId: String
    ) {
        _userServices.value = UiState.Loading
        repository.getUserServices(
            userId
        ) {
            _userServices.value = it
        }
    }

    fun deleteService(
        serviceId: String
    ) {
        _serviceDeleted.value = UiState.Loading
        repository.getService(serviceId) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    viewModelScope.launch {
                        repository.deleteImages(uiState.data.images) {
                            repository.deleteService(
                                serviceId
                            ) {
                                _serviceDeleted.value = it
                            }
                        }
                    }
                }

                else -> {
                    _serviceDeleted.value = UiState.Error("Failed to delete service!")
                }
            }
        }
    }
}