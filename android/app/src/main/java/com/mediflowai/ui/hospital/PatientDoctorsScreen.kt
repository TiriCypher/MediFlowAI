package com.mediflowai.ui.hospital

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.navigation.PatientBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.PatientDoctorsViewModel


private val DoctorsNavy =
    Color(0xFF081225)

private val DoctorsTeal =
    Color(0xFF00A6A6)

private val DoctorsBackground =
    Color(0xFFF5F8FB)

private val DoctorsMuted =
    Color(0xFF64748B)


@Composable
fun PatientDoctorsScreen(
    onNavigate: (String) -> Unit,
    onBookDoctor: (Int) -> Unit,
    viewModel: PatientDoctorsViewModel = viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedDepartmentId by remember {
        mutableIntStateOf(0)
    }


    val filteredDoctors =
        state.doctors.filter { doctor ->

            val matchesDepartment =
                selectedDepartmentId == 0 ||
                        doctor.department_id ==
                        selectedDepartmentId

            val search =
                searchText.trim()

            val matchesSearch =
                search.isBlank() ||
                        doctor.name.contains(
                            search,
                            ignoreCase = true
                        ) ||
                        doctor.specialization
                            ?.contains(
                                search,
                                ignoreCase = true
                            ) == true ||
                        doctor.department_name
                            ?.contains(
                                search,
                                ignoreCase = true
                            ) == true

            matchesDepartment &&
                    matchesSearch
        }


    Scaffold(
        containerColor =
            DoctorsBackground,

        bottomBar = {

            PatientBottomBar(
                currentRoute =
                    Screen.PatientDoctors.route,
                onNavigate =
                    onNavigate
            )
        }
    ) { paddingValues ->


        when {

            state.isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment =
                        Alignment.Center
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
                        Arrangement.Center,
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            "Unable to load doctors",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            state.error ?: ""
                    )

                    Button(
                        modifier =
                            Modifier.padding(
                                top = 16.dp
                            ),
                        onClick = {
                            viewModel.loadDoctors()
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
                        PaddingValues(
                            horizontal = 18.dp,
                            vertical = 20.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            16.dp
                        )
                ) {


                    item {

                        Column {

                            Text(
                                text =
                                    "Find a Doctor",
                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineMedium,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    DoctorsNavy
                            )

                            Text(
                                text =
                                    "Find the right specialist for your care.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,
                                color =
                                    DoctorsMuted
                            )
                        }
                    }


                    item {

                        OutlinedTextField(
                            value =
                                searchText,

                            onValueChange = {
                                searchText = it
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Outlined
                                            .Search,
                                    contentDescription =
                                        null
                                )
                            },

                            label = {
                                Text(
                                    "Search doctors"
                                )
                            },

                            placeholder = {
                                Text(
                                    "Name or specialization"
                                )
                            },

                            singleLine = true,

                            shape =
                                RoundedCornerShape(
                                    18.dp
                                )
                        )
                    }


                    item {

                        Text(
                            text =
                                "Departments",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                DoctorsNavy
                        )
                    }


                    item {

                        LazyRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {

                            item {

                                FilterChip(
                                    selected =
                                        selectedDepartmentId ==
                                                0,

                                    onClick = {
                                        selectedDepartmentId =
                                            0
                                    },

                                    label = {
                                        Text("All")
                                    }
                                )
                            }


                            items(
                                items =
                                    state.departments,
                                key = {
                                    it.id
                                }
                            ) { department ->

                                FilterChip(
                                    selected =
                                        selectedDepartmentId ==
                                                department.id,

                                    onClick = {

                                        selectedDepartmentId =
                                            department.id
                                    },

                                    label = {

                                        Text(
                                            department.name
                                        )
                                    }
                                )
                            }
                        }
                    }


                    item {

                        Text(
                            text =
                                "${filteredDoctors.size} doctor" +
                                        if (
                                            filteredDoctors.size ==
                                            1
                                        )
                                            ""
                                        else
                                            "s",
                            style =
                                MaterialTheme.typography
                                    .labelLarge,
                            color =
                                DoctorsMuted
                        )
                    }


                    if (
                        filteredDoctors.isEmpty()
                    ) {

                        item {

                            Card(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
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
                                            24.dp
                                        ),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text =
                                            "No doctors found",
                                        fontWeight =
                                            FontWeight.Bold
                                    )

                                    Text(
                                        text =
                                            "Try another search or department.",
                                        color =
                                            DoctorsMuted
                                    )
                                }
                            }
                        }

                    } else {

                        items(
                            items =
                                filteredDoctors,
                            key = {
                                it.id
                            }
                        ) { doctor ->

                            PatientDoctorCard(
                                doctor =
                                    doctor,

                                onBook = {

                                    onBookDoctor(
                                        doctor.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun PatientDoctorCard(
    doctor: DoctorItem,
    onBook: () -> Unit
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
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
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


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Box(
                    modifier =
                        Modifier
                            .size(
                                54.dp
                            )
                            .background(
                                DoctorsTeal.copy(
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
                                .MedicalServices,
                        contentDescription =
                            null,
                        tint =
                            DoctorsTeal
                    )
                }


                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                start =
                                    14.dp
                            )
                ) {

                    Text(
                        text =
                            doctor.name,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            DoctorsNavy
                    )

                    Text(
                        text =
                            doctor.specialization
                                ?: "General Medicine",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            DoctorsTeal,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    doctor.department_name
                        ?.let {

                            Text(
                                text = it,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    DoctorsMuted
                            )
                        }
                }
            }


            doctor.availability?.let {

                Text(
                    text =
                        "Available: $it",
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color =
                        DoctorsMuted
                )
            }


            Button(
                onClick =
                    onBook,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        15.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                DoctorsNavy
                        )
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined
                            .CalendarMonth,
                    contentDescription =
                        null,
                    modifier =
                        Modifier.size(
                            18.dp
                        )
                )

                Text(
                    modifier =
                        Modifier.padding(
                            start = 8.dp
                        ),
                    text =
                        "Book Appointment"
                )
            }
        }
    }
}