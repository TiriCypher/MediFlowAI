package com.mediflowai.data.model

data class AppointmentItem(
    val id: Int,
    val patient_id: Int,
    val doctor_id: Int,
    val department_id: Int?,
    val appointment_time: String,
    val reason: String?,
    val priority: Int,
    val status: String,
    val created_at: String?
)

data class AppointmentCreateRequest(
    val patient_id: Int,
    val doctor_id: Int,
    val department_id: Int?,
    val appointment_time: String,
    val priority: Int = 3,
    val status: String = "SCHEDULED",
    val reason: String?
)