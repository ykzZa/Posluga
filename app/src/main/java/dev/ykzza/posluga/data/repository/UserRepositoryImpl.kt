package dev.ykzza.posluga.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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
            when(uiState) {
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

    override fun updateUserData(userId: String, updates: HashMap<String, Any>, result: (UiState<String>) -> Unit) {
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