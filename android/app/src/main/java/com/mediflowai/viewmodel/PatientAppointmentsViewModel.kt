package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.model.AppointmentCreateRequest


data class PatientAppointmentsUiState(
    val isLoading: Boolean = false,
    val isBooking: Boolean = false,
    val appointments: List<AppointmentItem> = emptyList(),
    val doctors: List<DoctorItem> = emptyList(),
    val departments: List<DepartmentItem> = emptyList(),
    val bookingSuccess: Boolean = false,
    val error: String? = null
)

class PatientAppointmentsViewModel : ViewModel() {

    private val repository =
        AppointmentRepository()

    private val _uiState =
        MutableStateFlow(
            PatientAppointmentsUiState()
        )

    val uiState:
            StateFlow<PatientAppointmentsUiState> =
        _uiState.asStateFlow()

    init {
        loadAppointments()
        loadBookingData()
    }

    fun loadAppointments() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val appointments =
                    repository.getAppointments()

                val doctors =
                    repository.getDoctors()

                val departments =
                    repository.getDepartments()

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        appointments = appointments,
                        doctors = doctors,
                        departments = departments
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                            ?: "Unable to load appointments."
                    )
            }
        }
    }
    fun loadBookingData() {

        viewModelScope.launch {

            try {

                val doctors =
                    repository.getDoctors()

                val departments =
                    repository.getDepartments()

                _uiState.value =
                    _uiState.value.copy(
                        doctors = doctors,
                        departments = departments
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        error = e.message
                            ?: "Unable to load doctors."
                    )
            }
        }
    }

    fun bookAppointment(
        patientId: Int,
        doctorId: Int,
        departmentId: Int?,
        appointmentTime: String,
        reason: String?,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isBooking = true,
                    error = null,
                    bookingSuccess = false
                )

            try {

                repository.createAppointment(
                    AppointmentCreateRequest(
                        patient_id = patientId,
                        doctor_id = doctorId,
                        department_id = departmentId,
                        appointment_time = appointmentTime,
                        reason = reason
                    )
                )

                _uiState.value =
                    _uiState.value.copy(
                        isBooking = false,
                        bookingSuccess = true
                    )

                loadAppointments()

                onSuccess()

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isBooking = false,
                        error = e.message
                            ?: "Unable to book appointment."
                    )
            }
        }
    }

}