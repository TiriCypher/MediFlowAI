/*package com.mediflowai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mediflowai.navigation.MediFlowNavGraph
import com.mediflowai.ui.theme.MediFlowAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            MediFlowAITheme {
                MediFlowNavGraph()
            }
        }
    }
}*/

package com.mediflowai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.mediflowai.navigation.MediFlowNavGraph
import com.mediflowai.ui.theme.MediFlowAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseAuth.getInstance()
            .currentUser
            ?.getIdToken(true)
            ?.addOnSuccessListener { result ->

                Log.d(
                    "MEDIFLOW_TOKEN",
                    result.token ?: "NO_TOKEN"
                )
            }
            ?.addOnFailureListener { error ->

                Log.e(
                    "MEDIFLOW_TOKEN",
                    "Failed to get token",
                    error
                )
            }

        setContent {
            MediFlowAITheme {
                MediFlowNavGraph()
            }
        }
    }
}