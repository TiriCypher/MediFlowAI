package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class PatientAppointmentDetailsUiState(
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val appointment: AppointmentItem? = null,
    val doctor: DoctorItem? = null,
    val department: DepartmentItem? = null,
    val error: String? = null
)


class PatientAppointmentDetailsViewModel :
    ViewModel() {

    private val repository =
        AppointmentRepository()


    private val _uiState =
        MutableStateFlow(
            PatientAppointmentDetailsUiState()
        )


    val uiState:
            StateFlow<PatientAppointmentDetailsUiState> =
        _uiState.asStateFlow()


    fun loadAppointment(
        appointmentId: Int
    ) {

        viewModelScope.launch {

            _uiState.value =
                PatientAppointmentDetailsUiState(
                    isLoading = true
                )


            try {

                val appointment =
                    repository.getAppointment(
                        appointmentId
                    )


                val doctors =
                    repository.getDoctors()


                val departments =
                    repository.getDepartments()


                val doctor =
                    doctors.firstOrNull {
                        it.id ==
                                appointment.doctor_id
                    }


                val department =
                    departments.firstOrNull {
                        it.id ==
                                appointment.department_id
                    }


                _uiState.value =
                    PatientAppointmentDetailsUiState(
                        appointment =
                            appointment,
                        doctor =
                            doctor,
                        department =
                            department
                    )

            } catch (e: Exception) {

                _uiState.value =
                    PatientAppointmentDetailsUiState(
                        error =
                            e.message
                                ?: "Unable to load appointment."
                    )
            }
        }
    }

    fun cancelAppointment() {

        val appointment =
            _uiState.value.appointment
                ?: return


        if (
            appointment.status.equals(
                "CANCELLED",
                ignoreCase = true
            ) ||
            appointment.status.equals(
                "COMPLETED",
                ignoreCase = true
            )
        ) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isCancelling = true,
                    error = null
                )


            try {

                val updatedAppointment =
                    repository.cancelAppointment(
                        appointment.id
                    )


                _uiState.value =
                    _uiState.value.copy(
                        isCancelling = false,
                        appointment =
                            updatedAppointment,
                        error = null
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isCancelling = false,
                        error =
                            e.message
                                ?: "Unable to cancel appointment."
                    )
            }
        }
    }
}