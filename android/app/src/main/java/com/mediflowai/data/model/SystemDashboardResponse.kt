package com.mediflowai.data.model

data class SystemDashboardResponse(
    val resources: ResourceSummary,
    val appointments: AppointmentAnalytics,
    val doctor_workload: DoctorWorkloadSummary,
    val patient_risk: PatientRiskSummary
)

data class ResourceSummary(
    val bed_summary: BedSummary,
    val department_summary: List<DepartmentBedSummary>,
    val bed_type_summary: List<BedTypeSummary>
)

data class BedSummary(
    val total_beds: Int,
    val available_beds: Int,
    val occupied_beds: Int,
    val total_icu_beds: Int,
    val available_icu_beds: Int,
    val total_general_beds: Int,
    val available_general_beds: Int,
    val occupancy_percentage: Double
)

data class DepartmentBedSummary(
    val department_id: Int,
    val department_name: String,
    val total_beds: Int,
    val available_beds: Int,
    val occupied_beds: Int
)

data class BedTypeSummary(
    val bed_type: String,
    val total_beds: Int,
    val available_beds: Int,
    val occupied_beds: Int
)

data class AppointmentAnalytics(
    val total_appointments: Int,
    val status_summary: List<AppointmentStatus>,
    val doctor_workload: List<DoctorAppointmentWorkload>
)

data class AppointmentStatus(
    val status: String,
    val count: Int
)

data class DoctorAppointmentWorkload(
    val doctor_id: Int,
    val doctor_name: String,
    val specialization: String?,
    val active_appointments: Int,
    val total_appointments: Int
)

data class DoctorWorkloadSummary(
    val total_doctors: Int,
    val least_loaded_doctor_id: Int?,
    val most_loaded_doctor_id: Int?,
    val doctors: List<DoctorWorkloadItem>
)

data class DoctorWorkloadItem(
    val doctor_id: Int,
    val doctor_name: String,
    val specialization: String?,
    val availability: String?,
    val active_appointments: Int,
    val total_appointments: Int
)

data class PatientRiskSummary(
    val total_patients: Int,
    val low_risk: Int,
    val medium_risk: Int,
    val high_risk: Int,
    val patients: List<PatientRiskItem>
)

data class PatientRiskItem(
    val patient_id: Int,
    val risk_level: String,
    val risk_score: Double
)