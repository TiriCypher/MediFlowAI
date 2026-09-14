package com.mediflowai.ui.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.navigation.PatientBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.PatientAppointmentsViewModel
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PatientAppointmentsScreen(
    onNavigate: (String) -> Unit,
    onAppointmentClick: (Int) -> Unit,
    viewModel: PatientAppointmentsViewModel = viewModel()
){

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {

        viewModel.loadAppointments()
    }

    Scaffold(
        bottomBar = {
            PatientBottomBar(
                currentRoute =
                    Screen.PatientAppointments.route,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->

        when {

            state.isLoading -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text = "Unable to load appointments",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = state.error ?: ""
                    )
                }
            }

            state.appointments.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(20.dp),
                    verticalArrangement =
                        Arrangement.Center,
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "No appointments yet",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your booked appointments will appear here."
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            20.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    item {

                        Text(
                            text = "My Appointments",
                            style =
                                MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(
                        items = state.appointments,
                        key = { it.id }
                    ) { appointment ->

                        val doctor =
                            state.doctors.firstOrNull {
                                it.id == appointment.doctor_id
                            }

                        val department =
                            state.departments.firstOrNull {
                                it.id == appointment.department_id
                            }

                        AppointmentCard(
                            appointment =
                                appointment,

                            doctorName =
                                doctor?.name
                                    ?: "Doctor",

                            departmentName =
                                department?.name
                                    ?: doctor?.department_name
                                    ?: "Department not available",

                            onClick = {

                                onAppointmentClick(
                                    appointment.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentItem,
    doctorName: String,
    departmentName: String,
    onClick: () -> Unit
) {

    val formattedDateTime =
        try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.US
                )

            val outputFormat =
                SimpleDateFormat(
                    "dd MMM yyyy • hh:mm a",
                    Locale.getDefault()
                )

            val date =
                inputFormat.parse(
                    appointment.appointment_time
                )

            if (date != null)
                outputFormat.format(date)
            else
                appointment.appointment_time

        } catch (e: Exception) {

            appointment.appointment_time
        }


    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = doctorName,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = departmentName,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    Color(0xFF64748B)
            )

            Text(
                text = formattedDateTime,
                style =
                    MaterialTheme.typography.bodyLarge,
                fontWeight =
                    FontWeight.SemiBold
            )

            Card(
                shape =
                    RoundedCornerShape(12.dp)
            ) {

                Text(
                    modifier =
                        Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                    text =
                        appointment.status,
                    style =
                        MaterialTheme.typography.labelMedium
                )
            }

            appointment.reason
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {

                    Text(
                        text = "Reason: $it",
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
        }
    }
}