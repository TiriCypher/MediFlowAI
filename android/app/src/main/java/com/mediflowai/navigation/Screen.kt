package com.mediflowai.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object ForgotPassword : Screen("forgot_password")

    data object Dashboard : Screen("dashboard")

    data object Appointments : Screen("appointments")

    data object Hospital : Screen("hospital")

    data object Analytics : Screen("analytics")

    data object Profile : Screen("profile")

    data object PatientDetails : Screen("patient_details")

    data object DoctorDetails : Screen("doctor_details")

    data object AppointmentDetails : Screen("appointment_details")

    data object Prediction : Screen("prediction")

    data object BedManagement : Screen("bed_management")

    data object Optimization : Screen("optimization")

    data object Alerts : Screen("alerts")

    data object DoctorHome : Screen("doctor_home")

    data object DoctorAppointments :
        Screen("doctor_appointments")

    data object DoctorPatients :
        Screen("doctor_patients")

    data object DoctorAnalytics :
        Screen("doctor_analytics")


    data object DoctorPatientDetails :
        Screen("doctor_patient_details") {

        const val PATIENT_ID =
            "patientId"

        val routeWithId =
            "$route/{$PATIENT_ID}"

        fun createRoute(
            patientId: Int
        ): String {

            return "$route/$patientId"
        }
    }

    data object DoctorReadmissionPrediction :
        Screen("doctor_readmission_prediction") {

        const val PATIENT_ID =
            "patientId"

        val routeWithId =
            "$route/{$PATIENT_ID}"

        fun createRoute(
            patientId: Int
        ): String {

            return "$route/$patientId"
        }
    }

    data object DoctorProfile :
        Screen("doctor_profile")

    data object DoctorAppointmentDetails :
        Screen("doctor_appointment_details") {

        const val APPOINTMENT_ID =
            "appointmentId"

        val routeWithId =
            "$route/{$APPOINTMENT_ID}"

        fun createRoute(
            appointmentId: Int
        ): String {

            return "$route/$appointmentId"
        }
    }

    data object StaffHome : Screen("staff_home")

    data object StaffAppointments :
        Screen("staff_appointments")

    data object StaffBeds :
        Screen("staff_beds")

    data object StaffOptimization :
        Screen("staff_optimization")

    data object StaffAlerts :
        Screen("staff_alerts")

    data object StaffProfile :
        Screen("staff_profile")

    data object StaffAppointmentDetails :
        Screen("staff_appointment_details") {

        const val APPOINTMENT_ID =
            "appointmentId"

        val routeWithId =
            "$route/{$APPOINTMENT_ID}"

        fun createRoute(
            appointmentId: Int
        ): String {

            return "$route/$appointmentId"
        }
    }
    data object PatientHome : Screen("patient_home")

    data object PatientAppointments :
        Screen("patient_appointments")

    data object PatientAppointmentDetails :
        Screen("patient_appointment_details") {

        const val APPOINTMENT_ID =
            "appointmentId"

        val routeWithId =
            "$route/{$APPOINTMENT_ID}"

        fun createRoute(
            appointmentId: Int
        ): String {
            return "$route/$appointmentId"
        }
    }

    data object PatientDoctors :
        Screen("patient_doctors")

    data object PatientAlerts :
        Screen("patient_alerts")

    data object PatientProfile :
        Screen("patient_profile")

    data object PatientBookAppointment :
        Screen("patient_book_appointment") {

        const val DOCTOR_ID =
            "doctorId"

        val routeWithDoctor =
            "$route?doctorId={$DOCTOR_ID}"

        fun createRoute(
            doctorId: Int
        ): String {
            return "$route?doctorId=$doctorId"
        }
    }


}