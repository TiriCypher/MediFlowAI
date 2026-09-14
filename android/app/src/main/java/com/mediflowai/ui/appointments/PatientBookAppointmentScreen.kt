package com.mediflowai.ui.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.viewmodel.PatientAppointmentsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private val BookingNavy =
    Color(0xFF081225)

private val BookingTeal =
    Color(0xFF00A6A6)

private val BookingBackground =
    Color(0xFFF5F8FB)

private val BookingMuted =
    Color(0xFF64748B)


@Composable
fun PatientBookAppointmentScreen(
    patientId: Int,
    preselectedDoctorId: Int? = null,
    onBooked: () -> Unit,
    onBack: () -> Unit,
    viewModel: PatientAppointmentsViewModel = viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()

    val context =
        LocalContext.current


    var selectedDoctorId by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedDepartmentId by remember {
        mutableStateOf<Int?>(null)
    }

    var preselectionApplied by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableStateOf<Calendar?>(null)
    }

    var selectedHour by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedMinute by remember {
        mutableStateOf<Int?>(null)
    }

    var reason by remember {
        mutableStateOf("")
    }

    var doctorExpanded by remember {
        mutableStateOf(false)
    }

    var departmentExpanded by remember {
        mutableStateOf(false)
    }


    // ------------------------------------------------
    // PRESELECT DOCTOR FROM DOCTOR DIRECTORY
    // ------------------------------------------------

    LaunchedEffect(
        preselectedDoctorId,
        state.doctors
    ) {

        if (
            !preselectionApplied &&
            preselectedDoctorId != null &&
            state.doctors.isNotEmpty()
        ) {

            val doctor =
                state.doctors.firstOrNull {
                    it.id ==
                            preselectedDoctorId
                }

            if (doctor != null) {

                selectedDoctorId =
                    doctor.id

                selectedDepartmentId =
                    doctor.department_id
            }

            preselectionApplied =
                true
        }
    }


    val selectedDoctor =
        state.doctors.firstOrNull {
            it.id ==
                    selectedDoctorId
        }


    val selectedDepartment =
        state.departments.firstOrNull {
            it.id ==
                    selectedDepartmentId
        }


    val dateText =
        selectedDate?.let {

            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(
                it.time
            )

        } ?: "Select Date"


    val timeText =
        if (
            selectedHour != null &&
            selectedMinute != null
        ) {

            val calendar =
                Calendar.getInstance()

            calendar.set(
                Calendar.HOUR_OF_DAY,
                selectedHour!!
            )

            calendar.set(
                Calendar.MINUTE,
                selectedMinute!!
            )

            SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(
                calendar.time
            )

        } else {

            "Select Time"
        }


    fun openDatePicker() {

        val calendar =
            selectedDate
                ?: Calendar.getInstance()


        DatePickerDialog(
            context,
            { _, year, month, day ->

                val picked =
                    Calendar.getInstance()

                picked.set(
                    year,
                    month,
                    day
                )

                selectedDate =
                    picked
            },
            calendar.get(
                Calendar.YEAR
            ),
            calendar.get(
                Calendar.MONTH
            ),
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        ).apply {

            datePicker.minDate =
                System.currentTimeMillis()

        }.show()
    }


    fun openTimePicker() {

        val calendar =
            Calendar.getInstance()


        TimePickerDialog(
            context,
            { _, hour, minute ->

                selectedHour =
                    hour

                selectedMinute =
                    minute
            },
            selectedHour
                ?: calendar.get(
                    Calendar.HOUR_OF_DAY
                ),
            selectedMinute
                ?: calendar.get(
                    Calendar.MINUTE
                ),
            false
        ).show()
    }


    fun buildAppointmentDateTime():
            String? {

        val date =
            selectedDate
                ?: return null

        val hour =
            selectedHour
                ?: return null

        val minute =
            selectedMinute
                ?: return null


        val result =
            date.clone() as Calendar


        result.set(
            Calendar.HOUR_OF_DAY,
            hour
        )

        result.set(
            Calendar.MINUTE,
            minute
        )

        result.set(
            Calendar.SECOND,
            0
        )

        result.set(
            Calendar.MILLISECOND,
            0
        )


        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.US
        ).format(
            result.time
        )
    }


    Scaffold(
        containerColor =
            BookingBackground
    ) { paddingValues ->


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


            // ------------------------------------------------
            // HEADER
            // ------------------------------------------------

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
                            BookingNavy
                    )
                }


                Column(
                    modifier =
                        Modifier.padding(
                            start =
                                4.dp
                        )
                ) {

                    Text(
                        text =
                            "Book Appointment",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            BookingNavy
                    )


                    Text(
                        text =
                            "Schedule your consultation",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            BookingMuted
                    )
                }
            }


            // ------------------------------------------------
            // DEPARTMENT
            // ------------------------------------------------

            BookingSectionTitle(
                title =
                    "Department"
            )


            SelectionCard(
                title =
                    selectedDepartment
                        ?.name
                        ?: "Select Department",

                subtitle =
                    if (
                        selectedDepartment != null
                    ) {
                        "Hospital department"
                    } else {
                        "Choose a department"
                    },

                icon =
                    Icons.Outlined
                        .LocalHospital,

                onClick = {
                    departmentExpanded =
                        true
                }
            )


            Box {

                DropdownMenu(
                    expanded =
                        departmentExpanded,

                    onDismissRequest = {
                        departmentExpanded =
                            false
                    }
                ) {

                    state.departments
                        .forEach { department ->

                            DropdownMenuItem(
                                text = {

                                    Column {

                                        Text(
                                            text =
                                                department.name,
                                            fontWeight =
                                                FontWeight
                                                    .SemiBold
                                        )


                                        department
                                            .description
                                            ?.let {

                                                Text(
                                                    text =
                                                        it,
                                                    style =
                                                        MaterialTheme
                                                            .typography
                                                            .bodySmall,
                                                    color =
                                                        BookingMuted
                                                )
                                            }
                                    }
                                },

                                onClick = {

                                    selectedDepartmentId =
                                        department.id

                                    // Reset doctor because
                                    // department changed.
                                    selectedDoctorId =
                                        null

                                    departmentExpanded =
                                        false
                                }
                            )
                        }
                }
            }


            // ------------------------------------------------
            // DOCTOR
            // ------------------------------------------------

            BookingSectionTitle(
                title =
                    "Doctor"
            )


            SelectionCard(
                title =
                    selectedDoctor
                        ?.name
                        ?: "Select Doctor",

                subtitle =
                    selectedDoctor?.let {

                        buildString {

                            if (
                                !it.specialization
                                    .isNullOrBlank()
                            ) {
                                append(
                                    it.specialization
                                )
                            }


                            if (
                                !it.availability
                                    .isNullOrBlank()
                            ) {

                                if (
                                    isNotEmpty()
                                ) {
                                    append(
                                        " • "
                                    )
                                }

                                append(
                                    it.availability
                                )
                            }


                            if (
                                isEmpty()
                            ) {
                                append(
                                    "Doctor information"
                                )
                            }
                        }

                    }
                        ?: "Choose an available specialist",

                icon =
                    Icons.Outlined
                        .MedicalServices,

                onClick = {
                    doctorExpanded =
                        true
                }
            )


            Box {

                DropdownMenu(
                    expanded =
                        doctorExpanded,

                    onDismissRequest = {
                        doctorExpanded =
                            false
                    }
                ) {


                    val filteredDoctors =
                        if (
                            selectedDepartmentId != null
                        ) {

                            state.doctors.filter {
                                it.department_id ==
                                        selectedDepartmentId
                            }

                        } else {

                            state.doctors
                        }


                    filteredDoctors
                        .forEach { doctor ->

                            DropdownMenuItem(
                                text = {

                                    Column {

                                        Text(
                                            text =
                                                doctor.name,
                                            fontWeight =
                                                FontWeight
                                                    .SemiBold
                                        )


                                        Text(
                                            text =
                                                buildString {

                                                    doctor
                                                        .specialization
                                                        ?.let {

                                                            append(
                                                                it
                                                            )
                                                        }


                                                    doctor
                                                        .availability
                                                        ?.let {

                                                            if (
                                                                isNotEmpty()
                                                            ) {

                                                                append(
                                                                    " • "
                                                                )
                                                            }

                                                            append(
                                                                it
                                                            )
                                                        }


                                                    if (
                                                        isEmpty()
                                                    ) {

                                                        append(
                                                            "Availability not specified"
                                                        )
                                                    }
                                                },

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall,

                                            color =
                                                BookingMuted
                                        )
                                    }
                                },


                                onClick = {

                                    selectedDoctorId =
                                        doctor.id

                                    // Always sync department
                                    // with selected doctor.
                                    selectedDepartmentId =
                                        doctor.department_id

                                    doctorExpanded =
                                        false
                                }
                            )
                        }
                }
            }


            // ------------------------------------------------
            // DATE AND TIME
            // ------------------------------------------------

            BookingSectionTitle(
                title =
                    "Date & Time"
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                DateTimeCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        dateText,

                    subtitle =
                        "Appointment date",

                    icon =
                        Icons.Outlined
                            .CalendarMonth,

                    onClick = {
                        openDatePicker()
                    }
                )


                DateTimeCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        timeText,

                    subtitle =
                        "Appointment time",

                    icon =
                        Icons.Outlined
                            .Schedule,

                    onClick = {
                        openTimePicker()
                    }
                )
            }


            // ------------------------------------------------
            // REASON
            // ------------------------------------------------

            BookingSectionTitle(
                title =
                    "Reason for Visit"
            )


            OutlinedTextField(
                value =
                    reason,

                onValueChange = {
                    reason = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        "Reason"
                    )
                },

                placeholder = {
                    Text(
                        "Describe your reason for consultation"
                    )
                },

                minLines =
                    4,

                shape =
                    RoundedCornerShape(
                        18.dp
                    )
            )


            // ------------------------------------------------
            // ERROR
            // ------------------------------------------------

            state.error?.let {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        CardDefaults
                            .cardColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .errorContainer
                            )
                ) {

                    Text(
                        modifier =
                            Modifier.padding(
                                14.dp
                            ),

                        text =
                            it,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onErrorContainer
                    )
                }
            }


            // ------------------------------------------------
            // BOOK BUTTON
            // ------------------------------------------------

            Button(
                onClick = {

                    val doctorId =
                        selectedDoctorId

                    val appointmentDateTime =
                        buildAppointmentDateTime()


                    if (
                        doctorId != null &&
                        appointmentDateTime != null
                    ) {

                        viewModel
                            .bookAppointment(
                                patientId =
                                    patientId,

                                doctorId =
                                    doctorId,

                                departmentId =
                                    selectedDepartmentId,

                                appointmentTime =
                                    appointmentDateTime,

                                reason =
                                    reason
                                        .trim()
                                        .ifBlank {
                                            null
                                        },

                                onSuccess =
                                    onBooked
                            )
                    }
                },

                enabled =
                    selectedDoctorId != null &&
                            selectedDate != null &&
                            selectedHour != null &&
                            selectedMinute != null &&
                            !state.isBooking,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            56.dp
                        ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                BookingNavy
                        )
            ) {

                Text(
                    text =
                        if (
                            state.isBooking
                        ) {
                            "Booking..."
                        } else {
                            "Confirm Appointment"
                        },

                    fontWeight =
                        FontWeight.SemiBold
                )
            }


            Text(
                text =
                    "Appointment status and priority are managed securely by the hospital system.",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    BookingMuted
            )


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )
        }
    }
}


@Composable
private fun BookingSectionTitle(
    title: String
) {

    Text(
        text =
            title.uppercase(),

        style =
            MaterialTheme
                .typography
                .labelLarge,

        fontWeight =
            FontWeight.Bold,

        color =
            BookingMuted
    )
}


@Composable
private fun SelectionCard(
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
                20.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
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
                            BookingTeal.copy(
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
                        icon,

                    contentDescription =
                        null,

                    tint =
                        BookingTeal
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
                            .titleMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        BookingNavy
                )


                Text(
                    text =
                        subtitle,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        BookingMuted
                )
            }


            Icon(
                imageVector =
                    Icons.Outlined
                        .ChevronRight,

                contentDescription =
                    null,

                tint =
                    BookingMuted
            )
        }
    }
}


@Composable
private fun DateTimeCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Card(
        modifier =
            modifier
                .clickable {
                    onClick()
                },

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

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    7.dp
                )
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    BookingTeal
            )


            Text(
                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    BookingNavy
            )


            Text(
                text =
                    subtitle,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    BookingMuted
            )
        }
    }
}