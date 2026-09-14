package com.mediflowai.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.SystemDashboardResponse
import com.mediflowai.viewmodel.AuthViewModel
import com.mediflowai.viewmodel.DashboardViewModel
import androidx.compose.material3.Scaffold
import com.mediflowai.navigation.MediFlowBottomBar
import com.mediflowai.navigation.Screen

private val PageBackground = Color(0xFFF6F8FC)

private val Navy900 = Color(0xFF081225)
private val Navy800 = Color(0xFF10213F)
private val Navy700 = Color(0xFF16345F)

private val Teal = Color(0xFF00A6A6)
private val Cyan = Color(0xFF38BDF8)

private val Emerald = Color(0xFF10B981)
private val Amber = Color(0xFFF59E0B)
private val Red = Color(0xFFEF4444)

private val TextPrimary = Color(0xFF101828)
private val TextSecondary = Color(0xFF667085)

private val CardBackground = Color.White
private val BorderColor = Color(0xFFE4EAF2)

@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {

    val dashboardViewModel: DashboardViewModel = viewModel()

    val backendStatus by
    dashboardViewModel.backendStatus.collectAsState()

    val dashboardData by
    dashboardViewModel.dashboardData.collectAsState()

    val dashboardError by
    dashboardViewModel.dashboardError.collectAsState()

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {

            MediFlowBottomBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBackground)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                PremiumHeader(
                    email = viewModel.getCurrentUserEmail()
                        ?: "Authenticated User",
                    backendStatus = backendStatus
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {

                    Spacer(modifier = Modifier.height(24.dp))

                    when {

                        dashboardError != null -> {

                            DashboardErrorCard(
                                error = dashboardError ?: "Unknown error",
                                onRetry = {
                                    dashboardViewModel.loadDashboard()
                                }
                            )
                        }

                        dashboardData == null -> {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                CircularProgressIndicator(
                                    color = Teal
                                )
                            }
                        }

                        else -> {

                            dashboardData?.let { data ->

                                DashboardContent(data)

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = {
                                        dashboardViewModel.loadDashboard()
                                        dashboardViewModel.checkBackend()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Navy900
                                    )
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Refresh Dashboard",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.logout()
                                        onLogout()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Navy900
                                    )
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = null
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Logout",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                AcademicDisclaimer()

                                Spacer(modifier = Modifier.height(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHeader(
    email: String,
    backendStatus: String
) {

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
                    top = 34.dp,
                    bottom = 28.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "MediFlow AI",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Predict. Prioritize. Care.",
                        color = Color.White.copy(alpha = 0.68f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.10f)
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Hospital Intelligence",
                color = Cyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Good to see you",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = email,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Emerald)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = backendStatus,
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: SystemDashboardResponse
) {

    val beds = data.resources.bed_summary
    val risk = data.patient_risk

    PremiumSectionTitle(
        title = "Hospital Overview",
        subtitle = "Live operational intelligence"
    )

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        PremiumMetricCard(
            modifier = Modifier.weight(1f),
            title = "Total Beds",
            value = beds.total_beds.toString(),
            icon = Icons.Default.Bed,
            accent = Cyan
        )

        PremiumMetricCard(
            modifier = Modifier.weight(1f),
            title = "Available",
            value = beds.available_beds.toString(),
            icon = Icons.Default.CheckCircle,
            accent = Emerald
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        PremiumMetricCard(
            modifier = Modifier.weight(1f),
            title = "Appointments",
            value = data.appointments.total_appointments.toString(),
            icon = Icons.Default.CalendarMonth,
            accent = Teal
        )

        PremiumMetricCard(
            modifier = Modifier.weight(1f),
            title = "Doctors",
            value = data.doctor_workload.total_doctors.toString(),
            icon = Icons.Default.MedicalServices,
            accent = Color(0xFF7C5CFC)
        )
    }

    Spacer(modifier = Modifier.height(30.dp))

    PremiumSectionTitle(
        title = "Bed Intelligence",
        subtitle = "Capacity and occupancy monitoring"
    )

    Spacer(modifier = Modifier.height(14.dp))

    BedOccupancyCard(data)

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        CompactBedCard(
            modifier = Modifier.weight(1f),
            title = "ICU",
            total = beds.total_icu_beds,
            available = beds.available_icu_beds,
            icon = Icons.Default.MonitorHeart
        )

        CompactBedCard(
            modifier = Modifier.weight(1f),
            title = "General",
            total = beds.total_general_beds,
            available = beds.available_general_beds,
            icon = Icons.Default.LocalHospital
        )
    }

    Spacer(modifier = Modifier.height(30.dp))

    PremiumSectionTitle(
        title = "Patient Risk",
        subtitle = "Latest ML readmission predictions"
    )

    Spacer(modifier = Modifier.height(14.dp))

    RiskOverviewCard(data)

    Spacer(modifier = Modifier.height(14.dp))

    PremiumCard {

        Text(
            text = "Patient Risk Details",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        risk.patients.forEachIndexed { index, patient ->

            PatientRiskRow(
                patientId = patient.patient_id,
                riskLevel = patient.risk_level,
                riskScore = patient.risk_score
            )

            if (index != risk.patients.lastIndex) {

                HorizontalDivider(
                    color = BorderColor
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(30.dp))

    PremiumSectionTitle(
        title = "Appointments",
        subtitle = "Scheduling activity and workload"
    )

    Spacer(modifier = Modifier.height(14.dp))

    PremiumCard {

        SummaryHeader(
            icon = Icons.Default.CalendarToday,
            title = "Appointment Summary",
            value = data.appointments.total_appointments.toString()
        )

        Spacer(modifier = Modifier.height(12.dp))

        data.appointments.status_summary
            .forEachIndexed { index, status ->

                DashboardInfoRow(
                    label = status.status,
                    value = status.count.toString()
                )

                if (
                    index !=
                    data.appointments.status_summary.lastIndex
                ) {
                    HorizontalDivider(
                        color = BorderColor
                    )
                }
            }
    }

    Spacer(modifier = Modifier.height(30.dp))

    PremiumSectionTitle(
        title = "Doctor Workload",
        subtitle = "Real-time clinical workload"
    )

    Spacer(modifier = Modifier.height(14.dp))

    data.doctor_workload.doctors.forEach { doctor ->

        PremiumCard {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Color(0xFFE8F5F5)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Teal
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = doctor.doctor_name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    doctor.specialization?.let {

                        Text(
                            text = it,
                            color = Teal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = BorderColor
            )

            DashboardInfoRow(
                label = "Active Appointments",
                value = doctor.active_appointments.toString()
            )

            HorizontalDivider(
                color = BorderColor
            )

            DashboardInfoRow(
                label = "Total Appointments",
                value = doctor.total_appointments.toString()
            )

            HorizontalDivider(
                color = BorderColor
            )

            DashboardInfoRow(
                label = "Availability",
                value = doctor.availability ?: "Not specified"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(18.dp))

    PremiumSectionTitle(
        title = "Department Capacity",
        subtitle = "Resource distribution across hospital departments"
    )

    Spacer(modifier = Modifier.height(14.dp))

    data.resources.department_summary.forEach { department ->

        PremiumCard {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Color(0xFFEAF6FF)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color(0xFF2388D9)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = department.department_name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            DashboardInfoRow(
                label = "Total Beds",
                value = department.total_beds.toString()
            )

            HorizontalDivider(color = BorderColor)

            DashboardInfoRow(
                label = "Available",
                value = department.available_beds.toString()
            )

            HorizontalDivider(color = BorderColor)

            DashboardInfoRow(
                label = "Occupied",
                value = department.occupied_beds.toString()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(18.dp))

    PremiumSectionTitle(
        title = "Bed Categories",
        subtitle = "Capacity by care type"
    )

    Spacer(modifier = Modifier.height(14.dp))

    data.resources.bed_type_summary.forEach { bedType ->

        PremiumCard {

            SummaryHeader(
                icon = Icons.Default.Bed,
                title = bedType.bed_type,
                value = bedType.total_beds.toString()
            )

            Spacer(modifier = Modifier.height(10.dp))

            DashboardInfoRow(
                label = "Available",
                value = bedType.available_beds.toString()
            )

            HorizontalDivider(color = BorderColor)

            DashboardInfoRow(
                label = "Occupied",
                value = bedType.occupied_beds.toString()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(18.dp))

    PremiumSectionTitle(
        title = "MediFlow Intelligence",
        subtitle = "AI and algorithmic optimization"
    )

    Spacer(modifier = Modifier.height(14.dp))

    IntelligenceCard(
        icon = Icons.Default.Psychology,
        title = "Readmission Risk AI",
        description =
            "Machine learning based patient readmission risk prediction.",
        accent = Color(0xFF7C5CFC)
    )

    Spacer(modifier = Modifier.height(12.dp))

    IntelligenceCard(
        icon = Icons.Default.AutoAwesome,
        title = "Smart Scheduling",
        description =
            "Priority-aware appointment scheduling and doctor load balancing.",
        accent = Cyan
    )

    Spacer(modifier = Modifier.height(12.dp))

    IntelligenceCard(
        icon = Icons.Default.Bed,
        title = "Resource Optimization",
        description =
            "Greedy hospital bed allocation based on care requirements and availability.",
        accent = Teal
    )
}

@Composable
private fun PremiumMetricCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
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
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        accent.copy(alpha = 0.12f)
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

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun BedOccupancyCard(
    data: SystemDashboardResponse
) {

    val beds = data.resources.bed_summary

    val occupancy =
        (beds.occupancy_percentage / 100.0)
            .toFloat()
            .coerceIn(0f, 1f)

    PremiumCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Bed Occupancy",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${beds.occupancy_percentage.toInt()}%",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color =
                    if (occupancy >= 0.8f)
                        Red.copy(alpha = 0.10f)
                    else
                        Teal.copy(alpha = 0.10f)
            ) {

                Text(
                    text =
                        "${beds.available_beds} available",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    color =
                        if (occupancy >= 0.8f)
                            Red
                        else
                            Teal,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = { occupancy },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color =
                if (occupancy >= 0.8f)
                    Red
                else
                    Teal,
            trackColor = Color(0xFFE9EEF5)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            BedLegend(
                value = beds.occupied_beds,
                label = "Occupied"
            )

            BedLegend(
                value = beds.available_beds,
                label = "Available"
            )

            BedLegend(
                value = beds.total_beds,
                label = "Total"
            )
        }
    }
}

@Composable
private fun BedLegend(
    value: Int,
    label: String
) {

    Column {

        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun CompactBedCard(
    modifier: Modifier,
    title: String,
    total: Int,
    available: Int,
    icon: ImageVector
) {

    PremiumCard(
        modifier = modifier
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Navy700
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "$available / $total",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Teal
        )

        Text(
            text = "Available",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun RiskOverviewCard(
    data: SystemDashboardResponse
) {

    val risk = data.patient_risk

    PremiumCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            RiskStat(
                modifier = Modifier.weight(1f),
                label = "LOW",
                value = risk.low_risk,
                color = Emerald
            )

            RiskStat(
                modifier = Modifier.weight(1f),
                label = "MEDIUM",
                value = risk.medium_risk,
                color = Amber
            )

            RiskStat(
                modifier = Modifier.weight(1f),
                label = "HIGH",
                value = risk.high_risk,
                color = Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                "${risk.total_patients} patients with latest risk predictions",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun RiskStat(
    modifier: Modifier,
    label: String,
    value: Int,
    color: Color
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PatientRiskRow(
    patientId: Int,
    riskLevel: String,
    riskScore: Double
) {

    val riskColor =
        when (riskLevel.uppercase()) {

            "LOW" -> Emerald
            "MEDIUM" -> Amber
            "HIGH" -> Red
            else -> TextSecondary
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Navy900.copy(alpha = 0.06f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Navy700,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Patient #$patientId",
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Text(
                text = "Readmission risk",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = riskColor.copy(alpha = 0.10f)
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 11.dp,
                    vertical = 6.dp
                ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = riskLevel,
                    color = riskColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "${(riskScore * 100).toInt()}%",
                    color = riskColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun IntelligenceCard(
    icon: ImageVector,
    title: String,
    description: String,
    accent: Color
) {

    PremiumCard {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        accent.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryHeader(
    icon: ImageVector,
    title: String,
    value: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Cyan.copy(alpha = 0.10f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF168AC0),
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900
        )
    }
}

@Composable
private fun DashboardInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = value,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PremiumSectionTitle(
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
private fun PremiumCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = CardDefaults.outlinedCardBorder(),
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

@Composable
private fun AcademicDisclaimer() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Navy900.copy(alpha = 0.045f),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.Top
        ) {

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "This application is an academic prototype and is not intended for clinical diagnosis or medical decision-making.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun DashboardErrorCard(
    error: String,
    onRetry: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF1F1)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Red
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Unable to load dashboard",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = error,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red
                )
            ) {
                Text("Try Again")
            }
        }
    }
}