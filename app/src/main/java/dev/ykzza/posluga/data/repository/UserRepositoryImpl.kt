package dev.ykzza.posluga.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: StorageReference
) : UserRepository {

    override fun getUserData(userId: String, result: (UiState<User>) -> Unit) {
        db.collection(Constants.USER_COLLECTION).document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    result.invoke(
                        UiState.Success(
                            user
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }


    override fun uploadImage(imageUri: Uri, userId: String, result: (UiState<String>) -> Unit) {
        val folder = Constants.PROFILE_PICTURES_FOLDER
        val imageRef = storage.child("$folder/$userId")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    result.invoke(
                        UiState.Success(
                            uri.toString()
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun updateUserDataWithImage(
        imageUri: Uri,
        userId: String,
        updates: HashMap<String, Any>,
        result: (UiState<String>) -> Unit
    ) {
        updateUserData(userId, updates) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    uploadImage(imageUri, userId) { uiStateImage ->
                        when (uiStateImage) {
                            is UiState.Success -> {
                                updateProfilePicUrl(
                                    uiStateImage.data,
                                    userId
                                ) { uiStateUpdateProfilePic ->
                                    when (uiStateUpdateProfilePic) {
                                        is UiState.Success -> {
                                            result.invoke(
                                                UiState.Success(
                                                    "Data updated"
                                                )
                                            )
                                        }

                                        else -> {
                                            result.invoke(
                                                UiState.Error(
                                                    "Failed to update data"
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            else -> {
                                result.invoke(
                                    UiState.Error(
                                        "Failed to upload picture"
                                    )
                                )
                            }
                        }
                    }
                }

                else -> {
                    result.invoke(
                        UiState.Error(
                            "Failed to update data"
                        )
                    )
                }
            }
        }
    }

    override fun updateProfilePicUrl(
        imageUri: String,
        userId: String,
        result: (UiState<String>) -> Unit
    ) {
        val imageUrlUpdate = hashMapOf<String, Any>(
            "photoUrl" to imageUri
        )
        db.collection(Constants.USER_COLLECTION).document(userId).update(imageUrlUpdate)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Image updated"
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

    override fun removeProfilePic(userId: String, result: (UiState<String>) -> Unit) {
        val updates = hashMapOf<String, Any>(
            "photoUrl" to ""
        )
        updateUserData(userId, updates) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    result.invoke(
                        UiState.Success(
                            "Picture deleted successfully"
                        )
                    )
                }

                else -> {
                    result.invoke(
                        UiState.Error(
                            "Oops, something went wrong"
                        )
                    )
                }
            }
        }
    }

    override fun getUserServiceCount(userId: String, result: (UiState<Int>) -> Unit) {
        db.collection(Constants.SERVICE_COLLECTION)
            .whereEqualTo("authorId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                val serviceCount = querySnapshot.size()
                result.invoke(
                    UiState.Success(
                        serviceCount
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        "Failed to load services"
                    )
                )
            }
    }

    override fun getUserProjectCount(userId: String, result: (UiState<Int>) -> Unit) {
        db.collection(Constants.PROJECT_COLLECTION)
            .whereEqualTo("authorId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                val serviceCount = querySnapshot.size()
                result.invoke(
                    UiState.Success(
                        serviceCount
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        "Failed to load projects"
                    )
                )
            }
    }

    override fun getUserReviewsCount(userId: String, result: (UiState<Int>) -> Unit) {
        db.collection(Constants.REVIEWS_COLLECTION)
            .whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                val serviceCount = querySnapshot.size()
                result.invoke(
                    UiState.Success(
                        serviceCount
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        "Failed to load reviews"
                    )
                )
            }
    }

    override fun isServiceFavourite(
        userId: String,
        serviceId: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        db.collection(Constants.USER_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    result.invoke(
                        UiState.Success(
                            user.favourites.contains(serviceId)
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun favouritesChange(
        userId: String,
        serviceId: String,
        changes: Boolean,
        result: (UiState<String>) -> Unit
    ) {
        getUserData(
            userId
        ) { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    val user = uiState.data
                    val list = user.favourites.toMutableList()
                    if(changes) {
                        list.add(serviceId)
                    } else {
                        list.remove(serviceId)
                    }
                    db.collection(Constants.USER_COLLECTION).document(userId)
                        .update("favourites", list)
                        .addOnSuccessListener {
                            result.invoke(
                                UiState.Success(
                                    "State changed"
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
                else -> {
                    result.invoke(
                        UiState.Error(
                            "Failed to get user data"
                        )
                    )
                }
            }
        }
    }

    override suspend fun getUsersListByIds(
        usersId: List<String>,
        result: (List<User>) -> Unit
    ) {
        val users = mutableListOf<User>()
        for (userId in usersId) {
            val document = db.collection(Constants.USER_COLLECTION).document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    users.add(user)
                }
            }
        }
        if (users.size == usersId.size) {
            result.invoke(
                users
            )
        } else {
            result.invoke(
                emptyList()
            )
        }
    }

    override fun updateUserData(
        userId: String,
        updates: HashMap<String, Any>,
        result: (UiState<String>) -> Unit
    ) {
        db.collection(Constants.USER_COLLECTION).document(userId).update(updates)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Data updated successfully.")
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