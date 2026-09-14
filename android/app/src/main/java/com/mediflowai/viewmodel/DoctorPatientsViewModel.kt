package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorPatientsUiState(

    val isLoading: Boolean =
        false,

    val patients:
    List<PatientProfile> =
        emptyList(),

    val error: String? =
        null
)


class DoctorPatientsViewModel :
    ViewModel() {


    private val repository =
        PatientRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorPatientsUiState()
        )


    val uiState:
            StateFlow<DoctorPatientsUiState> =
        _uiState.asStateFlow()


    init {

        loadPatients()
    }


    fun loadPatients() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val patients =
                    repository
                        .getPatients()


                _uiState.value =
                    DoctorPatientsUiState(
                        isLoading = false,
                        patients =
                            patients
                    )


            } catch (e: Exception) {

                _uiState.value =
                    DoctorPatientsUiState(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load patients."
                    )
            }
        }
    }
}