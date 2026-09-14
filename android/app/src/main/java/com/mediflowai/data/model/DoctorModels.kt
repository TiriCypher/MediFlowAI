package com.mediflowai.data.model

data class DoctorItem(
    val id: Int,
    val user_id: Int,
    val name: String,
    val department_id: Int?,
    val department_name: String?,
    val specialization: String?,
    val availability: String?
)