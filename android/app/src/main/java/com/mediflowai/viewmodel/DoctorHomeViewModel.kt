package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.repository.AppointmentRepository
import com.mediflowai.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorHomeUiState(
    val isLoading: Boolean = false,
    val doctor: DoctorItem? = null,
    val appointments: List<AppointmentItem> =
        emptyList(),
    val error: String? = null
)


class DoctorHomeViewModel : ViewModel() {

    private val doctorRepository =
        DoctorRepository()

    private val appointmentRepository =
        AppointmentRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorHomeUiState()
        )


    val uiState:
            StateFlow<DoctorHomeUiState> =
        _uiState.asStateFlow()


    init {
        loadDashboard()
    }


    fun loadDashboard() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val doctor =
                    doctorRepository
                        .getMyDoctorProfile()


                val appointments =
                    appointmentRepository
                        .getAppointments()


                _uiState.value =
                    DoctorHomeUiState(
                        doctor = doctor,
                        appointments =
                            appointments,
                        isLoading = false
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load doctor dashboard."
                    )
            }
        }
    }
}