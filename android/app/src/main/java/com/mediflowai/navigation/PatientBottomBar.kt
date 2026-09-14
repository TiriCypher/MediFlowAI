package com.mediflowai.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PatientNavy = Color(0xFF081225)
private val PatientTeal = Color(0xFF00A6A6)
private val PatientMuted = Color(0xFF94A3B8)

private data class PatientNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

private val patientItems = listOf(

    PatientNavItem(
        "Home",
        Screen.PatientHome.route,
        Icons.Default.Home
    ),

    PatientNavItem(
        "Visits",
        Screen.PatientAppointments.route,
        Icons.Default.CalendarMonth
    ),

    PatientNavItem(
        "Doctors",
        Screen.PatientDoctors.route,
        Icons.Default.MedicalServices
    ),

    PatientNavItem(
        "Alerts",
        Screen.PatientAlerts.route,
        Icons.Default.Notifications
    ),

    PatientNavItem(
        "Profile",
        Screen.PatientProfile.route,
        Icons.Default.Person
    )
)


@Composable
fun PatientBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(
                horizontal = 8.dp,
                vertical = 10.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceEvenly,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        patientItems.forEach { item ->

            val selected =
                currentRoute == item.route

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        if (selected)
                            PatientNavy
                        else
                            Color.Transparent
                    )
                    .clickable {
                        onNavigate(item.route)
                    }
                    .padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                androidx.compose.foundation.layout.Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = item.icon,
                        contentDescription =
                            item.label,
                        tint =
                            if (selected)
                                PatientTeal
                            else
                                PatientMuted,
                        modifier =
                            Modifier.size(21.dp)
                    )

                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight =
                            if (selected)
                                FontWeight.SemiBold
                            else
                                FontWeight.Normal,
                        color =
                            if (selected)
                                Color.White
                            else
                                PatientMuted
                    )
                }
            }
        }
    }
}