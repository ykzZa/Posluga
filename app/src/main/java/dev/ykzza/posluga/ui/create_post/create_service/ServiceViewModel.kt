package dev.ykzza.posluga.ui.create_post.create_service

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.R
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.data.repository.ServiceRepository
import dev.ykzza.posluga.util.UiState
import dev.ykzza.posluga.util.getStringArrayEnglish
import dev.ykzza.posluga.util.isEnglish
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
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

    private val _servicePosted = MutableLiveData<UiState<String>>()
    val servicePosted: LiveData<UiState<String>>
        get() = _servicePosted

    private val _imagesUriList = MutableLiveData<List<Uri>>()
    val imagesUriList: LiveData<List<Uri>>
        get() = _imagesUriList

    private val _imagesUploaded = MutableLiveData<UiState<List<String>>>()
    val imagesUploaded: LiveData<UiState<List<String>>>
        get() = _imagesUploaded

    private val _service = MutableLiveData<UiState<Service>>()
    val service: LiveData<UiState<Service>>
        get() = _service

    private val _serviceImages = MutableLiveData<List<String>?>()
    val serviceImages: LiveData<List<String>?>
        get() = _serviceImages

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
        _serviceImages.value?.let {
            photosToRemove.addAll(it)
        }
        _serviceImages.value = null
        _imagesUriList.value = list
    }

    fun removeImage(imageUrl: Uri) {
        if (_serviceImages.value == null) {
            val list = _imagesUriList.value?.toMutableList()
            list?.remove(imageUrl)
            list?.let {
                _imagesUriList.value = it
            }
        } else {
            photosToRemove.add(imageUrl.toString())
            val list = _serviceImages.value?.toMutableList()
            list?.remove(imageUrl.toString())
            list?.let {
                _serviceImages.value = it
            }
        }
    }

    fun setServiceImages(list: List<String>) {
        _serviceImages.value = list
    }

    fun uploadImages(
        userId: String,
    ) {
        _servicePosted.value = UiState.Loading
        if (_serviceImages.value == null) {
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

    fun getService(
        serviceId: String
    ) {
        _service.value = UiState.Loading
        repository.getService(serviceId) {
            _service.value = it
        }
    }

    fun postService(
        serviceId: String?,
        title: String,
        description: String,
        authorId: String,
        date: String,
        price: String,
        listImages: List<String>
    ) {
        if (validateData(title, description)) {
            if (isEnglish(_category.value!!)) {
                val service = Service(
                    serviceId ?: "",
                    title,
                    description,
                    _category.value!!,
                    _subCategory.value!!,
                    authorId,
                    date,
                    price.toIntOrNull() ?: 0,
                    _state.value!!,
                    _city.value!!
                )
                repository.postService(
                    service,
                    listImages
                ) { uiState ->
                    _servicePosted.value = uiState
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
                val service = Service(
                    serviceId ?: "",
                    title,
                    description,
                    category,
                    subCategory,
                    authorId,
                    date,
                    price.toIntOrNull() ?: 0,
                    state,
                    city
                )
                repository.postService(
                    service,
                    listImages
                ) { uiState ->
                    _servicePosted.value = uiState
                }
            }
        }
    }

    private fun validateData(
        title: String,
        description: String
    ): Boolean {
        if (title.isBlank()) {
            _servicePosted.value = UiState.Error(
                "Title can't be blank"
            )
            return false
        }
        if (description.isBlank()) {
            _servicePosted.value = UiState.Error(
                "Description can't be blank"
            )
            return false
        }
        if (_category.value.isNullOrBlank() || _subCategory.value.isNullOrBlank()) {
            _servicePosted.value = UiState.Error(
                "You need to choose category"
            )
            return false
        }
        if (_state.value.isNullOrBlank() || _city.value.isNullOrBlank()) {
            _servicePosted.value = UiState.Error(
                "You need to choose geo of service"
            )
            return false
        }
        return true
    }
}