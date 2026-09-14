package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.model.StaffDepartmentSummary
import com.mediflowai.data.repository.AppointmentRepository
import com.mediflowai.data.repository.DoctorRepository
import com.mediflowai.data.repository.PatientRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class StaffAppointmentDetailsUiState(

    val isLoading: Boolean =
        false,

    val appointment:
    AppointmentItem? =
        null,

    val patient:
    PatientProfile? =
        null,

    val doctor:
    DoctorItem? =
        null,

    val department:
    StaffDepartmentSummary? =
        null,

    val error: String? =
        null
)


class StaffAppointmentDetailsViewModel :
    ViewModel() {


    private val appointmentRepository =
        AppointmentRepository()

    private val patientRepository =
        PatientRepository()

    private val doctorRepository =
        DoctorRepository()


    private val _uiState =
        MutableStateFlow(
            StaffAppointmentDetailsUiState()
        )


    val uiState:
            StateFlow<StaffAppointmentDetailsUiState> =
        _uiState.asStateFlow()


    fun loadAppointment(
        appointmentId: Int
    ) {

        viewModelScope.launch {

            _uiState.value =
                StaffAppointmentDetailsUiState(
                    isLoading = true
                )


            try {

                val appointment =
                    appointmentRepository
                        .getAppointment(
                            appointmentId
                        )


                val patientDeferred =
                    async {
                        patientRepository
                            .getPatient(
                                appointment.patient_id
                            )
                    }


                val doctorsDeferred =
                    async {
                        doctorRepository
                            .getDoctors()
                    }


                val departmentsDeferred =
                    async {

                        doctorRepository
                            .getDepartments()
                            .map { department ->

                                StaffDepartmentSummary(
                                    id = department.id,
                                    name = department.name
                                )
                            }
                    }


                val patient =
                    patientDeferred.await()


                val doctor =
                    doctorsDeferred
                        .await()
                        .firstOrNull {
                            it.id ==
                                    appointment.doctor_id
                        }


                val department =
                    departmentsDeferred
                        .await()
                        .firstOrNull {
                            it.id ==
                                    appointment.department_id
                        }


                _uiState.value =
                    StaffAppointmentDetailsUiState(

                        appointment =
                            appointment,

                        patient =
                            patient,

                        doctor =
                            doctor,

                        department =
                            department
                    )


            } catch (e: Exception) {

                _uiState.value =
                    StaffAppointmentDetailsUiState(
                        error =
                            e.message
                                ?: "Unable to load appointment."
                    )
            }
        }
    }
}