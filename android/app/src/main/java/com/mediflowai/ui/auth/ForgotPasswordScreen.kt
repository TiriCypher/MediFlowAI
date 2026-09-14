package com.mediflowai.ui.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediflowai.R
import com.mediflowai.data.model.AuthResult
import com.mediflowai.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {

            is AuthResult.Success -> {
                successMessage =
                    "Password reset email sent. Please check your inbox."

                errorMessage = null
                viewModel.clearAuthState()
            }

            is AuthResult.Error -> {
                errorMessage =
                    (authState as AuthResult.Error).message

                successMessage = null
                viewModel.clearAuthState()
            }

            else -> Unit
        }
    }

    val isLoading = authState is AuthResult.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primary
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .statusBarsPadding()
        ) {

            /*
             * TOP BRANDING SECTION
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 28.dp,
                        vertical = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.mediflow_ai_logo
                        ),
                        contentDescription =
                            "MediFlow AI Logo",
                        modifier = Modifier.size(52.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "MediFlow AI",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Predict. Prioritize. Care.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            /*
             * MAIN CONTENT CARD
             */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.background
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 30.dp
                        )
                ) {

                    /*
                     * BACK BUTTON
                     */
                    IconButton(
                        onClick = {
                            viewModel.clearAuthState()
                            onBackToLogin()
                        },
                        modifier = Modifier.size(44.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Back to Login"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    /*
                     * HEADER ICON
                     */
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.LockReset,
                            contentDescription = null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer,
                            modifier =
                                Modifier.size(30.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = "Reset your password",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "No worries. Enter your registered email address and we'll send you a secure password reset link.",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )

                    /*
                     * EMAIL LABEL
                     */
                    Text(
                        text = "Email Address",
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    /*
                     * EMAIL FIELD
                     */
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                            successMessage = null
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text("name@example.com")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email
                            ),
                        shape =
                            RoundedCornerShape(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    /*
                     * SUCCESS MESSAGE
                     */
                    if (successMessage != null) {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(14.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .primaryContainer
                                )
                        ) {

                            Text(
                                text = successMessage!!,
                                modifier =
                                    Modifier.padding(16.dp),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimaryContainer,
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )
                    }

                    /*
                     * ERROR MESSAGE
                     */
                    if (errorMessage != null) {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(14.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                )
                        ) {

                            Text(
                                text = errorMessage!!,
                                modifier =
                                    Modifier.padding(16.dp),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer,
                                fontSize = 14.sp,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )
                    }

                    /*
                     * SEND BUTTON
                     */
                    Button(
                        onClick = {

                            when {

                                email.isBlank() -> {

                                    errorMessage =
                                        "Please enter your email address"

                                    successMessage = null
                                }

                                !Patterns
                                    .EMAIL_ADDRESS
                                    .matcher(email)
                                    .matches() -> {

                                    errorMessage =
                                        "Please enter a valid email address"

                                    successMessage = null
                                }

                                else -> {

                                    viewModel.resetPassword(
                                        email.trim()
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                            )
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(23.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text =
                                    "Send Reset Link",
                                fontSize = 16.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    /*
                     * BACK TO LOGIN
                     */
                    TextButton(
                        onClick = {
                            viewModel.clearAuthState()
                            onBackToLogin()
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Back to Login",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    /*
                     * SECURITY INFORMATION
                     */
                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(14.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                        ) {

                            Text(
                                text =
                                    "Secure password recovery",
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(5.dp)
                            )

                            Text(
                                text =
                                    "Use the reset link sent to your registered email to create a new password.",
                                fontSize = 13.sp,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text =
                            "Academic prototype • Not intended for clinical diagnosis or medical decision-making.",
                        modifier =
                            Modifier.fillMaxWidth(),
                        fontSize = 11.sp,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}