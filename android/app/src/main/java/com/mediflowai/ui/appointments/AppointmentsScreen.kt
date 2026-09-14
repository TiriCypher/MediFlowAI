package com.mediflowai.ui.appointments

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.navigation.MediFlowBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.DashboardViewModel

private val PageBackground = Color(0xFFF6F8FC)

private val Navy900 = Color(0xFF081225)
private val Navy800 = Color(0xFF10213F)
private val Navy700 = Color(0xFF16345F)

private val Teal = Color(0xFF00A6A6)
private val Cyan = Color(0xFF38BDF8)

private val Emerald = Color(0xFF10B981)
private val Amber = Color(0xFFF59E0B)

private val TextPrimary = Color(0xFF101828)
private val TextSecondary = Color(0xFF667085)

private val BorderColor = Color(0xFFE4EAF2)

@Composable
fun AppointmentsScreen(
    onNavigate: (String) -> Unit
) {

    val viewModel: DashboardViewModel = viewModel()
    val data by viewModel.dashboardData.collectAsState()
    val error by viewModel.dashboardError.collectAsState()

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            MediFlowBottomBar(
                currentRoute = Screen.Appointments.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBackground)
                .verticalScroll(rememberScrollState())
        ) {

            AppointmentsHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                when {

                    error != null -> {

                        PremiumInfoCard {

                            Text(
                                text = "Unable to load appointments",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = error ?: "Unknown error",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    data == null -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Teal
                            )
                        }
                    }

                    else -> {

                        data?.let { dashboard ->

                            val appointments =
                                dashboard.appointments

                            SectionTitle(
                                title = "Appointment Overview",
                                subtitle = "Real scheduling activity"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                AppointmentMetricCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Total",
                                    value = appointments.total_appointments,
                                    icon = Icons.Default.CalendarMonth,
                                    accent = Cyan
                                )

                                AppointmentMetricCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Scheduled",
                                    value =
                                        appointments.status_summary
                                            .firstOrNull {
                                                it.status.equals(
                                                    "SCHEDULED",
                                                    ignoreCase = true
                                                )
                                            }
                                            ?.count ?: 0,
                                    icon = Icons.Default.EventAvailable,
                                    accent = Emerald
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            SectionTitle(
                                title = "Status Breakdown",
                                subtitle = "Current appointment states"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PremiumInfoCard {

                                appointments.status_summary
                                    .forEachIndexed { index, status ->

                                        StatusRow(
                                            status = status.status,
                                            count = status.count
                                        )

                                        if (
                                            index !=
                                            appointments.status_summary.lastIndex
                                        ) {
                                            HorizontalDivider(
                                                color = BorderColor
                                            )
                                        }
                                    }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            SectionTitle(
                                title = "Doctor Workload",
                                subtitle = "Appointment distribution by doctor"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            appointments.doctor_workload.forEach { doctor ->

                                DoctorLoadCard(
                                    name = doctor.doctor_name,
                                    specialization =
                                        doctor.specialization
                                            ?: "General",
                                    activeAppointments =
                                        doctor.active_appointments,
                                    totalAppointments =
                                        doctor.total_appointments
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Spacer(modifier = Modifier.height(22.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentsHeader() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Navy900,
                        Navy800,
                        Navy700
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 30.dp,
                    bottom = 26.dp
                )
        ) {

            Text(
                text = "Appointments",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Scheduling & clinical workload",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(22.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Cyan.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Cyan
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Smart Scheduling",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Priority-aware appointment optimization",
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentMetricCard(
    modifier: Modifier,
    title: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        accent.copy(alpha = 0.11f),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = value.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = title,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StatusRow(
    status: String,
    count: Int
) {

    val statusColor =
        when (status.uppercase()) {
            "SCHEDULED" -> Emerald
            "COMPLETED" -> Teal
            "CANCELLED" -> Color(0xFFEF4444)
            else -> Amber
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    statusColor,
                    CircleShape
                )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = status,
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = statusColor.copy(alpha = 0.1f)
        ) {

            Text(
                text = count.toString(),
                modifier = Modifier.padding(
                    horizontal = 11.dp,
                    vertical = 5.dp
                ),
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DoctorLoadCard(
    name: String,
    specialization: String,
    activeAppointments: Int,
    totalAppointments: Int
) {

    PremiumInfoCard {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Teal.copy(alpha = 0.1f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = Teal
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = specialization,
                    color = Teal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = BorderColor
        )

        InfoRow(
            label = "Active Appointments",
            value = activeAppointments.toString()
        )

        HorizontalDivider(
            color = BorderColor
        )

        InfoRow(
            label = "Total Appointments",
            value = totalAppointments.toString()
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = TextSecondary
        )

        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {

    Column {

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PremiumInfoCard(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}