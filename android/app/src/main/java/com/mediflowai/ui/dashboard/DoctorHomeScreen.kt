package com.mediflowai.ui.dashboard

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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.viewmodel.DoctorHomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.mediflowai.navigation.DoctorBottomBar
import com.mediflowai.navigation.Screen


private val DoctorNavy =
    Color(0xFF081225)

private val DoctorTeal =
    Color(0xFF00A6A6)

private val DoctorBackground =
    Color(0xFFF5F8FB)

private val DoctorMuted =
    Color(0xFF64748B)

private val DoctorGreen =
    Color(0xFF16A34A)


@Composable
fun DoctorHomeScreen(
    email: String? = null,
    onNavigate: (String) -> Unit,
    viewModel: DoctorHomeViewModel = viewModel()
){

    val state by
    viewModel.uiState.collectAsState()


    Scaffold(
        containerColor =
            DoctorBackground,

        bottomBar = {

            DoctorBottomBar(
                currentRoute =
                    Screen.DoctorHome.route,

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
                            "Unable to load doctor dashboard",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            DoctorMuted
                    )


                    Button(
                        modifier =
                            Modifier.padding(
                                top = 18.dp
                            ),
                        onClick = {
                            viewModel
                                .loadDashboard()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            state.doctor != null -> {

                val doctor =
                    state.doctor!!


                val activeAppointments =
                    state.appointments
                        .filter {
                            !it.status.equals(
                                "CANCELLED",
                                ignoreCase = true
                            )
                        }


                val todayCount =
                    activeAppointments.count {
                        isToday(
                            it.appointment_time
                        )
                    }


                val scheduledCount =
                    state.appointments.count {
                        it.status.equals(
                            "SCHEDULED",
                            ignoreCase = true
                        )
                    }


                val completedCount =
                    state.appointments.count {
                        it.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    }


                val nextAppointment =
                    findNextAppointment(
                        state.appointments
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
                                vertical = 20.dp
                            ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            18.dp
                        )
                ) {


                    // --------------------------------
                    // HEADER
                    // --------------------------------

                    Text(
                        text =
                            "MediFlow AI",
                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,
                        color =
                            DoctorTeal,
                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(
                        text =
                            "Doctor Workspace",
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorNavy
                    )


                    // --------------------------------
                    // PROFILE HEADER
                    // --------------------------------

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
                                    DoctorNavy
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


                            Column(
                                modifier =
                                    Modifier.padding(
                                        start = 16.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        doctor.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        Color.White
                                )


                                doctor.specialization
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.let {

                                        Text(
                                            text = it,
                                            color =
                                                Color.White.copy(
                                                    alpha = 0.82f
                                                )
                                        )
                                    }


                                doctor.department_name
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?.let {

                                        Text(
                                            text = it,
                                            color =
                                                DoctorTeal,
                                            fontWeight =
                                                FontWeight.SemiBold
                                        )
                                    }


                                email?.let {

                                    Text(
                                        modifier =
                                            Modifier.padding(
                                                top = 4.dp
                                            ),
                                        text = it,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,
                                        color =
                                            Color.White.copy(
                                                alpha = 0.60f
                                            )
                                    )
                                }
                            }
                        }
                    }


                    // --------------------------------
                    // OVERVIEW
                    // --------------------------------

                    Text(
                        text =
                            "Today's Overview",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorNavy
                    )


                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        DoctorStatCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Today",
                            value =
                                todayCount.toString()
                        )


                        DoctorStatCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Scheduled",
                            value =
                                scheduledCount
                                    .toString()
                        )
                    }


                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        DoctorStatCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Completed",
                            value =
                                completedCount
                                    .toString()
                        )


                        DoctorStatCard(
                            modifier =
                                Modifier.weight(1f),
                            title =
                                "Assigned",
                            value =
                                state.appointments
                                    .size
                                    .toString()
                        )
                    }


                    // --------------------------------
                    // NEXT APPOINTMENT
                    // --------------------------------

                    Text(
                        text =
                            "Next Appointment",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorNavy
                    )


                    if (nextAppointment != null) {

                        NextAppointmentCard(
                            appointment =
                                nextAppointment
                        )

                    } else {

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
                                        20.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        "No upcoming appointments",
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        DoctorNavy
                                )


                                Text(
                                    text =
                                        "There are currently no scheduled future appointments assigned to you.",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    color =
                                        DoctorMuted
                                )
                            }
                        }
                    }


                    // --------------------------------
                    // DOCTOR SCHEDULE
                    // --------------------------------

                    Text(
                        text =
                            "My Schedule",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorNavy
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
                                            46.dp
                                        )
                                        .background(
                                            DoctorTeal.copy(
                                                alpha = 0.10f
                                            ),
                                            CircleShape
                                        ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined
                                            .Schedule,
                                    contentDescription =
                                        null,
                                    tint =
                                        DoctorTeal
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
                                        "Availability",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    color =
                                        DoctorMuted
                                )


                                Text(
                                    text =
                                        doctor.availability
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Not specified",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge,
                                    fontWeight =
                                        FontWeight.SemiBold,
                                    color =
                                        DoctorNavy
                                )
                            }
                        }
                    }


                    // --------------------------------
                    // DISCLAIMER
                    // --------------------------------

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
                                    DoctorTeal.copy(
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
                                DoctorMuted
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DoctorStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
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
                    DoctorNavy
            )


            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    DoctorMuted
            )
        }
    }
}


