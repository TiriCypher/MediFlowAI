package com.mediflowai.data.model


data class DoctorAnalyticsResponse(

    val doctor_id: Int,

    val doctor_name: String,

    val specialization: String?,

    val assigned_patients: Int,

    val appointments:
    DoctorAppointmentAnalytics,

    val patient_risk:
    DoctorRiskAnalytics
)


data class DoctorAppointmentAnalytics(

    val total: Int,

    val scheduled: Int,

    val completed: Int,

    val cancelled: Int,

    val completion_rate: Double
)


data class DoctorRiskAnalytics(

    val total_predictions: Int,

    val low_risk: Int,

    val medium_risk: Int,

    val high_risk: Int
)