package dev.ykzza.posluga.ui.create_post.create_project

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.data.repository.ProjectRepository
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.getStringArrayEnglish
import dev.ykzza.posluga.util.isEnglish
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val application: Application
) : ViewModel() {

    @Inject
    @Named("categories")
    lateinit var categories: Array<String>

    @Inject
    @Named("subCategories")
    lateinit var subCategories: Array<Int>

    @Inject
    @Named("states")
    lateinit var states: Array<String>

    @Inject
    @Named("cities")
    lateinit var cities: Array<Int>

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

    private val _project = MutableLiveData<UiState<Project>>()
    val project: LiveData<UiState<Project>>
        get() = _project

    private val _projectImages = MutableLiveData<List<String>?>()
    val projectImages: LiveData<List<String>?>
        get() = _projectImages

    private val photosToRemove = mutableSetOf<String>()

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
        _projectImages.value?.let {
            photosToRemove.addAll(it)
        }
        _projectImages.value = null
        _imagesUriList.value = list
    }

    fun removeImage(imageUrl: Uri) {
        if (_projectImages.value == null) {
            val list = _imagesUriList.value?.toMutableList()
            list?.remove(imageUrl)
            list?.let {
                _imagesUriList.value = it
            }
        } else {
            photosToRemove.add(imageUrl.toString())
            val list = _projectImages.value?.toMutableList()
            list?.remove(imageUrl.toString())
            list?.let {
                _projectImages.value = it
            }
        }
    }

    fun setProjectImages(list: List<String>) {
        _projectImages.value = list
    }

    fun uploadImages(
        userId: String,
    ) {
        _projectPosted.value = UiState.Loading
        if (_projectImages.value == null) {
            viewModelScope.launch {
                repository.uploadImages(
                    userId,
                    _imagesUriList.value ?: emptyList()
                ) {
                    _imagesUploaded.value = it
                }
                repository.deleteImages(
                    photosToRemove.toList()
                ) {}
            }
        } else {
            viewModelScope.launch {
                repository.deleteImages(
                    photosToRemove.toList()
                ) {
                    _imagesUploaded.value = it
                }
            }
        }
    }

    fun getProject(
        projectId: String
    ) {
        _project.value = UiState.Loading
        repository.getProject(projectId) {
            _project.value = it
        }
    }

    fun postProject(
        projectId: String?,
        title: String,
        description: String,
        authorId: String,
        date: String,
        price: String,
        listImages: List<String>
    ) {
        if (validateData(title, description)) {
            if (isEnglish(_category.value!!)) {
                val project = Project(
                    projectId ?: "",
                    title,
                    description,
                    _category.value!!,
                    _subCategory.value!!,
                    authorId,
                    Timestamp.now(),
                    price.toIntOrNull() ?: 0,
                    _state.value!!,
                    _city.value!!
                )
                repository.postProject(
                    project,
                    listImages
                ) { uiState ->
                    _projectPosted.value = uiState
                }
            } else {
                val categoryIndex =
                    categories.indexOf(_category.value)
                val category = getStringArrayEnglish(application, R.array.categories)[categoryIndex]
                val subCategoryArray = subCategories[categoryIndex]
                val subCategoryIndex =
                    application.resources.getStringArray(subCategoryArray)
                        .indexOf(_subCategory.value)
                val subCategory =
                    getStringArrayEnglish(application, subCategoryArray)[subCategoryIndex]
                val stateIndex =
                    states.indexOf(_state.value)
                val state = getStringArrayEnglish(application, R.array.states)[stateIndex]
                val citiesArray = cities[stateIndex]
                val cityIndex =
                    application.resources.getStringArray(citiesArray)
                        .indexOf(_city.value)
                val city = getStringArrayEnglish(application, citiesArray)[cityIndex]
                val project = Project(
                    projectId ?: "",
                    title,
                    description,
                    category,
                    subCategory,
                    authorId,
                    Timestamp.now(),
                    price.toIntOrNull() ?: 0,
                    state,
                    city
                )
                repository.postProject(
                    project,
                    listImages
                ) { uiState ->
                    _projectPosted.value = uiState
                }
            }
        }
    }

    private fun validateData(
        title: String,
        description: String
    ): Boolean {
        if (title.isBlank()) {
            _projectPosted.value = UiState.Error(
                "Title can't be blank"
            )
            return false
        }
        if (description.isBlank()) {
            _projectPosted.value = UiState.Error(
                "Description can't be blank"
            )
            return false
        }
        if (_category.value.isNullOrBlank() || _subCategory.value.isNullOrBlank()) {
            _projectPosted.value = UiState.Error(
                "You need to choose category"
            )
            return false
        }
        if (_state.value.isNullOrBlank() || _city.value.isNullOrBlank()) {
            _projectPosted.value = UiState.Error(
                "You need to choose geo of service"
            )
            return false
        }
        return true
    }
}