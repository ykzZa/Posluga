package dev.ykzza.posluga.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.ykzza.posluga.data.entities.Review
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState

class ReviewRepositoryImpl(
    private val db: FirebaseFirestore
) : ReviewRepository {


    override fun writeUserReview(
        review: Review,
        result: (UiState<String>) -> Unit
    ) {
        val document = db.collection(Constants.REVIEWS_COLLECTION).document()
        review.reviewId = document.id
        document.set(review)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Review has been added successfully."
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun getUserReviews(userId: String, result: (UiState<List<Review>>) -> Unit) {
        db.collection(Constants.REVIEWS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reviews = mutableListOf<Review>()
                for (document in querySnapshot) {
                    val review = document.toObject(Review::class.java)
                    reviews.add(review)
                }
                result.invoke(
                    UiState.Success(
                        reviews
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }


}