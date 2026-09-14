package com.mediflowai.data.remote

import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.model.SystemDashboardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.AppointmentCreateRequest
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.model.AlertItem
import retrofit2.http.Path
import com.mediflowai.data.model.MLPredictionItem
import com.mediflowai.data.model.PatientReadmissionPredictionResponse
import com.mediflowai.data.model.ReadmissionPredictionRequest
import com.mediflowai.data.model.DoctorAnalyticsResponse
import com.mediflowai.data.model.StaffDashboardResponse

data class HealthResponse(
    val status: String
)


data class UserSyncRequest(
    val name: String,
    val email: String
)


data class BackendUser(
    val id: Int,
    val firebase_uid: String,
    val name: String,
    val email: String,
    val role: String,
    val created_at: String
)

data class PatientUpdateRequest(
    val name: String?,
    val phone: String?,
    val address: String?,
    val medical_information: String?
)


interface ApiService {

    @GET("health")
    suspend fun getHealth(): HealthResponse


    @GET("optimization/system-dashboard")
    suspend fun getSystemDashboard(): SystemDashboardResponse

    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentItem>


    @GET("appointments/{appointmentId}")
    suspend fun getAppointment(
        @Path("appointmentId")
        appointmentId: Int
    ): AppointmentItem

    @PATCH("appointments/{appointmentId}/cancel")
    suspend fun cancelAppointment(
        @Path("appointmentId")
        appointmentId: Int
    ): AppointmentItem

    @PATCH("appointments/{appointmentId}/complete")
    suspend fun completeAppointment(
        @Path("appointmentId")
        appointmentId: Int
    ): AppointmentItem


    @GET("alerts")
    suspend fun getAlerts(): List<AlertItem>

    @GET("doctors")
    suspend fun getDoctors(): List<DoctorItem>


    @GET("doctors/me")
    suspend fun getMyDoctorProfile(): DoctorItem

    @GET("departments")
    suspend fun getDepartments(): List<DepartmentItem>

    @POST("appointments")
    suspend fun createAppointment(
        @Body request: AppointmentCreateRequest
    ): AppointmentItem

    @POST("users/sync")
    suspend fun syncUser(
        @Body request: UserSyncRequest
    ): BackendUser


    @PATCH("alerts/{alertId}/read")
    suspend fun markAlertAsRead(
        @Path("alertId") alertId: Int
    ): AlertItem

    @GET("users/me")
    suspend fun getCurrentUser(): BackendUser


    // ---------------------------------------------
    // PATIENT
    // ---------------------------------------------

    @GET("patients/me")
    suspend fun getMyPatientProfile(): PatientProfile

    @PATCH("patients/me")
    suspend fun updateMyPatientProfile(
        @Body request: PatientUpdateRequest
    ): PatientProfile

    @GET("patients/{patientId}")
    suspend fun getPatient(
        @Path("patientId")
        patientId: Int
    ): PatientProfile

    @GET("patients")
    suspend fun getPatients():
            List<PatientProfile>



    // ---------------------------------------------
// ML PREDICTIONS
// ---------------------------------------------

    @POST(
        "predictions/patients/{patientId}/readmission"
    )
    suspend fun predictPatientReadmission(

        @Path("patientId")
        patientId: Int,

        @Body
        request: ReadmissionPredictionRequest

    ): PatientReadmissionPredictionResponse


    @GET(
        "predictions/patients/{patientId}"
    )
    suspend fun getPatientPredictionHistory(

        @Path("patientId")
        patientId: Int

    ): List<MLPredictionItem>

    // ---------------------------------------------
// DOCTOR ANALYTICS
// ---------------------------------------------

    @GET("analytics/doctor/me")
    suspend fun getDoctorAnalytics():
            DoctorAnalyticsResponse

    // ---------------------------------------------
// STAFF DASHBOARD
// ---------------------------------------------

    @GET("optimization/staff-dashboard")
    suspend fun getStaffDashboard():
            StaffDashboardResponse
}