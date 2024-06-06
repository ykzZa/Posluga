package dev.ykzza.posluga.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchDialogViewModel @Inject constructor(): ViewModel() {

    val category = MutableLiveData<String?>()
    val subCategory = MutableLiveData<String?>()

    val state = MutableLiveData<String?>()
    val city = MutableLiveData<String?>()

    fun setCategory(searchCategory: String) {
        category.value = searchCategory
        subCategory.value = null
    }

    fun setState(searchState: String) {
        state.value = searchState
        city.value = null
    }
}