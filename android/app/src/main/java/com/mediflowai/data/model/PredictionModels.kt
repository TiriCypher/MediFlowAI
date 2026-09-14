package com.mediflowai.data.model


data class ReadmissionPredictionRequest(

    val los_days: Int,

    val admit_type: String,

    val ward_type: String,

    val num_procedures: Int,

    val charlson_index: Int,

    val hba1c: Double,

    val creatinine: Double,

    val haemoglobin: Double,

    val systolic_bp: Int,

    val age: Int,

    val gender: String,

    val state: String,

    val bpl_card: Boolean,

    val insurance_type: String?,

    val comorbidity_count: Int,

    val prev_admissions: Int,

    val hospital_state: String,

    val tier: String,

    val beds: Int,

    val teaching: Boolean,

    val diagnosis_count: Int,

    val primary_diagnosis_category: String,

    val admission_month: Int
)


data class PatientReadmissionPredictionResponse(

    val prediction_id: Int,

    val patient_id: Int,

    val prediction: Int,

    val readmission_probability: Double,

    val risk_category: String,

    val model_version: String,

    val created_at: String
)


data class MLPredictionItem(

    val id: Int,

    val patient_id: Int,

    val risk_score: Double,

    val risk_level: String,

    val model_version: String?,

    val created_at: String
)