package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorAppointmentsUiState(
    val isLoading: Boolean = false,
    val appointments: List<AppointmentItem> =
        emptyList(),
    val error: String? = null
)


class DoctorAppointmentsViewModel :
    ViewModel() {

    private val repository =
        AppointmentRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorAppointmentsUiState()
        )


    val uiState:
            StateFlow<DoctorAppointmentsUiState> =
        _uiState.asStateFlow()


    init {
        loadAppointments()
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
                    repository
                        .getAppointments()


                _uiState.value =
                    DoctorAppointmentsUiState(
                        isLoading = false,
                        appointments =
                            appointments
                    )

            } catch (e: Exception) {

                _uiState.value =
                    DoctorAppointmentsUiState(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load appointments."
                    )
            }
        }
    }
}