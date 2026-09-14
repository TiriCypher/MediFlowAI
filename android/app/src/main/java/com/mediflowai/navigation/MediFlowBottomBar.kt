package com.mediflowai.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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

private val Navy = Color(0xFF081225)
private val Teal = Color(0xFF00A6A6)
private val Cyan = Color(0xFF38BDF8)
private val Muted = Color(0xFF98A2B3)
private val BarBackground = Color(0xFFFFFFFF)

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomNavItem(
        label = "Dashboard",
        route = Screen.Dashboard.route,
        icon = Icons.Default.Dashboard
    ),
    BottomNavItem(
        label = "Appointments",
        route = Screen.Appointments.route,
        icon = Icons.Default.CalendarMonth
    ),
    BottomNavItem(
        label = "Hospital",
        route = Screen.Hospital.route,
        icon = Icons.Default.LocalHospital
    ),
    BottomNavItem(
        label = "Analytics",
        route = Screen.Analytics.route,
        icon = Icons.Default.Analytics
    ),
    BottomNavItem(
        label = "Profile",
        route = Screen.Profile.route,
        icon = Icons.Default.Person
    )
)

@Composable
fun MediFlowBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    Surface(
        color = BarBackground,
        shadowElevation = 14.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            bottomItems.forEach { item ->

                val selected =
                    currentRoute == item.route

                BottomBarItem(
                    item = item,
                    selected = selected,
                    onClick = {
                        onNavigate(item.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected)
            Navy
        else
            Color.Transparent

    val iconColor =
        if (selected)
            Cyan
        else
            Muted

    Box(
        modifier = Modifier
            .animateContentSize()
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(backgroundColor)
            .clickable {
                onClick()
            }
            .padding(
                horizontal =
                    if (selected) 13.dp
                    else 10.dp,
                vertical = 11.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(21.dp)
            )

            if (selected) {

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = item.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}