package com.mediflowai.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.mediflowai.navigation.Screen
import com.mediflowai.navigation.StaffBottomBar
import com.mediflowai.viewmodel.StaffHomeViewModel


private val StaffNavy =
    Color(0xFF081225)

private val StaffTeal =
    Color(0xFF00A6A6)

private val StaffMuted =
    Color(0xFF64748B)

private val StaffBackground =
    Color(0xFFF5F8FB)

private val StaffGreen =
    Color(0xFF16A34A)

private val StaffAmber =
    Color(0xFFD97706)


@Composable
fun StaffHomeScreen(
    onNavigate: (String) -> Unit,
    viewModel:
    StaffHomeViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.loadDashboard()
    }


    Scaffold(
        containerColor =
            StaffBackground,

        bottomBar = {

            StaffBottomBar(
                currentRoute =
                    Screen.StaffHome.route,
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
                            "Unable to load dashboard",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            StaffNavy
                    )


                    Text(
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            ),
                        text =
                            state.error ?: "",
                        color =
                            StaffMuted
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


            state.dashboard != null -> {

                val dashboard =
                    state.dashboard!!

                val beds =
                    dashboard
                        .resources
                        .bed_summary

                val appointmentStatus =
                    dashboard
                        .appointments
                        .status_summary

                fun appointmentCount(
                    status: String
                ): Int {

                    return appointmentStatus
                        .firstOrNull {
                            it.status.equals(
                                status,
                                ignoreCase = true
                            )
                        }
                        ?.count
                        ?: 0
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
                            16.dp
                        )
                ) {


                    item {

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    24.dp
                                ),
                            colors =
                                CardDefaults
                                    .cardColors(
                                        containerColor =
                                            StaffNavy
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
                                        "Staff Operations",
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
                                    modifier =
                                        Modifier.padding(
                                            top = 4.dp
                                        ),
                                    text =
                                        "Hospital resource and appointment overview",
                                    color =
                                        Color.White.copy(
                                            alpha =
                                                0.72f
                                        )
                                )
                            }
                        }
                    }


                    item {

                        Text(
                            text =
                                "Bed Operations",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                StaffNavy
                        )
                    }


                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {

                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Total",
                                value =
                                    beds
                                        .total_beds
                                        .toString(),
                                accent =
                                    StaffTeal
                            )


                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Available",
                                value =
                                    beds
                                        .available_beds
                                        .toString(),
                                accent =
                                    StaffGreen
                            )


                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Occupied",
                                value =
                                    beds
                                        .occupied_beds
                                        .toString(),
                                accent =
                                    StaffAmber
                            )
                        }
                    }


                    item {

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigate(
                                            Screen
                                                .StaffBeds
                                                .route
                                        )
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

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            18.dp
                                        ),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Column {

                                    Text(
                                        text =
                                            "Occupancy",
                                        color =
                                            StaffMuted
                                    )


                                    Text(
                                        modifier =
                                            Modifier.padding(
                                                top = 4.dp
                                            ),
                                        text =
                                            "${beds.occupancy_percentage}%",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .headlineSmall,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            StaffNavy
                                    )
                                }


                                Icon(
                                    modifier =
                                        Modifier.size(
                                            30.dp
                                        ),
                                    imageVector =
                                        Icons.Outlined.Bed,
                                    contentDescription =
                                        null,
                                    tint =
                                        StaffTeal
                                )
                            }
                        }
                    }


                    item {

                        Text(
                            text =
                                "Appointments",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                StaffNavy
                        )
                    }


                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {

                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Total",
                                value =
                                    dashboard
                                        .appointments
                                        .total_appointments
                                        .toString(),
                                accent =
                                    StaffTeal
                            )


                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Scheduled",
                                value =
                                    appointmentCount(
                                        "SCHEDULED"
                                    )
                                        .toString(),
                                accent =
                                    StaffTeal
                            )
                        }
                    }


                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                )
                        ) {

                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Completed",
                                value =
                                    appointmentCount(
                                        "COMPLETED"
                                    )
                                        .toString(),
                                accent =
                                    StaffGreen
                            )


                            StaffMetricCard(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                title =
                                    "Cancelled",
                                value =
                                    appointmentCount(
                                        "CANCELLED"
                                    )
                                        .toString(),
                                accent =
                                    StaffAmber
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "Quick Operations",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                StaffNavy
                        )
                    }


                    item {

                        StaffQuickAction(
                            icon =
                                Icons.Outlined
                                    .CalendarMonth,
                            title =
                                "Appointment Operations",
                            subtitle =
                                "View and manage hospital appointments",
                            onClick = {
                                onNavigate(
                                    Screen
                                        .StaffAppointments
                                        .route
                                )
                            }
                        )
                    }


                    item {

                        StaffQuickAction(
                            icon =
                                Icons.Outlined.Bed,
                            title =
                                "Bed Management",
                            subtitle =
                                "View availability and occupancy",
                            onClick = {
                                onNavigate(
                                    Screen
                                        .StaffBeds
                                        .route
                                )
                            }
                        )
                    }


                    item {

                        StaffQuickAction(
                            icon =
                                Icons.Outlined.Tune,
                            title =
                                "Resource Optimization",
                            subtitle =
                                "Open smart allocation tools",
                            onClick = {
                                onNavigate(
                                    Screen
                                        .StaffOptimization
                                        .route
                                )
                            }
                        )
                    }


                    item {

                        StaffQuickAction(
                            icon =
                                Icons.Outlined
                                    .Notifications,
                            title =
                                "Operational Alerts",
                            subtitle =
                                "Review hospital alerts",
                            onClick = {
                                onNavigate(
                                    Screen
                                        .StaffAlerts
                                        .route
                                )
                            }
                        )
                    }


                    item {

                        Text(
                            text =
                                "This application is an academic prototype " +
                                        "and is not intended for clinical diagnosis " +
                                        "or medical decision-making.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                StaffMuted
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun StaffMetricCard(
    modifier: Modifier,
    title: String,
    value: String,
    accent: Color
) {

    Card(
        modifier =
            modifier,
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
                )
        ) {

            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    accent
            )


            Text(
                modifier =
                    Modifier.padding(
                        top = 4.dp
                    ),
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    StaffMuted
            )
        }
    }
}


@Composable
private fun StaffQuickAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
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
                            StaffTeal.copy(
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
                        StaffTeal
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
                        title,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        StaffNavy
                )


                Text(
                    modifier =
                        Modifier.padding(
                            top = 3.dp
                        ),
                    text =
                        subtitle,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        StaffMuted
                )
            }
        }
    }
}