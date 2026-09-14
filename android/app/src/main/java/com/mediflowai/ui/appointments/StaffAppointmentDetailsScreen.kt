package com.mediflowai.ui.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.viewmodel.StaffAppointmentDetailsViewModel


private val StaffDetailsNavy =
    Color(0xFF081225)

private val StaffDetailsTeal =
    Color(0xFF00A6A6)

private val StaffDetailsMuted =
    Color(0xFF64748B)

private val StaffDetailsBackground =
    Color(0xFFF5F8FB)


@Composable
fun StaffAppointmentDetailsScreen(

    appointmentId: Int,

    onBack: () -> Unit,

    viewModel:
    StaffAppointmentDetailsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(
        appointmentId
    ) {

        viewModel.loadAppointment(
            appointmentId
        )
    }


    Scaffold(
        containerColor =
            StaffDetailsBackground
    ) { paddingValues ->


        when {

            state.isLoading -> {

                androidx.compose.foundation.layout.Box(
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
                            )
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
                                "Back"
                        )
                    }


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
                            StaffDetailsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),

                        text =
                            state.error ?: "",

                        color =
                            StaffDetailsMuted
                    )
                }
            }


            state.appointment != null -> {

                val appointment =
                    state.appointment!!

                val patient =
                    state.patient

                val doctor =
                    state.doctor

                val department =
                    state.department


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
                                        "Back"
                                )
                            }


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
                                    StaffDetailsNavy
                            )
                        }
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
                                        StaffDetailsNavy
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(
                                        22.dp
                                    ),

                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        6.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        patient?.name
                                            ?: "Patient",

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
                                    text =
                                        doctor?.name
                                            ?: "Doctor",

                                    color =
                                        StaffDetailsTeal,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )


                                Text(
                                    text =
                                        appointment.status,

                                    color =
                                        Color.White.copy(
                                            alpha = 0.78f
                                        )
                                )
                            }
                        }
                    }


                    item {

                        Text(
                            text =
                                "Appointment",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                StaffDetailsNavy
                        )
                    }


                    item {

                        StaffDetailsCard(

                            rows =
                                listOf(

                                    "Date & Time" to
                                            formatStaffAppointmentDate(
                                                appointment
                                                    .appointment_time
                                            ),

                                    "Department" to
                                            (
                                                    department?.name
                                                        ?: "Not assigned"
                                                    ),

                                    "Priority" to
                                            priorityLabel(
                                                appointment
                                                    .priority
                                            ),

                                    "Status" to
                                            appointment.status,

                                    "Reason" to
                                            (
                                                    appointment.reason
                                                        ?.takeIf {
                                                            it.isNotBlank()
                                                        }
                                                        ?: "Not provided"
                                                    )
                                )
                        )
                    }


                    item {

                        Text(
                            text =
                                "Patient",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                StaffDetailsNavy
                        )
                    }


                    item {

                        StaffDetailsCard(

                            rows =
                                listOf(

                                    "Name" to
                                            (
                                                    patient?.name
                                                        ?: "Not available"
                                                    ),

                                    "Gender" to
                                            (
                                                    patient?.gender
                                                        ?: "Not available"
                                                    ),

                                    "Blood Group" to
                                            (
                                                    patient?.blood_group
                                                        ?: "Not available"
                                                    ),

                                    "Phone" to
                                            (
                                                    patient?.phone
                                                        ?: "Not available"
                                                    )
                                )
                        )
                    }


                    item {

                        Text(
                            text =
                                "Doctor",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                StaffDetailsNavy
                        )
                    }


                    item {

                        StaffDetailsCard(

                            rows =
                                listOf(

                                    "Doctor" to
                                            (
                                                    doctor?.name
                                                        ?: "Not available"
                                                    ),

                                    "Specialization" to
                                            (
                                                    doctor
                                                        ?.specialization
                                                        ?: "Not available"
                                                    ),

                                    "Department" to
                                            (
                                                    doctor
                                                        ?.department_name
                                                        ?: "Not available"
                                                    ),

                                    "Availability" to
                                            (
                                                    doctor
                                                        ?.availability
                                                        ?: "Not available"
                                                    )
                                )
                        )
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
                                        StaffDetailsTeal.copy(
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
                                    "Staff operational access does not include " +
                                            "patient readmission-risk predictions.",

                                color =
                                    StaffDetailsMuted
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "This application is an academic prototype and is not intended " +
                                        "for clinical diagnosis or medical decision-making.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                StaffDetailsMuted
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun StaffDetailsCard(
    rows: List<Pair<String, String>>
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
                    14.dp
                )
        ) {

            rows.forEach {
                    (label, value) ->

                Column {

                    Text(
                        text =
                            label,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            StaffDetailsMuted
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 2.dp
                            ),

                        text =
                            value,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            StaffDetailsNavy
                    )
                }
            }
        }
    }
}

private fun priorityLabel(
    priority: Int
): String {

    return when (priority) {

        1 -> "Critical"

        2 -> "High"

        3 -> "Normal"

        4 -> "Low"

        else ->
            priority.toString()
    }
}


private fun formatStaffAppointmentDate(
    value: String
): String {

    val formats =
        listOf(

            java.text.SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                java.util.Locale.US
            ),

            java.text.SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                java.util.Locale.US
            )
        )


    for (format in formats) {

        try {

            val date =
                format.parse(
                    value
                )

            if (date != null) {

                return java.text.SimpleDateFormat(
                    "dd MMM yyyy • hh:mm a",
                    java.util.Locale.US
                ).format(
                    date
                )
            }

        } catch (_: Exception) {
        }
    }

    return value
}