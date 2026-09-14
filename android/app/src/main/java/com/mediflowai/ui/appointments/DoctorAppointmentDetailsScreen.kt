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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
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
import com.mediflowai.viewmodel.DoctorAppointmentDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private val DoctorDetailsNavy =
    Color(0xFF081225)

private val DoctorDetailsTeal =
    Color(0xFF00A6A6)

private val DoctorDetailsBackground =
    Color(0xFFF5F8FB)

private val DoctorDetailsMuted =
    Color(0xFF64748B)

private val DoctorDetailsGreen =
    Color(0xFF16A34A)

private val DoctorDetailsRed =
    Color(0xFFDC2626)

private val DoctorDetailsAmber =
    Color(0xFFD97706)


@Composable
fun DoctorAppointmentDetailsScreen(
    appointmentId: Int,
    onBack: () -> Unit,
    viewModel:
    DoctorAppointmentDetailsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()

    var showCompleteDialog by remember {
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
            DoctorDetailsBackground
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
                            "Unable to load appointment",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorDetailsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DoctorDetailsMuted
                    )
                }
            }


            state.appointment != null &&
                    state.patient != null -> {

                val appointment =
                    state.appointment!!

                val patient =
                    state.patient!!


                val departmentName =
                    state.department?.name
                        ?: state.doctor
                            ?.department_name
                        ?: "Department not available"


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
                                    DoctorDetailsNavy
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
                                    DoctorDetailsNavy
                            )


                            Text(
                                text =
                                    "Assigned patient appointment",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    DoctorDetailsMuted
                            )
                        }
                    }


                    // ----------------------------------
                    // PATIENT HERO
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
                                    DoctorDetailsNavy
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
                                            text = it,
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
                                                DoctorDetailsTeal,
                                            fontWeight =
                                                FontWeight.SemiBold
                                        )
                                    }
                            }
                        }
                    }


                    // ----------------------------------
                    // STATUS
                    // ----------------------------------

                    DoctorAppointmentStatusCard(
                        status =
                            appointment.status
                    )


                    // ----------------------------------
                    // SCHEDULE
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "Schedule"
                    )


                    DoctorDetailCard(
                        icon =
                            Icons.Outlined
                                .CalendarMonth,
                        label =
                            "Date",
                        value =
                            formatDate(
                                appointment
                                    .appointment_time
                            )
                    )


                    DoctorDetailCard(
                        icon =
                            Icons.Outlined
                                .Schedule,
                        label =
                            "Time",
                        value =
                            formatTime(
                                appointment
                                    .appointment_time
                            )
                    )


                    // ----------------------------------
                    // APPOINTMENT INFORMATION
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "Appointment Information"
                    )


                    DoctorDetailCard(
                        icon =
                            Icons.Outlined
                                .LocalHospital,
                        label =
                            "Department",
                        value =
                            departmentName
                    )


                    DoctorDetailCard(
                        icon =
                            Icons.Outlined
                                .MedicalServices,
                        label =
                            "Priority",
                        value =
                            priorityLabel(
                                appointment.priority
                            )
                    )


                    // ----------------------------------
                    // REASON
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "Reason for Visit"
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
                                DoctorDetailsNavy
                        )
                    }


                    // ----------------------------------
                    // PATIENT INFORMATION
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "Patient Information"
                    )


                    PatientInformationCard(
                        dateOfBirth =
                            patient.date_of_birth,
                        gender =
                            patient.gender,
                        bloodGroup =
                            patient.blood_group,
                        phone =
                            patient.phone
                    )


                    // ----------------------------------
                    // MEDICAL INFORMATION
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "Medical Information"
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
                                DoctorDetailsNavy
                        )
                    }


                    // ----------------------------------
                    // SYSTEM INFORMATION
                    // ----------------------------------

                    SectionTitle(
                        text =
                            "System Information"
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

                            DoctorInformationRow(
                                label =
                                    "Appointment",
                                value =
                                    "#${appointment.id}"
                            )


                            DoctorInformationRow(
                                label =
                                    "Status",
                                value =
                                    appointment.status
                                        .uppercase()
                            )


                            DoctorInformationRow(
                                label =
                                    "Priority",
                                value =
                                    appointment.priority
                                        .toString()
                            )
                        }
                    }

                    val canComplete =
                        !appointment.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        ) &&
                                !appointment.status.equals(
                                    "CANCELLED",
                                    ignoreCase = true
                                )


                    if (canComplete) {

                        SectionTitle(
                            text =
                                "Appointment Actions"
                        )


                        Button(
                            onClick = {

                                showCompleteDialog =
                                    true
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                !state.isCompleting,

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        DoctorDetailsTeal
                                )
                        ) {

                            Text(
                                text =
                                    if (
                                        state.isCompleting
                                    ) {
                                        "Completing..."
                                    } else {
                                        "Mark as Completed"
                                    },

                                fontWeight =
                                    FontWeight.Bold
                            )
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
                                    DoctorDetailsTeal.copy(
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
                                DoctorDetailsMuted
                        )
                    }
                }
            }
        }
    }

    if (showCompleteDialog) {

        AlertDialog(

            onDismissRequest = {

                if (!state.isCompleting) {

                    showCompleteDialog =
                        false
                }
            },

            title = {

                Text(
                    text =
                        "Complete Appointment?"
                )
            },

            text = {

                Text(
                    text =
                        "Confirm that this appointment has been completed. " +
                                "Its status will be updated to COMPLETED."
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        showCompleteDialog =
                            false

                        viewModel
                            .completeAppointment()
                    },

                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    DoctorDetailsTeal
                            )
                ) {

                    Text(
                        text =
                            "Confirm Completion"
                    )
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {

                        showCompleteDialog =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Not Yet"
                    )
                }
            }
        )
    }
}


@Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text =
            text,
        style =
            MaterialTheme
                .typography
                .titleLarge,
        fontWeight =
            FontWeight.Bold,
        color =
            DoctorDetailsNavy
    )
}


@Composable
private fun DoctorDetailCard(
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
                            DoctorDetailsTeal.copy(
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
                        DoctorDetailsTeal
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
                        DoctorDetailsMuted
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
                        DoctorDetailsNavy
                )
            }
        }
    }
}


@Composable
private fun PatientInformationCard(
    dateOfBirth: String?,
    gender: String?,
    bloodGroup: String?,
    phone: String?
) {

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

            DoctorInformationRow(
                label =
                    "Date of Birth",
                value =
                    dateOfBirth
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not provided"
            )


            DoctorInformationRow(
                label =
                    "Gender",
                value =
                    gender
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not provided"
            )


            DoctorInformationRow(
                label =
                    "Blood Group",
                value =
                    bloodGroup
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not provided"
            )


            DoctorInformationRow(
                label =
                    "Phone",
                value =
                    phone
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not provided"
            )
        }
    }
}


@Composable
private fun DoctorAppointmentStatusCard(
    status: String
) {

    val upperStatus =
        status.uppercase()


    val color =
        when (upperStatus) {

            "COMPLETED" ->
                DoctorDetailsGreen

            "CANCELLED" ->
                DoctorDetailsRed

            "SCHEDULED" ->
                DoctorDetailsTeal

            else ->
                DoctorDetailsAmber
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
                    color.copy(
                        alpha =
                            0.10f
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
                    DoctorDetailsNavy
            )


            Text(
                text =
                    upperStatus,
                fontWeight =
                    FontWeight.Bold,
                color =
                    color
            )
        }
    }
}


@Composable
private fun DoctorInformationRow(
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
                DoctorDetailsMuted
        )


        Text(
            text =
                value,
            fontWeight =
                FontWeight.SemiBold,
            color =
                DoctorDetailsNavy
        )
    }
}


private fun priorityLabel(
    priority: Int
): String {

    return when (priority) {

        1 ->
            "Critical"

        2 ->
            "High"

        3 ->
            "Normal"

        4 ->
            "Low"

        5 ->
            "Routine"

        else ->
            "Priority $priority"
    }
}


private fun formatDate(
    value: String
): String {

    return formatDateTime(
        value,
        "dd MMM yyyy"
    )
}


private fun formatTime(
    value: String
): String {

    return formatDateTime(
        value,
        "hh:mm a"
    )
}


private fun formatDateTime(
    value: String,
    outputPattern: String
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