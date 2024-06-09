package dev.ykzza.posluga.data.repository

import dev.ykzza.posluga.data.entities.Review
import dev.ykzza.posluga.util.UiState

interface ReviewRepository {

    fun writeUserReview(
        review: Review,
        result: (UiState<String>) -> Unit
    )

    fun getUserReviews(
        userId: String,
        result: (List<Review>) -> Unit
    )

}