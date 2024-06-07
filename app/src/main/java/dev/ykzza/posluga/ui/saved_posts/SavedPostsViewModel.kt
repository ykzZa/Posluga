package dev.ykzza.posluga.ui.saved_posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.data.repository.ServiceRepository
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPostsViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    private val _savedServices = MutableLiveData<UiState<List<Service>>>()
    val savedServices: LiveData<UiState<List<Service>>>
        get() = _savedServices

    private val _user = MutableLiveData<UiState<User>>()
    val user: LiveData<UiState<User>>
        get() = _user

    fun getServicesByIds(
        idsList: List<String>
    ) {
        _savedServices.value = UiState.Loading
        viewModelScope.launch {
            repository.getServicesByIds(
                idsList
            ) {
                _savedServices.value = it
            }
        }
    }

    fun getUser(userId: String) {
        _user.value = UiState.Loading
        userRepository.getUserData(
            userId
        ) {
            _user.value = it
        }
    }
}