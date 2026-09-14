package com.mediflowai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.mediflowai.data.model.AuthResult
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun register(
        email: String,
        password: String
    ): AuthResult {
        return try {
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(
                e.message ?: "Registration failed"
            )
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): AuthResult {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(
                e.message ?: "Login failed"
            )
        }
    }

    suspend fun resetPassword(
        email: String
    ): AuthResult {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(
                e.message ?: "Unable to send password reset email"
            )
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
}