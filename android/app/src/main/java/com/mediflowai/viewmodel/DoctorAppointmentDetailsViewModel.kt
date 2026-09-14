package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.repository.AppointmentRepository
import com.mediflowai.data.repository.DoctorRepository
import com.mediflowai.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorAppointmentDetailsUiState(

    val isLoading: Boolean = false,


    val isCompleting: Boolean = false,

    val appointment:
    AppointmentItem? = null,

    val patient:
    PatientProfile? = null,

    val doctor:
    DoctorItem? = null,

    val department:
    DepartmentItem? = null,

    val error: String? = null,

)


class DoctorAppointmentDetailsViewModel :
    ViewModel() {


    private val appointmentRepository =
        AppointmentRepository()

    private val patientRepository =
        PatientRepository()

    private val doctorRepository =
        DoctorRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorAppointmentDetailsUiState()
        )


    val uiState:
            StateFlow<DoctorAppointmentDetailsUiState> =
        _uiState.asStateFlow()


    fun loadAppointment(
        appointmentId: Int
    ) {

        viewModelScope.launch {

            _uiState.value =
                DoctorAppointmentDetailsUiState(
                    isLoading = true
                )


            try {

                // Backend verifies that this appointment
                // belongs to the logged-in doctor.
                val appointment =
                    appointmentRepository
                        .getAppointment(
                            appointmentId
                        )


                // Backend independently verifies
                // doctor → patient relationship.
                val patient =
                    patientRepository
                        .getPatient(
                            appointment.patient_id
                        )


                val doctor =
                    doctorRepository
                        .getMyDoctorProfile()


                val departments =
                    doctorRepository
                        .getDepartments()


                val department =
                    departments.firstOrNull {

                        it.id ==
                                appointment.department_id
                    }


                _uiState.value =
                    DoctorAppointmentDetailsUiState(
                        isLoading = false,
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
                    DoctorAppointmentDetailsUiState(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load appointment."
                    )
            }
        }
    }

    fun completeAppointment() {

        val appointment =
            _uiState.value.appointment
                ?: return


        if (
            appointment.status.equals(
                "COMPLETED",
                ignoreCase = true
            ) ||
            appointment.status.equals(
                "CANCELLED",
                ignoreCase = true
            )
        ) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isCompleting = true,
                    error = null
                )


            try {

                val updatedAppointment =
                    appointmentRepository
                        .completeAppointment(
                            appointment.id
                        )


                _uiState.value =
                    _uiState.value.copy(
                        isCompleting = false,
                        appointment =
                            updatedAppointment,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isCompleting = false,
                        error =
                            e.message
                                ?: "Unable to complete appointment."
                    )
            }
        }
    }
}