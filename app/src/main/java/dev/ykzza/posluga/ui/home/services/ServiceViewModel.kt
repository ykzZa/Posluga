package dev.ykzza.posluga.ui.home.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.data.repository.ServiceRepository
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _service = MutableLiveData<UiState<Service>>()
    val service: LiveData<UiState<Service>>
        get() = _service

    fun getService(serviceId: String) {
        _service.value = UiState.Loading
        repository.getService(
            serviceId
        ) {
            _service.value = it
        }
    }

    fun updateServiceState(
        userId: String,
        serviceId: String,
        changes: Boolean
    ) {
        userRepository.favouritesChange(
            userId,
            serviceId,
            changes
        ) {

        }
    }
}