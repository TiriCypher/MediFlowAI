package com.mediflowai.navigation


fun homeRouteForRole(role: String): String {

    return when (role.uppercase()) {

        "ADMIN" ->
            Screen.Dashboard.route

        "DOCTOR" ->
            Screen.DoctorHome.route

        "STAFF" ->
            Screen.StaffHome.route

        "PATIENT" ->
            Screen.PatientHome.route

        else ->
            Screen.PatientHome.route
    }
}