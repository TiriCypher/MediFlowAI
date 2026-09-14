package com.mediflowai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.mediflowai.data.remote.ApiService
import com.mediflowai.data.remote.BackendUser
import com.mediflowai.data.remote.UserSyncRequest

class UserRepository(
    private val apiService: ApiService
) {

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    suspend fun syncCurrentUser(): BackendUser {

        val firebaseUser =
            firebaseAuth.currentUser
                ?: throw IllegalStateException(
                    "No Firebase user is signed in."
                )

        val email =
            firebaseUser.email
                ?: throw IllegalStateException(
                    "Firebase user email is missing."
                )

        val name =
            firebaseUser.displayName
                ?.takeIf { it.isNotBlank() }
                ?: email.substringBefore("@")

        return apiService.syncUser(
            request = UserSyncRequest(
                name = name,
                email = email
            )
        )
    }

    suspend fun getCurrentUser(): BackendUser {

        return apiService.getCurrentUser()
    }
}