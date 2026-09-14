package com.mediflowai.data.model

data class AlertItem(
    val id: Int,
    val patient_id: Int?,
    val type: String,
    val message: String,
    val status: String,
    val created_at: String
)