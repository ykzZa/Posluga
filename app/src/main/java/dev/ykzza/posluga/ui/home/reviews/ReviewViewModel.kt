package dev.ykzza.posluga.ui.home.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
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

    private val _userReviews = MutableLiveData<List<Review>>()
    val userReviews: LiveData<List<Review>>
        get() = _userReviews

    private val _addedReview = MutableLiveData<UiState<String>>()
    val addedReview: LiveData<UiState<String>>
        get() = _addedReview

    private val _authors = MutableLiveData<List<User>>()
    val authors: LiveData<List<User>>
        get() = _authors

    private val _reviewAuthorPairs = MutableLiveData<List<Pair<Review, User>>>()
    val reviewAuthorsPairs: LiveData<List<Pair<Review, User>>>
        get() = _reviewAuthorPairs

    fun getReviews(
        userId: String
    ) {
        reviewRepository.getUserReviews(
            userId
        ) {
            _userReviews.value = it
            if (it.isNotEmpty()) {
                getUsers(
                    it.map { reviewIt ->
                        reviewIt.authorId
                    }
                ) {
                    val zipped = _userReviews.value?.zip(_authors.value!!)
                    _reviewAuthorPairs.value = zipped ?: emptyList()
                }
            } else {
                _reviewAuthorPairs.value = emptyList()
            }
        }
    }

    private fun getUsers(
        usersId: List<String>,
        onEnd: () -> Unit
    ) {
        viewModelScope.launch {
            userRepository.getUsersListByIds(
                usersId
            ) {
                _authors.value = it
                onEnd.invoke()
            }
        }
    }

    fun addReview(
        userId: String,
        authorId: String,
        title: String,
        text: String,
        date: Timestamp,
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