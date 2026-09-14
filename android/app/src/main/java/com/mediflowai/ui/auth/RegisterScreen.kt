package com.mediflowai.ui.auth

import android.app.Activity
import android.util.Patterns
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediflowai.R
import com.mediflowai.data.model.AuthResult
import com.mediflowai.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val authState by viewModel.authState.collectAsState()
    val roleLoading by viewModel.roleLoading.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity


    LaunchedEffect(authState) {

        when (authState) {

            is AuthResult.Success -> {

                errorMessage = null

                viewModel.syncBackendUser(

                    onSuccess = {
                        viewModel.clearAuthState()
                        onRegisterSuccess()
                    },

                    onError = { message ->
                        errorMessage = message
                    }
                )
            }

            is AuthResult.Error -> {

                errorMessage =
                    (authState as AuthResult.Error).message
            }

            else -> Unit
        }
    }

    val isLoading =
        authState is AuthResult.Loading || roleLoading

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 28.dp,
                        vertical = 28.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .background(Color.White),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(
                            id =
                                R.drawable.mediflow_ai_logo
                        ),
                        contentDescription =
                            "MediFlow AI Logo",
                        modifier =
                            Modifier.size(52.dp),
                        tint =
                            Color.Unspecified
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text = "MediFlow AI",
                    fontSize = 26.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Smart healthcare, intelligently connected",
                    fontSize = 13.sp,
                    color =
                        Color.White.copy(alpha = 0.8f)
                )
            }

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .background
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 30.dp
                        )
                ) {

                    Text(
                        text =
                            "Create your account",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Get started with MediFlow AI",
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Spacer(
                        modifier =
                            Modifier.height(26.dp)
                    )

                    Text(
                        text =
                            "Email Address",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "name@example.com"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email
                            ),
                        shape =
                            RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    Text(
                        text = "Password",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Minimum 6 characters"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    passwordVisible =
                                        !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (passwordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                    contentDescription =
                                        "Toggle password visibility"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        shape =
                            RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    )

                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )

                    Text(
                        text =
                            "Confirm Password",
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value =
                            confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Re-enter your password"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {

                                    confirmPasswordVisible =
                                        !confirmPasswordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (
                                            confirmPasswordVisible
                                        ) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                    contentDescription =
                                        "Toggle password visibility"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation =
                            if (
                                confirmPasswordVisible
                            ) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        shape =
                            RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    )

                    if (errorMessage != null) {

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                                ),
                            shape =
                                RoundedCornerShape(12.dp)
                        ) {

                            Text(
                                text = errorMessage!!,
                                modifier =
                                    Modifier.padding(14.dp),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onErrorContainer
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Button(
                        onClick = {

                            when {

                                email.isBlank() -> {
                                    errorMessage =
                                        "Please enter your email"
                                }

                                !Patterns
                                    .EMAIL_ADDRESS
                                    .matcher(email)
                                    .matches() -> {

                                    errorMessage =
                                        "Please enter a valid email address"
                                }

                                password.length < 6 -> {
                                    errorMessage =
                                        "Password must contain at least 6 characters"
                                }

                                password !=
                                        confirmPassword -> {

                                    errorMessage =
                                        "Passwords do not match"
                                }

                                else -> {

                                    viewModel.register(
                                        email.trim(),
                                        password
                                    )
                                }
                            }
                        },
                        modifier =
                            Modifier
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
                                    Modifier.size(24.dp),
                                color =
                                    Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text =
                                    "Create Account",
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Column(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier =
                                Modifier.fillMaxWidth(),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            HorizontalDivider()

                            Text(
                                text = "  OR  ",
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                                modifier =
                                    Modifier.background(
                                        MaterialTheme
                                            .colorScheme
                                            .background
                                    ),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            errorMessage = null
                            activity?.let {
                                viewModel.signInWithGoogle(it)
                            }
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        enabled = !isLoading,
                        shape =
                            RoundedCornerShape(16.dp)
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text = "G",
                                fontSize = 20.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.size(10.dp)
                            )

                            Text(
                                text =
                                    "Continue with Google",
                                fontWeight =
                                    FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )

                    Column(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "Already have an account?",
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )

                        TextButton(
                            onClick =
                                onLoginClick,
                            enabled = !isLoading
                        ) {

                            Text(
                                text =
                                    "Sign in instead",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Academic prototype • Not intended for clinical diagnosis or medical decision-making.",
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

