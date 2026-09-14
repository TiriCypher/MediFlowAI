package com.mediflowai.ui.appointments

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
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.navigation.DoctorBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.DoctorAppointmentsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.clickable

private val DoctorAppointmentsNavy =
    Color(0xFF081225)

private val DoctorAppointmentsTeal =
    Color(0xFF00A6A6)

private val DoctorAppointmentsMuted =
    Color(0xFF64748B)

private val DoctorAppointmentsBackground =
    Color(0xFFF5F8FB)

private val DoctorAppointmentsGreen =
    Color(0xFF16A34A)

private val DoctorAppointmentsRed =
    Color(0xFFDC2626)

private val DoctorAppointmentsAmber =
    Color(0xFFD97706)


@Composable
fun DoctorAppointmentsScreen(
    onNavigate: (String) -> Unit,
    onAppointmentClick: (Int) -> Unit,
    viewModel:
    DoctorAppointmentsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.loadAppointments()
    }


    Scaffold(
        containerColor =
            DoctorAppointmentsBackground,

        bottomBar = {

            DoctorBottomBar(
                currentRoute =
                    Screen.DoctorAppointments.route,

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
                            "Unable to load appointments",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorAppointmentsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DoctorAppointmentsMuted
                    )


                    Button(
                        modifier =
                            Modifier.padding(
                                top = 18.dp
                            ),
                        onClick = {

                            viewModel
                                .loadAppointments()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            else -> {

                val scheduled =
                    state.appointments.count {

                        it.status.equals(
                            "SCHEDULED",
                            ignoreCase = true
                        )
                    }


                val completed =
                    state.appointments.count {

                        it.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    }


                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),
                    contentPadding =
                        androidx.compose.foundation
                            .layout.PaddingValues(
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
                                "My Appointments",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                DoctorAppointmentsNavy
                        )


                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                ),
                            text =
                                "Appointments currently assigned to you.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                DoctorAppointmentsMuted
                        )
                    }


                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
                                )
                        ) {

                            AppointmentSummaryCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                value =
                                    scheduled
                                        .toString(),
                                label =
                                    "Scheduled"
                            )


                            AppointmentSummaryCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                value =
                                    completed
                                        .toString(),
                                label =
                                    "Completed"
                            )
                        }
                    }


                    if (
                        state.appointments.isEmpty()
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
                                    CardDefaults
                                        .cardColors(
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
                                            "No appointments assigned",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            DoctorAppointmentsNavy
                                    )


                                    Text(
                                        modifier =
                                            Modifier.padding(
                                                top = 6.dp
                                            ),
                                        text =
                                            "Appointments assigned to you will appear here automatically.",
                                        color =
                                            DoctorAppointmentsMuted
                                    )
                                }
                            }
                        }

                    } else {

                        items(
                            items =
                                state.appointments,
                            key = {
                                it.id
                            }
                        ) { appointment ->

                            DoctorAppointmentCard(
                                appointment =
                                    appointment,

                                onClick = {

                                    onAppointmentClick(
                                        appointment.id
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
                                CardDefaults
                                    .cardColors(
                                        containerColor =
                                            DoctorAppointmentsTeal
                                                .copy(
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
                                    DoctorAppointmentsMuted
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AppointmentSummaryCard(
    modifier: Modifier,
    value: String,
    label: String
) {

    Card(
        modifier =
            modifier,
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
                    6.dp
                )
        ) {

            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    DoctorAppointmentsNavy
            )


            Text(
                text =
                    label,
                color =
                    DoctorAppointmentsMuted
            )
        }
    }
}


@Composable
private fun DoctorAppointmentCard(
    appointment:
    AppointmentItem,
    onClick: () -> Unit
){

    val statusColor =
        when (
            appointment.status
                .uppercase()
        ) {

            "COMPLETED" ->
                DoctorAppointmentsGreen

            "CANCELLED" ->
                DoctorAppointmentsRed

            "SCHEDULED" ->
                DoctorAppointmentsTeal

            else ->
                DoctorAppointmentsAmber
        }


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
                    9.dp
                )
        ) {


            Text(
                text =
                    formatAppointmentDateTime(
                        appointment
                            .appointment_time
                    ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    DoctorAppointmentsNavy
            )


            appointment.reason
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {

                    Text(
                        text = it,
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            DoctorAppointmentsMuted
                    )
                }


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
                        appointment
                            .status
                            .uppercase(),
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        statusColor
                )


                Text(
                    text =
                        priorityLabel(
                            appointment.priority
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        DoctorAppointmentsMuted
                )
            }
        }
    }
}


private fun priorityLabel(
    priority: Int
): String {

    return when (priority) {

        1 ->
            "Priority: Critical"

        2 ->
            "Priority: High"

        3 ->
            "Priority: Normal"

        4 ->
            "Priority: Low"

        5 ->
            "Priority: Routine"

        else ->
            "Priority: $priority"
    }
}


private fun formatAppointmentDateTime(
    value: String
): String {

    val patterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )


    for (pattern in patterns) {

        try {

            val input =
                SimpleDateFormat(
                    pattern,
                    Locale.US
                )

            input.isLenient =
                false


            val parsed =
                input.parse(
                    value
                )


            if (parsed != null) {

                return SimpleDateFormat(
                    "dd MMM yyyy • hh:mm a",
                    Locale.getDefault()
                ).format(
                    parsed
                )
            }

        } catch (_: Exception) {
        }
    }


    return value
}