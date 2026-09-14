package com.mediflowai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun StaffBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    NavigationBar {

        NavigationBarItem(
            selected =
                currentRoute ==
                        Screen.StaffHome.route,

            onClick = {
                onNavigate(
                    Screen.StaffHome.route
                )
            },

            icon = {
                Icon(
                    imageVector =
                        Icons.Outlined.Home,
                    contentDescription =
                        "Home"
                )
            },

            label = {
                Text("Home")
            }
        )


        NavigationBarItem(
            selected =
                currentRoute ==
                        Screen.StaffAppointments.route,

            onClick = {
                onNavigate(
                    Screen.StaffAppointments.route
                )
            },

            icon = {
                Icon(
                    imageVector =
                        Icons.Outlined.CalendarMonth,
                    contentDescription =
                        "Appointments"
                )
            },

            label = {
                Text("Appointments")
            }
        )


        NavigationBarItem(
            selected =
                currentRoute ==
                        Screen.StaffBeds.route,

            onClick = {
                onNavigate(
                    Screen.StaffBeds.route
                )
            },

            icon = {
                Icon(
                    imageVector =
                        Icons.Outlined.Bed,
                    contentDescription =
                        "Beds"
                )
            },

            label = {
                Text("Beds")
            }
        )


        NavigationBarItem(
            selected =
                currentRoute ==
                        Screen.StaffAlerts.route,

            onClick = {
                onNavigate(
                    Screen.StaffAlerts.route
                )
            },

            icon = {
                Icon(
                    imageVector =
                        Icons.Outlined.Notifications,
                    contentDescription =
                        "Alerts"
                )
            },

            label = {
                Text("Alerts")
            }
        )


        NavigationBarItem(
            selected =
                currentRoute ==
                        Screen.StaffProfile.route,

            onClick = {
                onNavigate(
                    Screen.StaffProfile.route
                )
            },

            icon = {
                Icon(
                    imageVector =
                        Icons.Outlined.Person,
                    contentDescription =
                        "Profile"
                )
            },

            label = {
                Text("Profile")
            }
        )
    }
}