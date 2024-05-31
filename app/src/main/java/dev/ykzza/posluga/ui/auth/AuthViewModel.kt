package dev.ykzza.posluga.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ykzza.posluga.data.entities.User
import dev.ykzza.posluga.data.repository.AuthRepository
import dev.ykzza.posluga.util.UiState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _signUp = MutableLiveData<UiState<String>>()
    val signUp: LiveData<UiState<String>>
        get() = _signUp

    private val _signIn = MutableLiveData<UiState<String>>()
    val signIn: LiveData<UiState<String>>
        get() = _signIn

    private val _signInWithGoogle = MutableLiveData<UiState<String>>()
    val signInWithGoogle: LiveData<UiState<String>>
        get() = _signInWithGoogle

    private val _recoverPassword = MutableLiveData<UiState<String>>()
    val recoverPassword: LiveData<UiState<String>>
        get() = _recoverPassword

    fun signIn(email: String, password: String) {
        _signIn.value = UiState.Loading
        if (email.isNotBlank() && password.isNotBlank()) {
            repository.signIn(email, password) {
                _signIn.value = it
            }
        } else {
            _signIn.value = UiState.Error(
                "Fields must not be blank"
            )
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        _signInWithGoogle.value = UiState.Loading
        repository.signInWithGoogle(account) {
            _signInWithGoogle.value = it
        }
    }

    fun signUp(userData: User, email: String, password: String) {
        _signUp.value = UiState.Loading
        if (validateSignUpData(userData, email, password)) {
            repository.signUp(
                userData,
                email,
                password
            ) {
                _signUp.value = it
            }
        }
    }

    fun recoverPassword(email: String) {
        _recoverPassword.value = UiState.Loading
        if (email.isNotBlank()) {
            repository.recoverPassword(email) {
                _recoverPassword.value = it
            }
        } else {
            _recoverPassword.value = UiState.Error(
                "Email can't be blank."
            )
        }
    }

    private fun validateSignUpData(userData: User, email: String, password: String): Boolean {
        if (userData.nickname.isBlank()) {
            _signUp.value = UiState.Error("Nickname can't be blank.")
            return false
        }
        if (password.isBlank() || password.length < 6) {
            _signUp.value = UiState.Error("Password is too short.")
            return false
        }
        if (email.isBlank()) {
            _signUp.value = UiState.Error("Invalid email.")
            return false
        }
        return true
    }

    fun signOut(result: () -> Unit) {
        repository.signOut(result)
    }
}