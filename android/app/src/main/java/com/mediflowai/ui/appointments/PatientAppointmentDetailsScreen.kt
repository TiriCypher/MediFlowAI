package com.mediflowai.ui.appointments

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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.viewmodel.PatientAppointmentDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Locale


private val DetailsNavy =
    Color(0xFF081225)

private val DetailsTeal =
    Color(0xFF00A6A6)

private val DetailsBackground =
    Color(0xFFF5F8FB)

private val DetailsMuted =
    Color(0xFF64748B)

private val DetailsGreen =
    Color(0xFF16A34A)

private val DetailsRed =
    Color(0xFFDC2626)

private val DetailsAmber =
    Color(0xFFD97706)


@Composable
fun PatientAppointmentDetailsScreen(
    appointmentId: Int,
    onBack: () -> Unit,
    viewModel:
    PatientAppointmentDetailsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()

    var showCancelDialog by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(
        appointmentId
    ) {

        viewModel.loadAppointment(
            appointmentId
        )
    }


    Scaffold(
        containerColor =
            DetailsBackground
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
                                20.dp
                            ),
                    verticalArrangement =
                        Arrangement.Center,
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Unable to load appointment",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DetailsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DetailsMuted
                    )
                }
            }


            state.appointment != null -> {

                val appointment =
                    state.appointment!!


                val doctorName =
                    state.doctor?.name
                        ?: "Doctor"


                val departmentName =
                    state.department?.name
                        ?: state.doctor
                            ?.department_name
                        ?: "Department not available"


                val date =
                    formatAppointmentDate(
                        appointment.appointment_time
                    )


                val time =
                    formatAppointmentTime(
                        appointment.appointment_time
                    )


                val canCancel =
                    !appointment.status.equals(
                        "CANCELLED",
                        ignoreCase = true
                    ) &&
                            !appointment.status.equals(
                                "COMPLETED",
                                ignoreCase = true
                            )


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
                                horizontal = 20.dp,
                                vertical = 12.dp
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
                                    DetailsNavy
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
                                    "Appointment Details",
                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineSmall,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    DetailsNavy
                            )


                            Text(
                                text =
                                    "View your appointment information",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    DetailsMuted
                            )
                        }
                    }


                    // ----------------------------------
                    // DOCTOR SUMMARY
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
                                    DetailsNavy
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(
                                    20.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {

                            Box(
                                modifier =
                                    Modifier
                                        .size(
                                            52.dp
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

                                Icon(
                                    imageVector =
                                        Icons.Outlined
                                            .MedicalServices,
                                    contentDescription =
                                        null,
                                    tint =
                                        Color.White
                                )
                            }


                            Text(
                                text =
                                    doctorName,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    Color.White
                            )


                            state.doctor
                                ?.specialization
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?.let {

                                    Text(
                                        text = it,
                                        color =
                                            Color.White.copy(
                                                alpha = 0.80f
                                            )
                                    )
                                }


                            Text(
                                text =
                                    departmentName,
                                color =
                                    DetailsTeal,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }


                    // ----------------------------------
                    // STATUS
                    // ----------------------------------

                    AppointmentStatusCard(
                        status =
                            appointment.status
                    )


                    // ----------------------------------
                    // DATE / TIME
                    // ----------------------------------

                    Text(
                        text =
                            "Schedule",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DetailsNavy
                    )


                    AppointmentInfoCard(
                        icon =
                            Icons.Outlined
                                .CalendarMonth,
                        label =
                            "Date",
                        value =
                            date
                    )


                    AppointmentInfoCard(
                        icon =
                            Icons.Outlined
                                .Schedule,
                        label =
                            "Time",
                        value =
                            time
                    )


                    // ----------------------------------
                    // APPOINTMENT INFORMATION
                    // ----------------------------------

                    Text(
                        text =
                            "Appointment Information",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DetailsNavy
                    )


                    AppointmentInfoCard(
                        icon =
                            Icons.Outlined
                                .MedicalServices,
                        label =
                            "Doctor",
                        value =
                            doctorName
                    )


                    AppointmentInfoCard(
                        icon =
                            Icons.Outlined
                                .LocalHospital,
                        label =
                            "Department",
                        value =
                            departmentName
                    )


                    // ----------------------------------
                    // REASON
                    // ----------------------------------

                    Text(
                        text =
                            "Reason for Visit",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DetailsNavy
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
                                appointment.reason
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "No reason provided.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyLarge,
                            color =
                                DetailsNavy
                        )
                    }


                    // ----------------------------------
                    // SYSTEM INFORMATION
                    // ----------------------------------

                    Text(
                        text =
                            "System Information",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DetailsNavy
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

                            DetailRow(
                                label =
                                    "Appointment ID",
                                value =
                                    "#${appointment.id}"
                            )


                            DetailRow(
                                label =
                                    "Status",
                                value =
                                    appointment.status
                            )


                            DetailRow(
                                label =
                                    "Priority",
                                value =
                                    appointment.priority
                                        .toString()
                            )
                        }
                    }


                    // ----------------------------------
                    // APPOINTMENT ACTIONS
                    // ----------------------------------

                    if (canCancel) {

                        Text(
                            text =
                                "Appointment Actions",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                DetailsNavy
                        )


                        OutlinedButton(
                            onClick = {
                                showCancelDialog =
                                    true
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            enabled =
                                !state.isCancelling,
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            colors =
                                ButtonDefaults
                                    .outlinedButtonColors(
                                        contentColor =
                                            DetailsRed
                                    )
                        ) {

                            Text(
                                text =
                                    if (
                                        state.isCancelling
                                    ) {
                                        "Cancelling..."
                                    } else {
                                        "Cancel Appointment"
                                    },
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    Text(
                        text =
                            "Appointment priority and status are managed securely by the hospital system.",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            DetailsMuted
                    )


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
                                    DetailsTeal.copy(
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
                                DetailsMuted
                        )
                    }
                }
            }
        }
    }


    // ----------------------------------
    // CANCEL CONFIRMATION DIALOG
    // ----------------------------------

    if (showCancelDialog) {

        AlertDialog(
            onDismissRequest = {

                if (!state.isCancelling) {

                    showCancelDialog =
                        false
                }
            },

            title = {

                Text(
                    text =
                        "Cancel Appointment?"
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to cancel this appointment? " +
                                "The appointment status will be changed to CANCELLED."
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        showCancelDialog =
                            false

                        viewModel
                            .cancelAppointment()
                    },

                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    DetailsRed
                            )
                ) {

                    Text(
                        text =
                            "Yes, Cancel"
                    )
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {

                        showCancelDialog =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Keep Appointment"
                    )
                }
            }
        )
    }
}


