package com.mediflowai.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mediflowai.ui.analytics.AnalyticsScreen
import com.mediflowai.ui.appointments.AppointmentsScreen
import com.mediflowai.ui.appointments.PatientAppointmentsScreen
import com.mediflowai.ui.auth.ForgotPasswordScreen
import com.mediflowai.ui.auth.LoginScreen
import com.mediflowai.ui.auth.RegisterScreen
import com.mediflowai.ui.dashboard.DashboardScreen
import com.mediflowai.ui.dashboard.DoctorHomeScreen
import com.mediflowai.ui.dashboard.PatientHomeScreen
import com.mediflowai.ui.dashboard.PatientSectionScreen
import com.mediflowai.ui.dashboard.StaffHomeScreen
import com.mediflowai.ui.hospital.HospitalScreen
import com.mediflowai.ui.profile.PatientProfileScreen
import com.mediflowai.ui.profile.ProfileScreen
import com.mediflowai.viewmodel.AuthViewModel
import com.mediflowai.ui.appointments.PatientBookAppointmentScreen
import com.mediflowai.viewmodel.PatientViewModel
import com.mediflowai.ui.hospital.PatientDoctorsScreen
import com.mediflowai.ui.hospital.PatientAlertsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mediflowai.ui.appointments.PatientAppointmentDetailsScreen
import com.mediflowai.ui.appointments.DoctorAppointmentsScreen
import com.mediflowai.ui.appointments.DoctorAppointmentDetailsScreen
import com.mediflowai.ui.hospital.DoctorPatientsScreen
import com.mediflowai.ui.hospital.DoctorPatientDetailsScreen
import com.mediflowai.ui.analytics.DoctorReadmissionPredictionScreen
import com.mediflowai.ui.profile.DoctorProfileScreen
import com.mediflowai.ui.analytics.DoctorAnalyticsScreen
import com.mediflowai.ui.appointments.StaffAppointmentsScreen
import com.mediflowai.ui.appointments.StaffAppointmentDetailsScreen

