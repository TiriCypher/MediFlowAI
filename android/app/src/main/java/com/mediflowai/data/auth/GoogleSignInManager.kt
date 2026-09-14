package com.mediflowai.data.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mediflowai.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleSignInManager {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signIn(
        activity: Activity
    ): Result<Unit> {

        return try {

            val credentialManager =
                CredentialManager.create(activity)

            val googleIdOption =
                GetGoogleIdOption.Builder()
                    .setServerClientId(
                        activity.getString(
                            R.string.default_web_client_id
                        )
                    )
                    .setFilterByAuthorizedAccounts(false)
                    .build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(
                        googleIdOption
                    )
                    .build()

            val result =
                credentialManager.getCredential(
                    context = activity,
                    request = request
                )

            val credential =
                result.credential

            if (
                credential is CustomCredential &&
                credential.type ==
                GoogleIdTokenCredential
                    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleIdTokenCredential =
                    GoogleIdTokenCredential
                        .createFrom(
                            credential.data
                        )

                val firebaseCredential =
                    GoogleAuthProvider
                        .getCredential(
                            googleIdTokenCredential.idToken,
                            null
                        )

                firebaseAuth
                    .signInWithCredential(
                        firebaseCredential
                    )
                    .await()

                Result.success(Unit)

            } else {

                Result.failure(
                    Exception(
                        "Invalid Google credential."
                    )
                )
            }

        } catch (e: GoogleIdTokenParsingException) {

            Result.failure(
                Exception(
                    "Unable to process Google account."
                )
            )

        } catch (e: GetCredentialException) {

            if (e is CancellationException) {
                throw e
            }

            Result.failure(
                Exception(
                    e.message
                        ?: "Google Sign-In was cancelled or failed."
                )
            )

        } catch (e: Exception) {

            if (e is CancellationException) {
                throw e
            }

            Result.failure(
                Exception(
                    e.message
                        ?: "Google Sign-In failed."
                )
            )
        }
    }
}

