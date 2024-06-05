package dev.ykzza.posluga.ui.create_post.create_project

import android.net.Uri
import androidx.core.text.isDigitsOnly
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
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
): ViewModel() {

    private val _category = MutableLiveData<String>()
    private val _subCategory = MutableLiveData<String>()

    private val _state = MutableLiveData<String>()
    private val _city = MutableLiveData<String>()

    private val _projectPosted = MutableLiveData<UiState<String>>()
    val projectPosted: LiveData<UiState<String>>
        get() = _projectPosted

    private val _imagesUriList = MutableLiveData<List<Uri>>()
    val imagesUriList: LiveData<List<Uri>>
        get() = _imagesUriList

    private val _imagesUploaded = MutableLiveData<UiState<List<String>>>()
    val imagesUploaded: LiveData<UiState<List<String>>>
        get() = _imagesUploaded

    fun setCategory(category: String) {
        _category.value = category
        _subCategory.value = ""
    }

    fun setSubCategory(subCategory: String) {
        _subCategory.value = subCategory
    }

    fun setState(state: String) {
        _state.value = state
        _city.value = ""
    }

    fun setCity(city: String) {
        _city.value = city
    }

    fun setImagesUri(list: List<Uri>) {
        _imagesUriList.value = list
    }

    fun removeImage(imageUrl: Uri) {
        val list = _imagesUriList.value?.toMutableList()
        list?.remove(imageUrl)
        list?.let {
            _imagesUriList.value = it
        }
    }

    fun uploadImages(
        userId: String,
    ) {
        _projectPosted.value = UiState.Loading
        viewModelScope.launch {
            repository.uploadImages(
                userId,
                _imagesUriList.value ?: emptyList()
            ) {
                _imagesUploaded.value = it
            }
        }
    }

    fun postProject(
        title: String,
        description: String,
        authorId: String,
        date: String,
        price: String,
        listImages: List<String>
    ) {
        if(validateData(title, description, price)) {
            val project = Project(
                "",
                title,
                description,
                _category.value!!,
                _subCategory.value!!,
                authorId,
                date,
                price.toInt(),
                _state.value!!,
                _city.value!!
            )
            repository.postProject(
                project,
                listImages
            ) { uiState ->
                _projectPosted.value = uiState
            }
        }
    }

    private fun validateData(
        title: String,
        description: String,
        price: String
    ): Boolean {
        if(title.isBlank()) {
            _projectPosted.value = UiState.Error(
                "Title can't be blank"
            )
            return false
        }
        if(description.isBlank()) {
            _projectPosted.value = UiState.Error(
                "Description can't be blank"
            )
            return false
        }
        if(_category.value.isNullOrBlank() || _subCategory.value.isNullOrBlank()) {
            _projectPosted.value = UiState.Error(
                "You need to choose category"
            )
            return false
        }
        if(_state.value.isNullOrBlank() || _city.value.isNullOrBlank()) {
            _projectPosted.value = UiState.Error(
                "You need to choose geo of service"
            )
            return false
        }
        if(price.isBlank() || !price.isDigitsOnly()) {
            _projectPosted.value = UiState.Error(
                "You need to enter a price"
            )
            return false
        }
        return true
    }
}