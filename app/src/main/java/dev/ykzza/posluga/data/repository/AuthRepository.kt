package dev.ykzza.posluga.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.util.UiState

interface AuthRepository {

    fun signUp(
        userData: User,
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    )

    fun signIn(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    )

    fun signInWithGoogle(
        account: GoogleSignInAccount,
        result: (UiState<String>) -> Unit
    )

    fun signOut(
        result: () -> Unit
    )

    fun recoverPassword(
        email: String,
        result: (UiState<String>) -> Unit
    )

    fun saveUserToDb(
        userData: User,
        result: (UiState<String>) -> Unit
    )
}
