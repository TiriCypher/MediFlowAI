package com.mediflowai.ui.hospital

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.navigation.DoctorBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.DoctorPatientsViewModel


private val DoctorPatientsNavy =
    Color(0xFF081225)

private val DoctorPatientsTeal =
    Color(0xFF00A6A6)

private val DoctorPatientsMuted =
    Color(0xFF64748B)

private val DoctorPatientsBackground =
    Color(0xFFF5F8FB)


@Composable
fun DoctorPatientsScreen(
    onNavigate: (String) -> Unit,
    onPatientClick: (Int) -> Unit,
    viewModel:
    DoctorPatientsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.loadPatients()
    }


    Scaffold(
        containerColor =
            DoctorPatientsBackground,

        bottomBar = {

            DoctorBottomBar(
                currentRoute =
                    Screen.DoctorPatients.route,

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
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text =
                            "Unable to load patients",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorPatientsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DoctorPatientsMuted
                    )


                    Button(
                        modifier =
                            Modifier.padding(
                                top = 18.dp
                            ),
                        onClick = {

                            viewModel
                                .loadPatients()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            else -> {

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
                            14.dp
                        )
                ) {


                    item {

                        Text(
                            text =
                                "My Patients",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                DoctorPatientsNavy
                        )


                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                ),
                            text =
                                "Patients assigned to you through appointments.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                DoctorPatientsMuted
                        )
                    }


                    item {

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
                                    )
                            ) {

                                Text(
                                    text =
                                        state.patients
                                            .size
                                            .toString(),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .headlineMedium,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        DoctorPatientsNavy
                                )


                                Text(
                                    text =
                                        "Assigned Patients",
                                    color =
                                        DoctorPatientsMuted
                                )
                            }
                        }
                    }


                    if (
                        state.patients.isEmpty()
                    ) {

                        item {

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
                                            22.dp
                                        )
                                ) {

                                    Text(
                                        text =
                                            "No patients assigned",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            DoctorPatientsNavy
                                    )


                                    Text(
                                        modifier =
                                            Modifier.padding(
                                                top = 6.dp
                                            ),
                                        text =
                                            "Patients linked to your appointments will appear here.",
                                        color =
                                            DoctorPatientsMuted
                                    )
                                }
                            }
                        }

                    } else {

                        items(
                            items =
                                state.patients,
                            key = {
                                it.id
                            }
                        ) { patient ->

                            DoctorPatientCard(
                                patient =
                                    patient,

                                onClick = {

                                    onPatientClick(
                                        patient.id
                                    )
                                }
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
                                        DoctorPatientsTeal.copy(
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
                                    DoctorPatientsMuted
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DoctorPatientCard(
    patient: PatientProfile,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

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
                    7.dp
                )
        ) {


            Text(
                text =
                    patient.name,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    DoctorPatientsNavy
            )


            val demographicText =
                listOfNotNull(
                    patient.gender
                        ?.takeIf {
                            it.isNotBlank()
                        },

                    patient.blood_group
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?.let {
                            "Blood Group: $it"
                        }
                )
                    .joinToString(
                        " • "
                    )


            if (
                demographicText.isNotBlank()
            ) {

                Text(
                    text =
                        demographicText,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        DoctorPatientsMuted
                )
            }


            patient.phone
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {

                    Text(
                        text =
                            it,
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            DoctorPatientsMuted
                    )
                }


            Text(
                text =
                    "View patient details",
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    DoctorPatientsTeal
            )
        }
    }
}