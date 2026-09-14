package com.mediflowai.data.model

data class PatientProfile(
    val id: Int,
    val user_id: Int?,
    val name: String,
    val date_of_birth: String?,
    val gender: String?,
    val blood_group: String?,
    val phone: String?,
    val address: String?,
    val medical_information: String?,
    val created_at: String?
)