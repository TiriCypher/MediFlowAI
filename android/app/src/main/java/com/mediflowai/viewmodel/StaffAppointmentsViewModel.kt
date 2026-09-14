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


data class StaffAppointmentsUiState(

    val isLoading: Boolean = false,

    val appointments:
    List<AppointmentItem> =
        emptyList(),

    val patients:
    List<PatientProfile> =
        emptyList(),

    val doctors:
    List<DoctorItem> =
        emptyList(),

    val departments:
    List<StaffDepartmentSummary> =
        emptyList(),

    val error: String? = null
)


class StaffAppointmentsViewModel :
    ViewModel() {


    private val appointmentRepository =
        AppointmentRepository()

    private val patientRepository =
        PatientRepository()

    private val doctorRepository =
        DoctorRepository()


    private val _uiState =
        MutableStateFlow(
            StaffAppointmentsUiState()
        )


    val uiState:
            StateFlow<StaffAppointmentsUiState> =
        _uiState.asStateFlow()


    fun loadAppointments() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val appointmentsDeferred =
                    async {
                        appointmentRepository
                            .getAppointments()
                    }

                val patientsDeferred =
                    async {
                        patientRepository
                            .getPatients()
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


                _uiState.value =
                    StaffAppointmentsUiState(

                        appointments =
                            appointmentsDeferred
                                .await(),

                        patients =
                            patientsDeferred
                                .await(),

                        doctors =
                            doctorsDeferred
                                .await(),

                        departments =
                            departmentsDeferred
                                .await()
                    )

            } catch (e: Exception) {

                _uiState.value =
                    StaffAppointmentsUiState(
                        error =
                            e.message
                                ?: "Unable to load appointments."
                    )
            }
        }
    }
}