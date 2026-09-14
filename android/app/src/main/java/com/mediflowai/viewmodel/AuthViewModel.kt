package com.mediflowai.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mediflowai.data.auth.GoogleSignInManager
import com.mediflowai.data.model.AuthResult
import com.mediflowai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mediflowai.data.remote.RetrofitClient
import com.mediflowai.data.remote.BackendUser
import com.mediflowai.data.repository.UserRepository

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AuthRepository(
            FirebaseAuth.getInstance()
        )

    private val googleSignInManager =
        GoogleSignInManager()

    private val _authState =
        MutableStateFlow<AuthResult?>(null)

    val authState: StateFlow<AuthResult?> =
        _authState.asStateFlow()

    private val userRepository =
        UserRepository(
            RetrofitClient.apiService
        )

    private val _backendUser =
        MutableStateFlow<BackendUser?>(null)

    val backendUser: StateFlow<BackendUser?> =
        _backendUser.asStateFlow()

    private val _roleLoading =
        MutableStateFlow(false)

    val roleLoading: StateFlow<Boolean> =
        _roleLoading.asStateFlow()

    private val _roleError =
        MutableStateFlow<String?>(null)

    val roleError: StateFlow<String?> =
        _roleError.asStateFlow()

    fun register(
        email: String,
        password: String
    ) {
        viewModelScope.launch {

            _authState.value =
                AuthResult.Loading

            _authState.value =
                repository.register(
                    email,
                    password
                )
        }
    }

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {

            _authState.value =
                AuthResult.Loading

            _authState.value =
                repository.login(
                    email,
                    password
                )
        }
    }

    fun resetPassword(
        email: String
    ) {
        viewModelScope.launch {

            _authState.value =
                AuthResult.Loading

            _authState.value =
                repository.resetPassword(
                    email
                )
        }
    }

    fun signInWithGoogle(
        activity: Activity
    ) {

        viewModelScope.launch {

            _authState.value =
                AuthResult.Loading

            val result =
                googleSignInManager.signIn(
                    activity
                )

            _authState.value =
                if (result.isSuccess) {

                    AuthResult.Success

                } else {

                    AuthResult.Error(
                        result.exceptionOrNull()
                            ?.message
                            ?: "Google Sign-In failed."
                    )
                }
        }
    }

    fun logout() {

        repository.logout()

        _authState.value = null
        _backendUser.value = null
        _roleError.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun getCurrentUserEmail(): String? {
        return repository.getCurrentUserEmail()
    }

    fun clearAuthState() {
        _authState.value = null
    }

    fun syncBackendUser(
        onSuccess: (BackendUser) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        viewModelScope.launch {

            _roleLoading.value = true
            _roleError.value = null

            try {

                val user =
                    userRepository.syncCurrentUser()

                _backendUser.value = user

                onSuccess(user)

            } catch (e: Exception) {

                val message =
                    e.message
                        ?: "Unable to sync backend user."

                _roleError.value = message

                onError(message)

            } finally {

                _roleLoading.value = false
            }
        }
    }

    fun loadBackendUser(
        onSuccess: (BackendUser) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        viewModelScope.launch {

            _roleLoading.value = true
            _roleError.value = null

            try {

                val user =
                    userRepository.getCurrentUser()

                _backendUser.value = user

                onSuccess(user)

            } catch (e: Exception) {

                val message =
                    e.message
                        ?: "Unable to load backend user."

                _roleError.value = message

                onError(message)

            } finally {

                _roleLoading.value = false
            }
        }
    }

}

