package com.mediflowai.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonSearch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.navigation.PatientBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.PatientAppointmentsViewModel
import com.mediflowai.viewmodel.PatientViewModel
import java.text.SimpleDateFormat
import java.util.Locale


private val Navy =
    Color(0xFF081225)

private val NavyLight =
    Color(0xFF10233F)

private val Teal =
    Color(0xFF00A6A6)

private val Cyan =
    Color(0xFF38BDF8)

private val SoftBackground =
    Color(0xFFF5F8FB)

private val SoftText =
    Color(0xFF64748B)


@Composable
fun PatientHomeScreen(
    email: String? = null,
    patientViewModel: PatientViewModel = viewModel(),
    appointmentsViewModel:
    PatientAppointmentsViewModel = viewModel(),
    onNavigate: (String) -> Unit
) {

    val uiState by
    patientViewModel.uiState
        .collectAsState()

    val appointmentState by
    appointmentsViewModel.uiState
        .collectAsState()


    Scaffold(
        containerColor =
            SoftBackground,

        bottomBar = {

            PatientBottomBar(
                currentRoute =
                    Screen.PatientHome.route,

                onNavigate =
                    onNavigate
            )
        }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    paddingValues
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {


            when {

                // ----------------------------------------
                // LOADING
                // ----------------------------------------

                uiState.isLoading -> {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(60.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                // ----------------------------------------
                // ERROR
                // ----------------------------------------

                uiState.error != null -> {

                    ErrorCard(
                        message =
                            uiState.error
                                ?: "Unable to load patient profile.",

                        onRetry = {

                            patientViewModel
                                .loadPatientProfile()
                        }
                    )
                }


                // ----------------------------------------
                // SUCCESS
                // ----------------------------------------

                uiState.patient != null -> {

                    val patient =
                        uiState.patient!!

                    val upcomingAppointment =
                        appointmentState.appointments
                            .filter {
                                it.status.uppercase() !=
                                        "CANCELLED"
                            }
                            .minByOrNull {
                                it.appointment_time
                            }

                    val upcomingDoctor =
                        upcomingAppointment?.let {
                                appointment ->

                            appointmentState.doctors
                                .firstOrNull {
                                    it.id ==
                                            appointment.doctor_id
                                }
                        }

                    val upcomingDepartment =
                        upcomingAppointment?.let {
                                appointment ->

                            appointmentState.departments
                                .firstOrNull {
                                    it.id ==
                                            appointment.department_id
                                }
                        }


                    PatientHeader(
                        name =
                            patient.name,

                        email =
                            email
                    )


                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal =
                                    18.dp,

                                vertical =
                                    20.dp
                            ),

                        verticalArrangement =
                            Arrangement
                                .spacedBy(
                                    22.dp
                                )
                    ) {


                        // --------------------------------
                        // UPCOMING APPOINTMENT
                        // --------------------------------

                        UpcomingAppointmentCard(
                            appointment =
                                upcomingAppointment,

                            doctorName =
                                upcomingDoctor?.name,

                            departmentName =
                                upcomingDepartment?.name
                                    ?: upcomingDoctor
                                        ?.department_name,

                            onClick = {

                                onNavigate(
                                    Screen
                                        .PatientAppointments
                                        .route
                                )
                            }
                        )


                        // --------------------------------
                        // QUICK ACTIONS
                        // --------------------------------

                        SectionTitle(
                            title =
                                "Quick Actions"
                        )


                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .spacedBy(
                                        12.dp
                                    )
                        ) {


                            QuickActionCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                title =
                                    "Book\nAppointment",

                                icon =
                                    Icons
                                        .Outlined
                                        .CalendarMonth,

                                onClick = {

                                    onNavigate(
                                        Screen
                                            .PatientBookAppointment
                                            .route
                                    )
                                }
                            )


                            QuickActionCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                title =
                                    "My\nAppointments",

                                icon =
                                    Icons
                                        .Outlined
                                        .MedicalServices,

                                onClick = {

                                    onNavigate(
                                        Screen
                                            .PatientAppointments
                                            .route
                                    )
                                }
                            )
                        }


                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .spacedBy(
                                        12.dp
                                    )
                        ) {


                            QuickActionCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                title =
                                    "Find\nDoctors",

                                icon =
                                    Icons
                                        .Outlined
                                        .PersonSearch,

                                onClick = {

                                    onNavigate(
                                        Screen
                                            .PatientDoctors
                                            .route
                                    )
                                }
                            )


                            QuickActionCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                title =
                                    "Departments",

                                icon =
                                    Icons
                                        .Outlined
                                        .LocalHospital,

                                onClick = {

                                    onNavigate(
                                        Screen
                                            .PatientDoctors
                                            .route
                                    )
                                }
                            )
                        }


                        // --------------------------------
                        // HEALTH SNAPSHOT
                        // --------------------------------

                        SectionTitle(
                            title =
                                "Health Snapshot"
                        )


                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .spacedBy(
                                        12.dp
                                    )
                        ) {


                            SnapshotCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                label =
                                    "Blood Group",

                                value =
                                    patient
                                        .blood_group
                                        ?: "--"
                            )


                            SnapshotCard(
                                modifier =
                                    Modifier
                                        .weight(1f),

                                label =
                                    "Gender",

                                value =
                                    patient
                                        .gender
                                        ?: "--"
                            )
                        }


                        SnapshotCard(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            label =
                                "Patient ID",

                            value =
                                "#${patient.id}"
                        )


                        // --------------------------------
                        // RECENT ACTIVITY
                        // --------------------------------

                        SectionTitle(
                            title =
                                "Recent Activity"
                        )


                        ActionRowCard(
                            title =
                                "Alerts & Notifications",

                            subtitle =
                                "View your latest hospital alerts",

                            icon =
                                Icons
                                    .Outlined
                                    .Notifications,

                            onClick = {

                                onNavigate(
                                    Screen
                                        .PatientAlerts
                                        .route
                                )
                            }
                        )


                        ActionRowCard(
                            title =
                                "My Profile",

                            subtitle =
                                "Personal and medical information",

                            icon =
                                Icons
                                    .Outlined
                                    .Person,

                            onClick = {

                                onNavigate(
                                    Screen
                                        .PatientProfile
                                        .route
                                )
                            }
                        )


                        AcademicDisclaimer()


                        Spacer(
                            modifier =
                                Modifier
                                    .height(
                                        20.dp
                                    )
                        )
                    }
                }
            }
        }
    }
}


