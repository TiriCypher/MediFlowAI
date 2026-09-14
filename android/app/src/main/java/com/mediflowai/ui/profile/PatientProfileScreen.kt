package com.mediflowai.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.navigation.PatientBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.AuthViewModel
import com.mediflowai.viewmodel.PatientViewModel


private val ProfileNavy =
    Color(0xFF081225)

private val ProfileTeal =
    Color(0xFF00A6A6)

private val ProfileBackground =
    Color(0xFFF5F8FB)

private val ProfileMuted =
    Color(0xFF64748B)

private val ProfileRed =
    Color(0xFFDC2626)


@Composable
fun PatientProfileScreen(
    onNavigate: (String) -> Unit,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    patientViewModel: PatientViewModel = viewModel()
) {

    val state by
    patientViewModel.uiState.collectAsState()

    val backendUser by
    authViewModel.backendUser.collectAsState()

    val patient =
        state.patient


    var name by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var medicalInfo by remember {
        mutableStateOf("")
    }


    LaunchedEffect(patient?.id) {

        patient?.let {

            name =
                it.name

            phone =
                it.phone.orEmpty()

            address =
                it.address.orEmpty()

            medicalInfo =
                it.medical_information
                    .orEmpty()
        }
    }


    Scaffold(
        containerColor =
            ProfileBackground,

        bottomBar = {

            PatientBottomBar(
                currentRoute =
                    Screen.PatientProfile.route,
                onNavigate =
                    onNavigate
            )
        }
    ) { paddingValues ->


        if (
            state.isLoading &&
            patient == null
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator()
            }

            return@Scaffold
        }


        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 20.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    18.dp
                )
        ) {


            // -----------------------------------------
            // HEADER
            // -----------------------------------------

            Text(
                text =
                    "My Profile",
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )

            Text(
                text =
                    "Manage your personal and account information.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    ProfileMuted
            )


            // -----------------------------------------
            // PROFILE SUMMARY
            // -----------------------------------------

            patient?.let {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                ProfileNavy
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                20.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Box(
                            modifier =
                                Modifier
                                    .size(
                                        58.dp
                                    )
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.12f
                                        ),
                                        CircleShape
                                    ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    it.name
                                        .firstOrNull()
                                        ?.uppercase()
                                        ?: "P",

                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineMedium,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }


                        Column(
                            modifier =
                                Modifier.padding(
                                    start = 16.dp
                                )
                        ) {

                            Text(
                                text =
                                    it.name,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )


                            Text(
                                text =
                                    backendUser?.email
                                        ?: "Patient account",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,

                                color =
                                    Color.White.copy(
                                        alpha = 0.75f
                                    )
                            )


                            Text(
                                text =
                                    "Patient ID #${it.id}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,

                                color =
                                    Color.White.copy(
                                        alpha = 0.65f
                                    )
                            )
                        }
                    }
                }
            }


            // -----------------------------------------
            // MEDICAL PROFILE
            // -----------------------------------------

            Text(
                text =
                    "Medical Profile",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )


            patient?.let {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            20.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            ),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                14.dp
                            )
                    ) {

                        ProfileInfoRow(
                            label =
                                "Blood Group",
                            value =
                                it.blood_group
                                    ?: "Not available"
                        )

                        ProfileInfoRow(
                            label =
                                "Gender",
                            value =
                                it.gender
                                    ?: "Not available"
                        )

                        ProfileInfoRow(
                            label =
                                "Date of Birth",
                            value =
                                it.date_of_birth
                                    ?: "Not available"
                        )
                    }
                }
            }


            // -----------------------------------------
            // EDITABLE INFORMATION
            // -----------------------------------------

            Text(
                text =
                    "Personal Information",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            18.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {


                    OutlinedTextField(
                        value =
                            name,

                        onValueChange = {
                            name = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Full Name")
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            phone,

                        onValueChange = {
                            phone = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Phone Number")
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            address,

                        onValueChange = {
                            address = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Address")
                        },

                        minLines =
                            2,

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            medicalInfo,

                        onValueChange = {
                            medicalInfo = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                "Medical Information"
                            )
                        },

                        minLines =
                            3,

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    Button(
                        onClick = {

                            patientViewModel
                                .updateProfile(
                                    name =
                                        name.trim(),

                                    phone =
                                        phone
                                            .trim()
                                            .ifBlank {
                                                null
                                            },

                                    address =
                                        address
                                            .trim()
                                            .ifBlank {
                                                null
                                            },

                                    medicalInformation =
                                        medicalInfo
                                            .trim()
                                            .ifBlank {
                                                null
                                            }
                                )
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        enabled =
                            !state.isSaving &&
                                    name.isNotBlank(),

                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),

                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        ProfileNavy
                                )
                    ) {

                        Text(
                            if (
                                state.isSaving
                            ) {
                                "Saving..."
                            } else {
                                "Save Changes"
                            }
                        )
                    }


                    if (
                        state.saveSuccess
                    ) {

                        Text(
                            text =
                                "Profile updated successfully.",

                            color =
                                ProfileTeal,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    state.error?.let {

                        Text(
                            text =
                                it,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                    }
                }
            }


            // -----------------------------------------
            // ACCOUNT
            // -----------------------------------------

            Text(
                text =
                    "Account",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            18.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {


                    ProfileInfoRow(
                        label =
                            "Email",
                        value =
                            backendUser?.email
                                ?: "Not available"
                    )


                    ProfileInfoRow(
                        label =
                            "Account Type",
                        value =
                            backendUser?.role
                                ?: "PATIENT"
                    )


                    Text(
                        text =
                            "Email and account role are protected account fields.",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            ProfileMuted
                    )
                }
            }


            // -----------------------------------------
            // ABOUT
            // -----------------------------------------

            Text(
                text =
                    "About MediFlow AI",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            18.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    Text(
                        text =
                            "MediFlow AI",
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            ProfileNavy
                    )

                    Text(
                        text =
                            "Predict. Prioritize. Care.",
                        color =
                            ProfileTeal,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Text(
                        text =
                            "AI-Based Smart Hospital Resource Management and Patient Risk Prediction System",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            ProfileMuted
                    )
                }
            }


            // -----------------------------------------
            // LOGOUT
            // -----------------------------------------

            Text(
                text =
                    "Session",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    ProfileNavy
            )


            OutlinedButton(
                onClick = {

                    authViewModel.logout()

                    onLogout()
                },

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        15.dp
                    ),

                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor =
                            ProfileRed
                    )
            ) {

                Text(
                    text =
                        "Sign Out",
                    fontWeight =
                        FontWeight.Bold
                )
            }


            // -----------------------------------------
            // DISCLAIMER
            // -----------------------------------------

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            ProfileTeal.copy(
                                alpha = 0.08f
                            )
                    )
            ) {

                Text(
                    modifier =
                        Modifier.padding(
                            16.dp
                        ),

                    text =
                        "This application is an academic prototype " +
                                "and is not intended for clinical diagnosis " +
                                "or medical decision-making.",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        ProfileMuted
                )
            }
        }
    }
}


@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                label,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                ProfileMuted
        )


        Text(
            text =
                value,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            fontWeight =
                FontWeight.SemiBold,
            color =
                ProfileNavy
        )
    }
}