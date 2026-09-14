package com.mediflowai.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val DoctorNavNavy =
    Color(0xFF081225)

private val DoctorNavTeal =
    Color(0xFF00A6A6)

private val DoctorNavMuted =
    Color(0xFF7C8799)

private val DoctorNavBackground =
    Color(0xFFFFFFFF)

private val DoctorNavIndicator =
    Color(0xFFE5F7F7)


@Composable
fun DoctorBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    Surface(
        modifier =
            Modifier
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(
                        24.dp
                    ),
                    clip = false
                ),
        shape =
            RoundedCornerShape(
                24.dp
            ),
        color =
            DoctorNavBackground,
        tonalElevation =
            0.dp,
        shadowElevation =
            10.dp
    ) {

        NavigationBar(
            modifier =
                Modifier.height(
                    74.dp
                ),
            containerColor =
                Color.Transparent,
            tonalElevation =
                0.dp
        ) {

            DoctorNavItem(
                selected =
                    currentRoute ==
                            Screen.DoctorHome.route,

                icon =
                    Icons.Outlined.Home,

                label =
                    "Home",

                onClick = {

                    onNavigate(
                        Screen.DoctorHome.route
                    )
                }
            )


            DoctorNavItem(
                selected =
                    currentRoute ==
                            Screen.DoctorAppointments.route,

                icon =
                    Icons.Outlined.CalendarMonth,

                label =
                    "Visits",

                onClick = {

                    onNavigate(
                        Screen.DoctorAppointments.route
                    )
                }
            )


            DoctorNavItem(
                selected =
                    currentRoute ==
                            Screen.DoctorPatients.route,

                icon =
                    Icons.Outlined.People,

                label =
                    "Patients",

                onClick = {

                    onNavigate(
                        Screen.DoctorPatients.route
                    )
                }
            )


            DoctorNavItem(
                selected =
                    currentRoute ==
                            Screen.DoctorAnalytics.route,

                icon =
                    Icons.Outlined.BarChart,

                label =
                    "Insights",

                onClick = {

                    onNavigate(
                        Screen.DoctorAnalytics.route
                    )
                }
            )


            DoctorNavItem(
                selected =
                    currentRoute ==
                            Screen.DoctorProfile.route,

                icon =
                    Icons.Outlined.Person,

                label =
                    "Profile",

                onClick = {

                    onNavigate(
                        Screen.DoctorProfile.route
                    )
                }
            )
        }
    }
}


@Composable
private fun RowScope.DoctorNavItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    NavigationBarItem(
        selected =
            selected,

        onClick =
            onClick,

        alwaysShowLabel =
            true,

        icon = {

            Box(
                modifier =
                    Modifier
                        .background(
                            color =
                                if (selected) {
                                    DoctorNavIndicator
                                } else {
                                    Color.Transparent
                                },
                            shape =
                                RoundedCornerShape(
                                    14.dp
                                )
                        )
                        .padding(
                            horizontal = 11.dp,
                            vertical = 7.dp
                        )
            ) {

                Icon(
                    imageVector =
                        icon,

                    contentDescription =
                        label,

                    tint =
                        if (selected) {
                            DoctorNavTeal
                        } else {
                            DoctorNavMuted
                        }
                )
            }
        },

        label = {

            Text(
                text =
                    label,

                fontSize =
                    11.sp,

                fontWeight =
                    if (selected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
            )
        },

        colors =
            NavigationBarItemDefaults
                .colors(

                    selectedIconColor =
                        DoctorNavTeal,

                    selectedTextColor =
                        DoctorNavNavy,

                    unselectedIconColor =
                        DoctorNavMuted,

                    unselectedTextColor =
                        DoctorNavMuted,

                    indicatorColor =
                        Color.Transparent
                )
    )
}