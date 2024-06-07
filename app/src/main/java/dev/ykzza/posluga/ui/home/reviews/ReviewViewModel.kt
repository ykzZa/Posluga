package dev.ykzza.posluga.ui.home.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.Review
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.data.repository.ReviewRepository
import dev.ykzza.posluga.data.repository.UserRepository
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userReviews = MutableLiveData<UiState<List<Review>>>()
    val userReviews: LiveData<UiState<List<Review>>>
        get() = _userReviews

    private val _addedReview = MutableLiveData<UiState<String>>()
    val addedReview: LiveData<UiState<String>>
        get() = _addedReview

    private val _authors = MutableLiveData<UiState<List<User>>>()
    val authors: LiveData<UiState<List<User>>>
        get() = _authors

    fun getReviews(
        userId: String
    ) {
        _userReviews.value = UiState.Loading
        reviewRepository.getUserReviews(
            userId
        ) {
            _userReviews.value = it
        }
    }

    fun getUsers(
        usersId: List<String>
    ) {
        viewModelScope.launch {
            userRepository.getReviewsAuthors(
                usersId
            ) {
                _authors.value = it
            }
        }
    }

    fun addReview(
        userId: String,
        authorId: String,
        title: String,
        text: String,
        date: String,
        rating: Int
    ) {
        val review = Review(
            "",
            userId,
            authorId,
            rating,
            title,
            text,
            date
        )
        reviewRepository.writeUserReview(
            review
        ) {
            _addedReview.value = it
        }
    }
}