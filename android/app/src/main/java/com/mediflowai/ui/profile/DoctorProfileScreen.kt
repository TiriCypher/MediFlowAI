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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.navigation.DoctorBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.DoctorProfileViewModel


private val DoctorProfileNavy =
    Color(0xFF081225)

private val DoctorProfileTeal =
    Color(0xFF00A6A6)

private val DoctorProfileMuted =
    Color(0xFF64748B)

private val DoctorProfileBackground =
    Color(0xFFF5F8FB)

private val DoctorProfileRed =
    Color(0xFFDC2626)


@Composable
fun DoctorProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel:
    DoctorProfileViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.loadProfile()
    }


    Scaffold(
        containerColor =
            DoctorProfileBackground,

        bottomBar = {

            DoctorBottomBar(
                currentRoute =
                    Screen.DoctorProfile.route,
                onNavigate =
                    onNavigate
            )
        }
    ) { paddingValues ->


        when {

            state.isLoading -> {

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
            }


            state.error != null -> {

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            )
                            .padding(
                                24.dp
                            ),
                    verticalArrangement =
                        Arrangement.Center,
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Unable to load profile",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorProfileNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DoctorProfileMuted
                    )


                    Button(
                        modifier =
                            Modifier.padding(
                                top = 18.dp
                            ),
                        onClick = {

                            viewModel
                                .loadProfile()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            state.doctor != null -> {

                val doctor =
                    state.doctor!!

                val user =
                    state.user


                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),

                    contentPadding =
                        androidx.compose.foundation.layout
                            .PaddingValues(
                                20.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            16.dp
                        )
                ) {


                    item {

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
                                DoctorProfileNavy
                        )


                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                ),
                            text =
                                "Doctor account and professional information.",
                            color =
                                DoctorProfileMuted
                        )
                    }


                    item {

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
                                        DoctorProfileNavy
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            24.dp
                                        ),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {


                                Box(
                                    modifier =
                                        Modifier
                                            .size(
                                                72.dp
                                            )
                                            .background(
                                                Color.White.copy(
                                                    alpha =
                                                        0.12f
                                                ),
                                                CircleShape
                                            ),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        modifier =
                                            Modifier.size(
                                                38.dp
                                            ),
                                        imageVector =
                                            Icons.Outlined
                                                .Person,
                                        contentDescription =
                                            null,
                                        tint =
                                            Color.White
                                    )
                                }


                                Text(
                                    modifier =
                                        Modifier.padding(
                                            top = 14.dp
                                        ),
                                    text =
                                        doctor.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .headlineSmall,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        Color.White
                                )


                                Text(
                                    modifier =
                                        Modifier.padding(
                                            top = 4.dp
                                        ),
                                    text =
                                        doctor.specialization
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Doctor",
                                    color =
                                        DoctorProfileTeal,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )


                                doctor.department_name
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.let {

                                        Text(
                                            modifier =
                                                Modifier.padding(
                                                    top = 3.dp
                                                ),
                                            text =
                                                it,
                                            color =
                                                Color.White.copy(
                                                    alpha =
                                                        0.75f
                                                )
                                        )
                                    }
                            }
                        }
                    }


                    item {

                        Text(
                            text =
                                "Professional Information",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                DoctorProfileNavy
                        )
                    }


                    item {

                        DoctorProfileInfoCard(
                            icon =
                                Icons.Outlined.Badge,
                            label =
                                "Specialization",
                            value =
                                doctor.specialization
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Not provided"
                        )
                    }


                    item {

                        DoctorProfileInfoCard(
                            icon =
                                Icons.Outlined.LocalHospital,
                            label =
                                "Department",
                            value =
                                doctor.department_name
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Not assigned"
                        )
                    }


                    item {

                        DoctorProfileInfoCard(
                            icon =
                                Icons.Outlined.Schedule,
                            label =
                                "Availability",
                            value =
                                doctor.availability
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Not provided"
                        )
                    }


                    item {

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
                                DoctorProfileNavy
                        )
                    }


                    item {

                        DoctorProfileInfoCard(
                            icon =
                                Icons.Outlined.Email,
                            label =
                                "Email",
                            value =
                                user?.email
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Not available"
                        )
                    }


                    item {

                        DoctorProfileInfoCard(
                            icon =
                                Icons.Outlined.Person,
                            label =
                                "Account Role",
                            value =
                                user?.role
                                    ?.uppercase()
                                    ?: "DOCTOR"
                        )
                    }


                    item {

                        OutlinedButton(
                            modifier =
                                Modifier.fillMaxWidth(),

                            onClick =
                                onLogout,

                            colors =
                                ButtonDefaults
                                    .outlinedButtonColors(
                                        contentColor =
                                            DoctorProfileRed
                                    )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined
                                        .Logout,
                                contentDescription =
                                    null
                            )


                            Text(
                                modifier =
                                    Modifier.padding(
                                        start = 8.dp
                                    ),
                                text =
                                    "Sign Out",
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }


                    item {

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
                                        DoctorProfileTeal.copy(
                                            alpha =
                                                0.08f
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
                                    DoctorProfileMuted
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DoctorProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {

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
                    Color.White
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Box(
                modifier =
                    Modifier
                        .size(
                            44.dp
                        )
                        .background(
                            DoctorProfileTeal.copy(
                                alpha =
                                    0.10f
                            ),
                            CircleShape
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        icon,
                    contentDescription =
                        null,
                    tint =
                        DoctorProfileTeal
                )
            }


            Column(
                modifier =
                    Modifier.padding(
                        start = 14.dp
                    )
            ) {

                Text(
                    text =
                        label,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        DoctorProfileMuted
                )


                Text(
                    modifier =
                        Modifier.padding(
                            top = 2.dp
                        ),
                    text =
                        value,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        DoctorProfileNavy
                )
            }
        }
    }
}