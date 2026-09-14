package com.mediflowai.ui.hospital

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.mediflowai.viewmodel.DoctorPatientDetailsViewModel


private val PatientDetailsNavy =
    Color(0xFF081225)

private val PatientDetailsTeal =
    Color(0xFF00A6A6)

private val PatientDetailsMuted =
    Color(0xFF64748B)

private val PatientDetailsBackground =
    Color(0xFFF5F8FB)


@Composable
fun DoctorPatientDetailsScreen(
    patientId: Int,
    onBack: () -> Unit,
    onRiskPrediction: (Int) -> Unit,
    viewModel:
    DoctorPatientDetailsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(
        patientId
    ) {

        viewModel.loadPatient(
            patientId
        )
    }


    Scaffold(
        containerColor =
            PatientDetailsBackground
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
                            "Unable to load patient",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            PatientDetailsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            PatientDetailsMuted
                    )
                }
            }


            state.patient != null -> {

                val patient =
                    state.patient!!


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
                                horizontal =
                                    20.dp,
                                vertical =
                                    12.dp
                            ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            18.dp
                        )
                ) {


                    // ----------------------------------
                    // HEADER
                    // ----------------------------------

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick =
                                onBack
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined
                                        .ArrowBack,
                                contentDescription =
                                    "Back",
                                tint =
                                    PatientDetailsNavy
                            )
                        }


                        Column(
                            modifier =
                                Modifier.padding(
                                    start = 4.dp
                                )
                        ) {

                            Text(
                                text =
                                    "Patient Details",
                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineSmall,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    PatientDetailsNavy
                            )


                            Text(
                                text =
                                    "Assigned patient record",
                                color =
                                    PatientDetailsMuted
                            )
                        }
                    }


                    // ----------------------------------
                    // HERO
                    // ----------------------------------

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
                                    PatientDetailsNavy
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
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
                                                alpha =
                                                    0.12f
                                            ),
                                            CircleShape
                                        ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined
                                            .Person,
                                    contentDescription =
                                        null,
                                    tint =
                                        Color.White
                                )
                            }


                            Column(
                                modifier =
                                    Modifier.padding(
                                        start =
                                            16.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        patient.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        Color.White
                                )


                                patient.gender
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.let {

                                        Text(
                                            text =
                                                it,
                                            color =
                                                Color.White.copy(
                                                    alpha =
                                                        0.75f
                                                )
                                        )
                                    }


                                patient.blood_group
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.let {

                                        Text(
                                            text =
                                                "Blood Group: $it",
                                            color =
                                                PatientDetailsTeal,
                                            fontWeight =
                                                FontWeight.SemiBold
                                        )
                                    }
                            }
                        }
                    }


                    // ----------------------------------
                    // PERSONAL INFORMATION
                    // ----------------------------------

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
                            PatientDetailsNavy
                    )


                    PatientDetailCard(
                        icon =
                            Icons.Outlined.Cake,
                        label =
                            "Date of Birth",
                        value =
                            patient.date_of_birth
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Not provided"
                    )


                    PatientDetailCard(
                        icon =
                            Icons.Outlined.Person,
                        label =
                            "Gender",
                        value =
                            patient.gender
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Not provided"
                    )


                    PatientDetailCard(
                        icon =
                            Icons.Outlined.Bloodtype,
                        label =
                            "Blood Group",
                        value =
                            patient.blood_group
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Not provided"
                    )


                    PatientDetailCard(
                        icon =
                            Icons.Outlined.Phone,
                        label =
                            "Phone",
                        value =
                            patient.phone
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Not provided"
                    )


                    // ----------------------------------
                    // MEDICAL INFORMATION
                    // ----------------------------------

                    Text(
                        text =
                            "Medical Information",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            PatientDetailsNavy
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

                        Text(
                            modifier =
                                Modifier.padding(
                                    18.dp
                                ),
                            text =
                                patient
                                    .medical_information
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "No medical information recorded.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyLarge,
                            color =
                                PatientDetailsNavy
                        )
                    }


                    // ----------------------------------
                    // ADDRESS
                    // ----------------------------------

                    patient.address
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?.let { address ->

                            Text(
                                text =
                                    "Address",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    PatientDetailsNavy
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

                                Text(
                                    modifier =
                                        Modifier.padding(
                                            18.dp
                                        ),
                                    text =
                                        address,
                                    color =
                                        PatientDetailsNavy
                                )
                            }
                        }


                    // ----------------------------------
                    // AI RISK ASSESSMENT
                    // ----------------------------------

                    Text(
                        text =
                            "AI Risk Assessment",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            PatientDetailsNavy
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
                                    12.dp
                                )
                        ) {

                            Text(
                                text =
                                    "Generate a machine-learning based " +
                                            "readmission risk assessment for " +
                                            patient.name + ".",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    PatientDetailsMuted
                            )


                            Button(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                onClick = {

                                    onRiskPrediction(
                                        patient.id
                                    )
                                }
                            ) {

                                Text(
                                    text =
                                        "Readmission Risk Prediction",
                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }
                        }
                    }


                    // ----------------------------------
                    // DISCLAIMER
                    // ----------------------------------

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
                                    PatientDetailsTeal.copy(
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
                                PatientDetailsMuted
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PatientDetailCard(
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
                Modifier.padding(
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
                            PatientDetailsTeal.copy(
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
                        PatientDetailsTeal
                )
            }


            Column(
                modifier =
                    Modifier.padding(
                        start =
                            14.dp
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
                        PatientDetailsMuted
                )


                Text(
                    text =
                        value,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        PatientDetailsNavy
                )
            }
        }
    }
}