@Composable
private fun NextAppointmentCard(
    appointment: AppointmentItem
) {

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
                    10.dp
                )
        ) {


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined
                            .CalendarMonth,
                    contentDescription =
                        null,
                    tint =
                        DoctorTeal
                )


                Text(
                    modifier =
                        Modifier.padding(
                            start = 10.dp
                        ),
                    text =
                        formatAppointmentDateTime(
                            appointment.appointment_time
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        DoctorNavy
                )
            }


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
                                .bodyMedium,
                        color =
                            DoctorMuted
                    )
                }


            Text(
                text =
                    appointment.status.uppercase(),
                style =
                    MaterialTheme
                        .typography
                        .labelLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    if (
                        appointment.status.equals(
                            "COMPLETED",
                            ignoreCase = true
                        )
                    ) {
                        DoctorGreen
                    } else {
                        DoctorTeal
                    }
            )
        }
    }
}


private fun parseAppointmentDate(
    value: String
): Date? {

    val patterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )


    for (pattern in patterns) {

        try {

            val formatter =
                SimpleDateFormat(
                    pattern,
                    Locale.US
                )


            formatter.isLenient =
                false


            val parsed =
                formatter.parse(
                    value
                )


            if (parsed != null) {
                return parsed
            }

        } catch (_: Exception) {
        }
    }


    return null
}


private fun isToday(
    value: String
): Boolean {

    val date =
        parseAppointmentDate(
            value
        ) ?: return false


    val appointmentCalendar =
        Calendar.getInstance().apply {
            time = date
        }


    val today =
        Calendar.getInstance()


    return (
            appointmentCalendar.get(
                Calendar.YEAR
            ) ==
                    today.get(
                        Calendar.YEAR
                    ) &&
                    appointmentCalendar.get(
                        Calendar.DAY_OF_YEAR
                    ) ==
                    today.get(
                        Calendar.DAY_OF_YEAR
                    )
            )
}


private fun findNextAppointment(
    appointments:
    List<AppointmentItem>
): AppointmentItem? {

    val now =
        Date()


    return appointments
        .filter {

            !it.status.equals(
                "CANCELLED",
                ignoreCase = true
            ) &&
                    !it.status.equals(
                        "COMPLETED",
                        ignoreCase = true
                    )
        }
        .mapNotNull { appointment ->

            val date =
                parseAppointmentDate(
                    appointment
                        .appointment_time
                )


            if (
                date != null &&
                !date.before(now)
            ) {

                appointment to date

            } else {

                null
            }
        }
        .minByOrNull {
            it.second
        }
        ?.first
}


private fun formatAppointmentDateTime(
    value: String
): String {

    val date =
        parseAppointmentDate(
            value
        ) ?: return value


    return SimpleDateFormat(
        "dd MMM yyyy • hh:mm a",
        Locale.getDefault()
    ).format(
        date
    )
}