@Composable
fun MediFlowNavGraph() {

    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()

    val backendUser by
    authViewModel.backendUser.collectAsState()


    fun navigateToRoleHome(
        role: String
    ) {

        val destination =
            homeRouteForRole(role)

        navController.navigate(
            destination
        ) {

            popUpTo(0)

            launchSingleTop = true
        }
    }


    fun loadUserAndNavigate() {

        authViewModel.loadBackendUser(

            onSuccess = { user ->

                navigateToRoleHome(
                    user.role
                )
            },

            onError = {

                authViewModel.syncBackendUser(

                    onSuccess = { user ->

                        navigateToRoleHome(
                            user.role
                        )
                    }
                )
            }
        )
    }


    // ------------------------------------------------
    // ADMIN NAVIGATION
    // ------------------------------------------------

    fun navigateMainTab(
        route: String
    ) {

        if (
            route ==
            Screen.Dashboard.route
        ) {

            navController.popBackStack(
                route =
                    Screen.Dashboard.route,
                inclusive = false
            )

        } else {

            navController.navigate(route) {

                popUpTo(
                    Screen.Dashboard.route
                ) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }
        }
    }


    // ------------------------------------------------
    // PATIENT NAVIGATION
    // ------------------------------------------------

    fun navigatePatientTab(
        route: String
    ) {

        if (
            route ==
            Screen.PatientHome.route
        ) {

            navController.popBackStack(
                route =
                    Screen.PatientHome.route,
                inclusive = false
            )

        } else {

            navController.navigate(route) {

                popUpTo(
                    Screen.PatientHome.route
                ) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateStaffTab(
        route: String
    ) {

        navController.navigate(
            route
        ) {

            popUpTo(
                Screen.StaffHome.route
            ) {
                saveState =
                    true
            }

            launchSingleTop =
                true

            restoreState =
                true
        }
    }

    // ------------------------------------------------
// DOCTOR NAVIGATION
// ------------------------------------------------

    fun navigateDoctorTab(
        route: String
    ) {

        if (
            route ==
            Screen.DoctorHome.route
        ) {

            navController.popBackStack(
                route =
                    Screen.DoctorHome.route,
                inclusive = false
            )

        } else {

            navController.navigate(
                route
            ) {

                popUpTo(
                    Screen.DoctorHome.route
                ) {
                    saveState = true
                }

                launchSingleTop =
                    true

                restoreState =
                    true
            }
        }
    }


    val startDestination =
        if (
            authViewModel.isUserLoggedIn()
        ) {
            Screen.Splash.route
        } else {
            Screen.Login.route
        }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ------------------------------------------------
        // SPLASH
        // ------------------------------------------------

        composable(
            Screen.Splash.route
        ) {

            LaunchedEffect(Unit) {

                loadUserAndNavigate()
            }

            Box(
                modifier =
                    Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }


        // ------------------------------------------------
        // LOGIN
        // ------------------------------------------------

        composable(
            Screen.Login.route
        ) {

            LoginScreen(
                viewModel =
                    authViewModel,

                onLoginSuccess = {

                    loadUserAndNavigate()
                },

                onRegisterClick = {

                    navController.navigate(
                        Screen.Register.route
                    )
                },

                onForgotPasswordClick = {

                    navController.navigate(
                        Screen.ForgotPassword.route
                    )
                }
            )
        }


        // ------------------------------------------------
        // REGISTER
        // ------------------------------------------------

        composable(
            Screen.Register.route
        ) {

            RegisterScreen(
                viewModel =
                    authViewModel,

                onRegisterSuccess = {

                    authViewModel
                        .syncBackendUser(

                            onSuccess = { user ->

                                navigateToRoleHome(
                                    user.role
                                )
                            }
                        )
                },

                onLoginClick = {

                    navController
                        .popBackStack()
                }
            )
        }


        // ------------------------------------------------
        // FORGOT PASSWORD
        // ------------------------------------------------

        composable(
            Screen.ForgotPassword.route
        ) {

            ForgotPasswordScreen(
                viewModel =
                    authViewModel,

                onBackToLogin = {

                    navController
                        .popBackStack()
                }
            )
        }


        // ------------------------------------------------
        // ADMIN DASHBOARD
        // ------------------------------------------------

        composable(
            Screen.Dashboard.route
        ) {

            DashboardScreen(
                viewModel =
                    authViewModel,

                onNavigate = { route ->

                    navigateMainTab(
                        route
                    )
                },

                onLogout = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(0)
                    }
                }
            )
        }


        // ------------------------------------------------
        // PATIENT HOME
        // ------------------------------------------------

        composable(
            Screen.PatientHome.route
        ) {

            PatientHomeScreen(
                email =
                    backendUser?.email,

                onNavigate = { route ->

                    navigatePatientTab(
                        route
                    )
                }
            )
        }


        // ------------------------------------------------
        // PATIENT APPOINTMENTS
        // ------------------------------------------------

        composable(
            Screen.PatientAppointments.route
        ) {

            PatientAppointmentsScreen(

                onNavigate = { route ->

                    navigatePatientTab(
                        route
                    )
                },

                onAppointmentClick = {
                        appointmentId ->

                    navController.navigate(
                        Screen.PatientAppointmentDetails
                            .createRoute(
                                appointmentId
                            )
                    )
                }
            )
        }

        composable(
            route =
                Screen.PatientAppointmentDetails
                    .routeWithId,

            arguments =
                listOf(
                    navArgument(
                        Screen.PatientAppointmentDetails
                            .APPOINTMENT_ID
                    ) {

                        type =
                            NavType.IntType
                    }
                )
        ) { backStackEntry ->

            val appointmentId =
                backStackEntry.arguments
                    ?.getInt(
                        Screen.PatientAppointmentDetails
                            .APPOINTMENT_ID
                    )


            if (appointmentId != null) {

                PatientAppointmentDetailsScreen(
                    appointmentId =
                        appointmentId,

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }
        }


        composable(
            route =
                Screen.PatientBookAppointment
                    .routeWithDoctor,

            arguments =
                listOf(
                    navArgument(
                        Screen.PatientBookAppointment
                            .DOCTOR_ID
                    ) {

                        type =
                            NavType.IntType

                        defaultValue =
                            -1
                    }
                )
        ) { backStackEntry ->

            val patientViewModel:
                    PatientViewModel = viewModel()

            val patientState by
            patientViewModel.uiState
                .collectAsState()


            val patientId =
                patientState.patient?.id


            val doctorId =
                backStackEntry.arguments
                    ?.getInt(
                        Screen.PatientBookAppointment
                            .DOCTOR_ID
                    )
                    ?.takeIf {
                        it > 0
                    }


            if (patientId != null) {

                PatientBookAppointmentScreen(
                    patientId =
                        patientId,

                    preselectedDoctorId =
                        doctorId,

                    onBooked = {

                        navController.navigate(
                            Screen.PatientAppointments.route
                        ) {

                            popUpTo(
                                Screen.PatientHome.route
                            )

                            launchSingleTop =
                                true
                        }
                    },

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )

            } else {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }
        }

        // ------------------------------------------------
        // PATIENT DOCTORS
        // ------------------------------------------------

        composable(
            Screen.PatientDoctors.route
        ) {

            PatientDoctorsScreen(

                onNavigate = { route ->

                    navigatePatientTab(
                        route
                    )
                },

                onBookDoctor = { doctorId ->

                    navController.navigate(
                        Screen.PatientBookAppointment
                            .createRoute(
                                doctorId
                            )
                    )
                }
            )
        }


        // ------------------------------------------------
        // PATIENT ALERTS
        // ------------------------------------------------

        composable(
            Screen.PatientAlerts.route
        ) {

            PatientAlertsScreen(
                onNavigate = { route ->

                    navigatePatientTab(
                        route
                    )
                }
            )
        }


        // ------------------------------------------------
        // PATIENT PROFILE
        // ------------------------------------------------

        composable(
            Screen.PatientProfile.route
        ) {

            PatientProfileScreen(

                authViewModel =
                    authViewModel,

                onNavigate = { route ->

                    navigatePatientTab(
                        route
                    )
                },

                onLogout = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(0)

                        launchSingleTop = true
                    }
                }
            )
        }


        // ------------------------------------------------
        // DOCTOR HOME
        // ------------------------------------------------

        composable(
            Screen.DoctorHome.route
        ) {

            DoctorHomeScreen(
                email =
                    backendUser?.email,

                onNavigate = { route ->

                    navigateDoctorTab(
                        route
                    )
                }
            )
        }

        // ------------------------------------------------
// DOCTOR APPOINTMENTS
// ------------------------------------------------

        composable(
            Screen.DoctorAppointments.route
        ) {

            DoctorAppointmentsScreen(

                onNavigate = { route ->

                    navigateDoctorTab(
                        route
                    )
                },

                onAppointmentClick = {
                        appointmentId ->

                    navController.navigate(
                        Screen.DoctorAppointmentDetails
                            .createRoute(
                                appointmentId
                            )
                    )
                }
            )
        }

        // ------------------------------------------------
// DOCTOR PATIENTS
// ------------------------------------------------

        composable(
            Screen.DoctorPatients.route
        ) {

            DoctorPatientsScreen(

                onNavigate = { route ->

                    navigateDoctorTab(
                        route
                    )
                },

                onPatientClick = {
                        patientId ->

                    navController.navigate(
                        Screen.DoctorPatientDetails
                            .createRoute(
                                patientId
                            )
                    )
                }
            )
        }

        // ------------------------------------------------
// DOCTOR PATIENT DETAILS
// ------------------------------------------------

        composable(
            route =
                Screen.DoctorPatientDetails
                    .routeWithId,

            arguments =
                listOf(
                    navArgument(
                        Screen.DoctorPatientDetails
                            .PATIENT_ID
                    ) {

                        type =
                            NavType.IntType
                    }
                )
        ) { backStackEntry ->


            val patientId =
                backStackEntry.arguments
                    ?.getInt(
                        Screen.DoctorPatientDetails
                            .PATIENT_ID
                    )


            if (patientId != null) {

                DoctorPatientDetailsScreen(

                    patientId =
                        patientId,

                    onBack = {

                        navController
                            .popBackStack()
                    },

                    onRiskPrediction = {
                            selectedPatientId ->

                        navController.navigate(
                            Screen.DoctorReadmissionPrediction
                                .createRoute(
                                    selectedPatientId
                                )
                        )
                    }
                )
            }
        }

        // ------------------------------------------------
// DOCTOR APPOINTMENT DETAILS
// ------------------------------------------------

        composable(
            route =
                Screen.DoctorAppointmentDetails
                    .routeWithId,

            arguments =
                listOf(
                    navArgument(
                        Screen.DoctorAppointmentDetails
                            .APPOINTMENT_ID
                    ) {

                        type =
                            NavType.IntType
                    }
                )
        ) { backStackEntry ->


            val appointmentId =
                backStackEntry.arguments
                    ?.getInt(
                        Screen.DoctorAppointmentDetails
                            .APPOINTMENT_ID
                    )


            if (appointmentId != null) {

                DoctorAppointmentDetailsScreen(

                    appointmentId =
                        appointmentId,

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }
        }

        // ------------------------------------------------
// DOCTOR ANALYTICS
// ------------------------------------------------

        composable(
            Screen.DoctorAnalytics.route
        ) {

            DoctorAnalyticsScreen(

                onNavigate = { route ->

                    navigateDoctorTab(
                        route
                    )
                }
            )
        }

        // ------------------------------------------------
// DOCTOR PROFILE
// ------------------------------------------------

        composable(
            Screen.DoctorProfile.route
        ) {

            DoctorProfileScreen(

                onNavigate = { route ->

                    navigateDoctorTab(
                        route
                    )
                },

                onLogout = {

                    authViewModel.logout()

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(0)

                        launchSingleTop =
                            true
                    }
                }
            )
        }


        // ------------------------------------------------
        // STAFF HOME
        // ------------------------------------------------

        composable(
            Screen.StaffHome.route
        ) {

            StaffHomeScreen(

                onNavigate = { route ->

                    navigateStaffTab(
                        route
                    )
                }
            )
        }

        composable(
            Screen.StaffAppointments.route
        ) {

            StaffAppointmentsScreen(

                onNavigate = { route ->

                    navigateStaffTab(
                        route
                    )
                },

                onAppointmentClick = {
                        appointmentId ->

                    navController.navigate(
                        Screen
                            .StaffAppointmentDetails
                            .createRoute(
                                appointmentId
                            )
                    )
                }
            )
        }

        composable(

            route =
                Screen
                    .StaffAppointmentDetails
                    .routeWithId,

            arguments =
                listOf(

                    navArgument(
                        Screen
                            .StaffAppointmentDetails
                            .APPOINTMENT_ID
                    ) {

                        type =
                            NavType.IntType
                    }
                )

        ) { backStackEntry ->


            val appointmentId =
                backStackEntry
                    .arguments
                    ?.getInt(
                        Screen
                            .StaffAppointmentDetails
                            .APPOINTMENT_ID
                    )


            if (
                appointmentId != null
            ) {

                StaffAppointmentDetailsScreen(

                    appointmentId =
                        appointmentId,

                    onBack = {
                        navController
                            .popBackStack()
                    }
                )
            }
        }


        // ------------------------------------------------
        // ADMIN APPOINTMENTS
        // ------------------------------------------------

        composable(
            Screen.Appointments.route
        ) {

            AppointmentsScreen(

                onNavigate = { route ->

                    navigateMainTab(
                        route
                    )
                }
            )
        }


        // ------------------------------------------------
        // ADMIN HOSPITAL
        // ------------------------------------------------

        composable(
            Screen.Hospital.route
        ) {

            HospitalScreen(

                onNavigate = { route ->

                    navigateMainTab(
                        route
                    )
                }
            )
        }


        // ------------------------------------------------
        // ADMIN ANALYTICS
        // ------------------------------------------------

        composable(
            Screen.Analytics.route
        ) {

            AnalyticsScreen(

                onNavigate = { route ->

                    navigateMainTab(
                        route
                    )
                }
            )
        }


        // ------------------------------------------------
        // ADMIN PROFILE
        // ------------------------------------------------

        composable(
            Screen.Profile.route
        ) {

            ProfileScreen(
                authViewModel =
                    authViewModel,

                onNavigate = { route ->

                    navigateMainTab(
                        route
                    )
                },

                onLogout = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(0)
                    }
                }
            )
        }

        // ------------------------------------------------
// DOCTOR READMISSION PREDICTION
// ------------------------------------------------

        composable(

            route =
                Screen.DoctorReadmissionPrediction
                    .routeWithId,

            arguments =
                listOf(
                    navArgument(
                        Screen.DoctorReadmissionPrediction
                            .PATIENT_ID
                    ) {

                        type =
                            NavType.IntType
                    }
                )

        ) { backStackEntry ->


            val patientId =
                backStackEntry.arguments
                    ?.getInt(
                        Screen.DoctorReadmissionPrediction
                            .PATIENT_ID
                    )


            if (patientId != null) {

                DoctorReadmissionPredictionScreen(

                    patientId =
                        patientId,

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }
        }
    }
}