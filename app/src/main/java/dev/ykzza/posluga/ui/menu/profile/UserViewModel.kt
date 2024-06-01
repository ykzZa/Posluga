package dev.ykzza.posluga.ui.menu.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _dataUpdated = MutableLiveData<UiState<String>>()
    val dataUpdated: LiveData<UiState<String>>
        get() = _dataUpdated

    private val _pictureDeleted = MutableLiveData<UiState<String>>()
    val pictureDeleted: LiveData<UiState<String>>
        get() = _pictureDeleted

    private val _user = MutableLiveData<UiState<User>>()
    val user: LiveData<UiState<User>>
        get() = _user

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    fun removeProfilePicture(userId: String) {
        _pictureDeleted.value = UiState.Loading
        repository.removeProfilePic(userId) {
            _pictureDeleted.value = it
        }
    }

    fun updateUser(userId: String, updates: HashMap<String, Any>) {
        _dataUpdated.value = UiState.Loading
        if(imageUri.value == null) {
            repository.updateUserData(
                userId,
                updates
            ) {
                _dataUpdated.value = it
            }
        } else {
            repository.updateUserDataWithImage(
                imageUri.value!!,
                userId,
                updates
            ) {
                _dataUpdated.value = it
            }
        }
    }

    fun getUserData(userId: String) {
        _user.value = UiState.Loading
        repository.getUserData(
            userId
        ) {
            _user.value = it
        }
    }

    fun saveImageUri(imageUri: Uri) {
        _imageUri.value = imageUri
    }
}