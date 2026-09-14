package com.mediflowai.ui.appointments

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.navigation.Screen
import com.mediflowai.navigation.StaffBottomBar
import com.mediflowai.viewmodel.StaffAppointmentsViewModel
import java.text.SimpleDateFormat
import java.util.Locale


private val StaffAppointmentsNavy =
    Color(0xFF081225)

private val StaffAppointmentsTeal =
    Color(0xFF00A6A6)

private val StaffAppointmentsMuted =
    Color(0xFF64748B)

private val StaffAppointmentsBackground =
    Color(0xFFF5F8FB)

private val StaffAppointmentsGreen =
    Color(0xFF16A34A)

private val StaffAppointmentsRed =
    Color(0xFFDC2626)

private val StaffAppointmentsAmber =
    Color(0xFFD97706)


@Composable
fun StaffAppointmentsScreen(
    onNavigate: (String) -> Unit,
    onAppointmentClick: (Int) -> Unit,
    viewModel:
    StaffAppointmentsViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    var search by
    remember {
        mutableStateOf("")
    }


    var selectedStatus by
    remember {
        mutableStateOf("ALL")
    }


    LaunchedEffect(Unit) {
        viewModel.loadAppointments()
    }


    Scaffold(

        containerColor =
            StaffAppointmentsBackground,

        bottomBar = {

            StaffBottomBar(
                currentRoute =
                    Screen.StaffAppointments.route,

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
                            StaffAppointmentsNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),

                        text =
                            state.error ?: "",

                        color =
                            StaffAppointmentsMuted
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

                val filteredAppointments =
                    state.appointments
                        .filter { appointment ->

                            selectedStatus ==
                                    "ALL" ||
                                    appointment
                                        .status
                                        .equals(
                                            selectedStatus,
                                            ignoreCase = true
                                        )
                        }
                        .filter { appointment ->

                            val patientName =
                                state.patients
                                    .firstOrNull {
                                        it.id ==
                                                appointment.patient_id
                                    }
                                    ?.name
                                    .orEmpty()


                            val doctorName =
                                state.doctors
                                    .firstOrNull {
                                        it.id ==
                                                appointment.doctor_id
                                    }
                                    ?.name
                                    .orEmpty()


                            val reason =
                                appointment.reason
                                    .orEmpty()


                            search.isBlank() ||

                                    patientName.contains(
                                        search,
                                        ignoreCase = true
                                    ) ||

                                    doctorName.contains(
                                        search,
                                        ignoreCase = true
                                    ) ||

                                    reason.contains(
                                        search,
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
                                "Appointments",

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                StaffAppointmentsNavy
                        )


                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                ),

                            text =
                                "Hospital-wide appointment operations",

                            color =
                                StaffAppointmentsMuted
                        )
                    }


                    item {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(
                                    22.dp
                                ),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        StaffAppointmentsNavy
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(
                                        20.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        state
                                            .appointments
                                            .size
                                            .toString(),

                                    style =
                                        MaterialTheme
                                            .typography
                                            .headlineMedium,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )


                                Text(
                                    text =
                                        "Total hospital appointments",

                                    color =
                                        Color.White.copy(
                                            alpha = 0.72f
                                        )
                                )
                            }
                        }
                    }


                    item {

                        OutlinedTextField(
                            modifier =
                                Modifier.fillMaxWidth(),

                            value =
                                search,

                            onValueChange = {
                                search = it
                            },

                            label = {
                                Text(
                                    "Search patient, doctor or reason"
                                )
                            },

                            singleLine =
                                true
                        )
                    }


                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {

                            listOf(
                                "ALL",
                                "SCHEDULED",
                                "COMPLETED",
                                "CANCELLED"
                            ).forEach { status ->

                                FilterChip(

                                    selected =
                                        selectedStatus ==
                                                status,

                                    onClick = {
                                        selectedStatus =
                                            status
                                    },

                                    label = {

                                        Text(
                                            when (status) {

                                                "ALL" ->
                                                    "All"

                                                "SCHEDULED" ->
                                                    "Scheduled"

                                                "COMPLETED" ->
                                                    "Completed"

                                                else ->
                                                    "Cancelled"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }


                    if (
                        filteredAppointments.isEmpty()
                    ) {

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
                                            Color.White
                                    )
                            ) {

                                Text(
                                    modifier =
                                        Modifier.padding(
                                            24.dp
                                        ),

                                    text =
                                        "No appointments match the selected filters.",

                                    color =
                                        StaffAppointmentsMuted
                                )
                            }
                        }

                    } else {

                        items(
                            items =
                                filteredAppointments,

                            key = {
                                it.id
                            }
                        ) { appointment ->


                            val patientName =
                                state.patients
                                    .firstOrNull {
                                        it.id ==
                                                appointment.patient_id
                                    }
                                    ?.name
                                    ?: "Patient"


                            val doctor =
                                state.doctors
                                    .firstOrNull {
                                        it.id ==
                                                appointment.doctor_id
                                    }


                            val departmentName =
                                state.departments
                                    .firstOrNull {
                                        it.id ==
                                                appointment.department_id
                                    }
                                    ?.name


                            StaffAppointmentCard(

                                appointment =
                                    appointment,

                                patientName =
                                    patientName,

                                doctorName =
                                    doctor?.name
                                        ?: "Doctor",

                                departmentName =
                                    departmentName,

                                onClick = {

                                    onAppointmentClick(
                                        appointment.id
                                    )
                                }
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "Staff access is limited to operational appointment management. " +
                                        "Clinical readmission-risk information is not available in this role.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                StaffAppointmentsMuted
                        )
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
                                StaffAppointmentsMuted
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun StaffAppointmentCard(

    appointment: AppointmentItem,

    patientName: String,

    doctorName: String,

    departmentName: String?,

    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

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
                        patientName,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        StaffAppointmentsNavy
                )


                Text(
                    text =
                        appointment.status,

                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        statusColor(
                            appointment.status
                        )
                )
            }


            Text(
                text =
                    doctorName,

                color =
                    StaffAppointmentsTeal,

                fontWeight =
                    FontWeight.SemiBold
            )


            departmentName
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {

                    Text(
                        text =
                            it,

                        color =
                            StaffAppointmentsMuted
                    )
                }


            Text(
                text =
                    formatStaffAppointmentDate(
                        appointment
                            .appointment_time
                    ),

                color =
                    StaffAppointmentsNavy
            )


            appointment.reason
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
                            StaffAppointmentsMuted
                    )
                }


            Text(
                text =
                    "Priority: ${
                        priorityLabel(
                            appointment.priority
                        )
                    }",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    StaffAppointmentsMuted
            )
        }
    }
}


private fun statusColor(
    status: String
): Color {

    return when (
        status.uppercase()
    ) {

        "COMPLETED" ->
            StaffAppointmentsGreen

        "CANCELLED" ->
            StaffAppointmentsRed

        "SCHEDULED" ->
            StaffAppointmentsTeal

        else ->
            StaffAppointmentsAmber
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

    val inputFormats =
        listOf(

            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US
            ),

            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                Locale.US
            )
        )


    for (format in inputFormats) {

        try {

            val date =
                format.parse(
                    value
                )

            if (date != null) {

                return SimpleDateFormat(
                    "dd MMM yyyy • hh:mm a",
                    Locale.US
                ).format(
                    date
                )
            }

        } catch (_: Exception) {
        }
    }


    return value
}