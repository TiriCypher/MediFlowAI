package com.mediflowai.ui.hospital

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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
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

private val TextPrimary = Color(0xFF101828)
private val TextSecondary = Color(0xFF667085)
private val BorderColor = Color(0xFFE4EAF2)

@Composable
fun HospitalScreen(
    onNavigate: (String) -> Unit
) {

    val viewModel: DashboardViewModel = viewModel()

    val data by viewModel.dashboardData.collectAsState()
    val error by viewModel.dashboardError.collectAsState()

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            MediFlowBottomBar(
                currentRoute = Screen.Hospital.route,
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

            HospitalHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                when {

                    error != null -> {

                        HospitalCard {

                            Text(
                                text = "Unable to load hospital data",
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

                            val beds =
                                dashboard.resources.bed_summary

                            SectionTitle(
                                title = "Capacity Overview",
                                subtitle = "Live hospital resource availability"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OccupancyCard(
                                occupancy = beds.occupancy_percentage,
                                occupied = beds.occupied_beds,
                                available = beds.available_beds,
                                total = beds.total_beds
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                ResourceMetricCard(
                                    modifier = Modifier.weight(1f),
                                    title = "ICU Beds",
                                    available = beds.available_icu_beds,
                                    total = beds.total_icu_beds,
                                    icon = Icons.Default.MonitorHeart,
                                    accent = Red
                                )

                                ResourceMetricCard(
                                    modifier = Modifier.weight(1f),
                                    title = "General",
                                    available = beds.available_general_beds,
                                    total = beds.total_general_beds,
                                    icon = Icons.Default.LocalHospital,
                                    accent = Teal
                                )
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            SectionTitle(
                                title = "Departments",
                                subtitle = "Bed capacity by clinical department"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            dashboard.resources.department_summary
                                .forEach { department ->

                                    DepartmentCard(
                                        name = department.department_name,
                                        total = department.total_beds,
                                        available = department.available_beds,
                                        occupied = department.occupied_beds
                                    )

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )
                                }

                            Spacer(modifier = Modifier.height(18.dp))

                            SectionTitle(
                                title = "Bed Categories",
                                subtitle = "Availability by care type"
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            dashboard.resources.bed_type_summary
                                .forEach { bedType ->

                                    BedTypeCard(
                                        type = bedType.bed_type,
                                        total = bedType.total_beds,
                                        available = bedType.available_beds,
                                        occupied = bedType.occupied_beds
                                    )

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )
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
private fun HospitalHeader() {

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
                text = "Hospital",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Resources, beds & clinical capacity",
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
                                Cyan.copy(alpha = 0.14f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Bed,
                            contentDescription = null,
                            tint = Cyan
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Resource Intelligence",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Live capacity and bed monitoring",
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
private fun OccupancyCard(
    occupancy: Double,
    occupied: Int,
    available: Int,
    total: Int
) {

    val progress =
        (occupancy / 100.0)
            .toFloat()
            .coerceIn(0f, 1f)

    val occupancyColor =
        when {
            progress >= 0.85f -> Red
            progress >= 0.65f -> Amber
            else -> Teal
        }

    HospitalCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Bed Occupancy",
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${occupancy.toInt()}%",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = occupancyColor.copy(alpha = 0.1f)
            ) {

                Text(
                    text = "$available available",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    color = occupancyColor,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = occupancyColor,
            trackColor = Color(0xFFE9EEF5)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            CapacityLegend(
                value = occupied,
                label = "Occupied"
            )

            CapacityLegend(
                value = available,
                label = "Available"
            )

            CapacityLegend(
                value = total,
                label = "Total"
            )
        }
    }
}

@Composable
private fun ResourceMetricCard(
    modifier: Modifier,
    title: String,
    available: Int,
    total: Int,
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
                        accent.copy(alpha = 0.1f),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "$available / $total",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = title,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Available",
                color = accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DepartmentCard(
    name: String,
    total: Int,
    available: Int,
    occupied: Int
) {

    HospitalCard {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Cyan.copy(alpha = 0.1f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Color(0xFF168AC0)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$available beds currently available",
                    color = Teal,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = BorderColor)

        ResourceRow(
            label = "Total beds",
            value = total.toString()
        )

        HorizontalDivider(color = BorderColor)

        ResourceRow(
            label = "Available",
            value = available.toString()
        )

        HorizontalDivider(color = BorderColor)

        ResourceRow(
            label = "Occupied",
            value = occupied.toString()
        )
    }
}

@Composable
private fun BedTypeCard(
    type: String,
    total: Int,
    available: Int,
    occupied: Int
) {

    HospitalCard {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Teal.copy(alpha = 0.1f),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Bed,
                    contentDescription = null,
                    tint = Teal
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = type,
                modifier = Modifier.weight(1f),
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Emerald.copy(alpha = 0.1f)
            ) {

                Text(
                    text = "$available available",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    color = Emerald,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ResourceRow(
            label = "Total",
            value = total.toString()
        )

        HorizontalDivider(color = BorderColor)

        ResourceRow(
            label = "Occupied",
            value = occupied.toString()
        )
    }
}

@Composable
private fun ResourceRow(
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
private fun CapacityLegend(
    value: Int,
    label: String
) {

    Column {

        Text(
            text = value.toString(),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
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
private fun HospitalCard(
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