@Composable
private fun AppointmentInfoCard(
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
                            DetailsTeal.copy(
                                alpha = 0.10f
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
                        DetailsTeal
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
                        DetailsMuted
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
                        DetailsNavy
                )
            }
        }
    }
}


@Composable
private fun AppointmentStatusCard(
    status: String
) {

    val upperStatus =
        status.uppercase()


    val statusColor =
        when (upperStatus) {

            "COMPLETED" ->
                DetailsGreen

            "CANCELLED" ->
                DetailsRed

            "SCHEDULED" ->
                DetailsTeal

            else ->
                DetailsAmber
        }


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
                    statusColor.copy(
                        alpha = 0.10f
                    )
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "Appointment Status",
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    DetailsNavy
            )


            Text(
                text =
                    upperStatus,
                fontWeight =
                    FontWeight.Bold,
                color =
                    statusColor
            )
        }
    }
}


@Composable
private fun DetailRow(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text =
                label,
            color =
                DetailsMuted
        )


        Text(
            text =
                value,
            fontWeight =
                FontWeight.SemiBold,
            color =
                DetailsNavy
        )
    }
}


private fun formatAppointmentDate(
    value: String
): String {

    return formatAppointmentDateTime(
        value,
        "dd MMM yyyy"
    )
}


private fun formatAppointmentTime(
    value: String
): String {

    return formatAppointmentDateTime(
        value,
        "hh:mm a"
    )
}


private fun formatAppointmentDateTime(
    value: String,
    outputPattern: String
): String {

    val inputPatterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )


    for (pattern in inputPatterns) {

        try {

            val input =
                SimpleDateFormat(
                    pattern,
                    Locale.US
                )


            val parsed =
                input.parse(
                    value
                )


            if (parsed != null) {

                return SimpleDateFormat(
                    outputPattern,
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