// ------------------------------------------------------------
// HEADER
// ------------------------------------------------------------

@Composable
private fun PatientHeader(
    name: String,
    email: String?
) {

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(

                    brush =
                        Brush
                            .verticalGradient(

                                colors =
                                    listOf(
                                        Navy,
                                        NavyLight
                                    )
                            )
                )
                .padding(
                    start =
                        22.dp,

                    end =
                        22.dp,

                    top =
                        34.dp,

                    bottom =
                        30.dp
                )
    ) {


        Column {


            Text(
                text =
                    "MediFlow AI",

                color =
                    Teal,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier
                        .height(
                            18.dp
                        )
            )


            Text(
                text =
                    "Welcome back,",

                color =
                    Color.White.copy(
                        alpha =
                            0.72f
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge
            )


            Text(
                text =
                    name,

                color =
                    Color.White,

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier
                        .height(
                            6.dp
                        )
            )


            Text(
                text =
                    "Your healthcare, simplified.",

                color =
                    Color.White.copy(
                        alpha =
                            0.72f
                    ),

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            email?.let {


                Spacer(
                    modifier =
                        Modifier
                            .height(
                                8.dp
                            )
                )


                Text(
                    text =
                        it,

                    color =
                        Cyan,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }
        }
    }
}


// ------------------------------------------------------------
// UPCOMING APPOINTMENT CARD
// ------------------------------------------------------------

@Composable
private fun UpcomingAppointmentCard(
    appointment: AppointmentItem?,
    doctorName: String?,
    departmentName: String?,
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
                22.dp
            ),

        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        Color.White
                ),

        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        2.dp
                )
    ) {


        Column(
            modifier =
                Modifier
                    .padding(
                        20.dp
                    ),

            verticalArrangement =
                Arrangement
                    .spacedBy(
                        8.dp
                    )
        ) {


            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),

                verticalAlignment =
                    Alignment
                        .CenterVertically
            ) {


                Box(
                    modifier =
                        Modifier
                            .size(
                                46.dp
                            )
                            .clip(
                                CircleShape
                            )
                            .background(
                                Teal.copy(
                                    alpha =
                                        0.12f
                                )
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {


                    Icon(
                        imageVector =
                            Icons
                                .Outlined
                                .CalendarMonth,

                        contentDescription =
                            null,

                        tint =
                            Teal
                    )
                }


                Column(
                    modifier =
                        Modifier
                            .padding(
                                start =
                                    14.dp
                            )
                            .weight(
                                1f
                            )
                ) {


                    Text(
                        text =
                            "Upcoming Appointment",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(
                        text =
                            if (
                                appointment != null
                            )
                                "Your next scheduled visit"
                            else
                                "No upcoming appointment",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            SoftText
                    )
                }


                Icon(
                    imageVector =
                        Icons
                            .Outlined
                            .ChevronRight,

                    contentDescription =
                        null,

                    tint =
                        SoftText
                )
            }


            if (
                appointment != null
            ) {


                Spacer(
                    modifier =
                        Modifier
                            .height(
                                8.dp
                            )
                )


                Text(
                    text =
                        doctorName
                            ?: "Doctor",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Navy
                )


                Text(
                    text =
                        departmentName
                            ?: "Department",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        SoftText
                )


                Text(
                    text =
                        formatAppointmentDateTime(
                            appointment
                                .appointment_time
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,

                    fontWeight =
                        FontWeight
                            .SemiBold
                )


                Text(
                    text =
                        appointment.status,

                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,

                    color =
                        Teal,

                    fontWeight =
                        FontWeight.Bold
                )

            } else {


                Text(
                    text =
                        "Book an appointment to see it here.",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        SoftText
                )
            }
        }
    }
}


// ------------------------------------------------------------
// QUICK ACTION CARD
// ------------------------------------------------------------

@Composable
private fun QuickActionCard(
    modifier: Modifier =
        Modifier,

    title: String,

    icon: ImageVector,

    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier
                .height(
                    120.dp
                )
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        Color.White
                ),

        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        1.dp
                )
    ) {


        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        16.dp
                    ),

            verticalArrangement =
                Arrangement
                    .SpaceBetween
        ) {


            Box(
                modifier =
                    Modifier
                        .size(
                            42.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                13.dp
                            )
                        )
                        .background(
                            Teal.copy(
                                alpha =
                                    0.10f
                            )
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
                        Teal
                )
            }


            Text(
                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight
                        .SemiBold
            )
        }
    }
}


