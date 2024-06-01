package dev.ykzza.posluga.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): AuthRepository {

    override fun signUp(
        userData: User,
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                userData.id = authResult.user?.uid ?: ""
                saveUserToDb(
                    userData
                ) { uiState ->
                    when(uiState) {
                        is UiState.Success -> {
                            result.invoke(
                                UiState.Success(
                                    "Signed Up successfully!"
                                )
                            )
                        }
                        else -> {
                            UiState.Error(
                                "Failed to save data!"
                            )
                        }
                    }
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

    override fun signIn(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Signed In successfully!")
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

    override fun signInWithGoogle(
        account: GoogleSignInAccount,
        result: (UiState<String>) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { authTask ->
                if(authTask.isSuccessful) {
                    val isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        if (isNewUser == true) {
                            saveUserToDb(
                                User(
                                    firebaseUser.uid,
                                    firebaseUser.displayName.toString(),
                                    "",
                                    "",
                                    "",
                                    firebaseUser.photoUrl.toString()
                                )
                            ) { uiState ->
                                when(uiState) {
                                    is UiState.Success -> {
                                        result.invoke(
                                            UiState.Success(
                                                "Signed Up successfully!"
                                            )
                                        )
                                    }
                                    else -> {
                                        result.invoke(
                                            UiState.Error(
                                                "Failed to save data!"
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            result.invoke(
                                UiState.Success(
                                    "Signed In successfully!"
                                )
                            )
                        }
                    }
                }
            }
    }

    override fun signOut(result: () -> Unit) {
        auth.signOut()
        result.invoke()
    }

    override fun recoverPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Check your email for password recovery."
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

    override fun saveUserToDb(userData: User, result: (UiState<String>) -> Unit) {
        db.collection(Constants.USER_COLLECTION).document(userData.id).set(userData)
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