package com.mediflowai.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response


class FirebaseAuthInterceptor : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val currentUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (currentUser == null) {
            return chain.proceed(
                chain.request()
            )
        }

        val token = runBlocking {

            currentUser
                .getIdToken(false)
                .await()
                .token
        }

        if (token.isNullOrBlank()) {
            return chain.proceed(
                chain.request()
            )
        }

        val authenticatedRequest =
            chain.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()

        return chain.proceed(
            authenticatedRequest
        )
    }
}