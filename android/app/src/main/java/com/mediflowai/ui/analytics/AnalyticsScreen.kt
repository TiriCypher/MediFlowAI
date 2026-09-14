package com.mediflowai.ui.analytics

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val Red = Color(0xFFEF4444)
private val Purple = Color(0xFF7C5CFC)

private val TextPrimary = Color(0xFF101828)
private val TextSecondary = Color(0xFF667085)
private val BorderColor = Color(0xFFE4EAF2)

@Composable
fun AnalyticsScreen(
    onNavigate: (String) -> Unit
) {

    val viewModel: DashboardViewModel = viewModel()

    val data by viewModel.dashboardData.collectAsState()
    val error by viewModel.dashboardError.collectAsState()

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            MediFlowBottomBar(
                currentRoute = Screen.Analytics.route,
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

            AnalyticsHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                when {

                    error != null -> {

                        AnalyticsCard {

                            Text(
                                text = "Unable to load analytics",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
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
                                .height(240.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator(
                                color = Teal
                            )
                        }
                    }

                    else -> {

                        data?.let { dashboard ->

                            val beds =
                                dashboard.resources.bed_summary

                            val risk =
                                dashboard.patient_risk

                            SectionTitle(
                                title = "AI Risk Analytics",
                                subtitle = "Latest readmission prediction distribution"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            AnalyticsCard {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {

                                    RiskDistributionCard(
                                        modifier = Modifier.weight(1f),
                                        label = "LOW",
                                        value = risk.low_risk,
                                        total = risk.total_patients,
                                        color = Emerald
                                    )

                                    RiskDistributionCard(
                                        modifier = Modifier.weight(1f),
                                        label = "MEDIUM",
                                        value = risk.medium_risk,
                                        total = risk.total_patients,
                                        color = Amber
                                    )

                                    RiskDistributionCard(
                                        modifier = Modifier.weight(1f),
                                        label = "HIGH",
                                        value = risk.high_risk,
                                        total = risk.total_patients,
                                        color = Red
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                HorizontalDivider(
                                    color = BorderColor
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                Purple.copy(alpha = 0.10f),
                                                RoundedCornerShape(13.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Psychology,
                                            contentDescription = null,
                                            tint = Purple
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {

                                        Text(
                                            text = "${risk.total_patients} patients analysed",
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )

                                        Text(
                                            text = "Latest stored ML prediction per patient",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            SectionTitle(
                                title = "Hospital Utilization",
                                subtitle = "Capacity and operational efficiency"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            UtilizationCard(
                                title = "Bed Occupancy",
                                value = beds.occupancy_percentage,
                                subtitle =
                                    "${beds.occupied_beds} occupied • ${beds.available_beds} available"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                MiniAnalyticsCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Bed,
                                    label = "Total Beds",
                                    value = beds.total_beds.toString(),
                                    accent = Cyan
                                )

                                MiniAnalyticsCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Groups,
                                    label = "Doctors",
                                    value = dashboard.doctor_workload.total_doctors.toString(),
                                    accent = Teal
                                )
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            SectionTitle(
                                title = "Doctor Workload Analytics",
                                subtitle = "Clinical workload distribution"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            dashboard.doctor_workload.doctors.forEach { doctor ->

                                DoctorAnalyticsCard(
                                    name = doctor.doctor_name,
                                    specialization =
                                        doctor.specialization ?: "General",
                                    activeAppointments =
                                        doctor.active_appointments,
                                    totalAppointments =
                                        doctor.total_appointments,
                                    availability =
                                        doctor.availability ?: "Not specified"
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            SectionTitle(
                                title = "Patient Prediction Insights",
                                subtitle = "Individual readmission risk scores"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            AnalyticsCard {

                                risk.patients.forEachIndexed { index, patient ->

                                    PatientInsightRow(
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

                            Spacer(modifier = Modifier.height(26.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsHeader() {

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
                text = "Analytics",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "AI-driven hospital intelligence",
                color = Color.White.copy(alpha = 0.7f)
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
                                Purple.copy(alpha = 0.18f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color(0xFFC4B5FD)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Decision Intelligence",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "ML risk • workload • resource utilization",
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
private fun RiskDistributionCard(
    modifier: Modifier,
    label: String,
    value: Int,
    total: Int,
    color: Color
) {

    val percentage =
        if (total > 0) {
            (value.toDouble() / total.toDouble()) * 100.0
        } else {
            0.0
        }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun UtilizationCard(
    title: String,
    value: Double,
    subtitle: String
) {

    val progress =
        (value / 100.0)
            .toFloat()
            .coerceIn(0f, 1f)

    val indicatorColor =
        when {
            progress >= 0.85f -> Red
            progress >= 0.65f -> Amber
            else -> Teal
        }

    AnalyticsCard {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        indicatorColor.copy(alpha = 0.10f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = indicatorColor
                )
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = "${value.toInt()}%",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = indicatorColor
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = indicatorColor,
            trackColor = Color(0xFFE9EEF5)
        )
    }
}

@Composable
private fun MiniAnalyticsCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
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
                    .size(40.dp)
                    .background(
                        accent.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DoctorAnalyticsCard(
    name: String,
    specialization: String,
    activeAppointments: Int,
    totalAppointments: Int,
    availability: String
) {

    AnalyticsCard {

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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Teal
                )
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = specialization,
                    color = Teal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Cyan.copy(alpha = 0.10f)
            ) {

                Text(
                    text = "$activeAppointments active",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    color = Color(0xFF168AC0),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = BorderColor
        )

        AnalyticsInfoRow(
            label = "Active appointments",
            value = activeAppointments.toString()
        )

        HorizontalDivider(
            color = BorderColor
        )

        AnalyticsInfoRow(
            label = "Total appointments",
            value = totalAppointments.toString()
        )

        HorizontalDivider(
            color = BorderColor
        )

        AnalyticsInfoRow(
            label = "Availability",
            value = availability
        )
    }
}

@Composable
private fun PatientInsightRow(
    patientId: Int,
    riskLevel: String,
    riskScore: Double
) {

    val color =
        when (riskLevel.uppercase()) {
            "LOW" -> Emerald
            "MEDIUM" -> Amber
            "HIGH" -> Red
            else -> TextSecondary
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    Navy900.copy(alpha = 0.06f),
                    CircleShape
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
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Readmission prediction",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = color.copy(alpha = 0.10f)
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = riskLevel,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${(riskScore * 100).toInt()}%",
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AnalyticsInfoRow(
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

        Spacer(modifier = Modifier.width(14.dp))

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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
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
private fun AnalyticsCard(
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