// ------------------------------------------------------------
// SNAPSHOT CARD
// ------------------------------------------------------------

@Composable
private fun SnapshotCard(
    modifier: Modifier =
        Modifier,

    label: String,

    value: String
) {

    Card(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                18.dp
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
                Modifier
                    .padding(
                        18.dp
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
                    SoftText
            )


            Spacer(
                modifier =
                    Modifier
                        .height(
                            6.dp
                        )
            )


            Text(
                text =
                    value,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy
            )
        }
    }
}


// ------------------------------------------------------------
// ACTION ROW
// ------------------------------------------------------------

@Composable
private fun ActionRowCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
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
                18.dp
            ),

        colors =
            CardDefaults
                .cardColors(
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
                Alignment
                    .CenterVertically
        ) {


            Box(
                modifier =
                    Modifier
                        .size(
                            44.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                14.dp
                            )
                        )
                        .background(
                            Navy.copy(
                                alpha =
                                    0.06f
                            )
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
                        Navy
                )
            }


            Column(
                modifier =
                    Modifier
                        .weight(
                            1f
                        )
                        .padding(
                            horizontal =
                                14.dp
                        )
            ) {


                Text(
                    text =
                        title,

                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,

                    fontWeight =
                        FontWeight
                            .SemiBold
                )


                Text(
                    text =
                        subtitle,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        SoftText
                )
            }


            Icon(
                imageVector =
                    Icons
                        .Outlined
                        .ChevronRight,

                contentDescription =
                    null,

                tint =
                    SoftText
            )
        }
    }
}


// ------------------------------------------------------------
// SECTION TITLE
// ------------------------------------------------------------

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text =
            title,

        style =
            MaterialTheme
                .typography
                .titleLarge,

        fontWeight =
            FontWeight.Bold,

        color =
            Navy
    )
}


// ------------------------------------------------------------
// ERROR CARD
// ------------------------------------------------------------

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    20.dp
                ),

        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {


        Column(
            modifier =
                Modifier
                    .padding(
                        20.dp
                    ),

            verticalArrangement =
                Arrangement
                    .spacedBy(
                        12.dp
                    )
        ) {


            Text(
                text =
                    "Unable to load your profile",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Text(
                text =
                    message
            )


            Button(
                onClick =
                    onRetry
            ) {

                Text(
                    text =
                        "Retry"
                )
            }
        }
    }
}


// ------------------------------------------------------------
// DISCLAIMER
// ------------------------------------------------------------

@Composable
private fun AcademicDisclaimer() {

    Card(
        modifier =
            Modifier
                .fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        Navy.copy(
                            alpha =
                                0.05f
                        )
                )
    ) {


        Text(
            modifier =
                Modifier
                    .padding(
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
                SoftText
        )
    }
}


// ------------------------------------------------------------
// DATE / TIME FORMATTER
// ------------------------------------------------------------

private fun formatAppointmentDateTime(
    value: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.US
            )

        val output =
            SimpleDateFormat(
                "dd MMM yyyy • hh:mm a",
                Locale.getDefault()
            )

        val date =
            input.parse(
                value
            )

        if (
            date != null
        ) {

            output.format(
                date
            )

        } else {

            value
        }

    } catch (
        e: Exception
    ) {

        value
    }
}