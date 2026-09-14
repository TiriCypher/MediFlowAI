package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorPatientDetailsUiState(

    val isLoading: Boolean =
        false,

    val patient:
    PatientProfile? =
        null,

    val error: String? =
        null
)


class DoctorPatientDetailsViewModel :
    ViewModel() {


    private val repository =
        PatientRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorPatientDetailsUiState()
        )


    val uiState:
            StateFlow<DoctorPatientDetailsUiState> =
        _uiState.asStateFlow()


    fun loadPatient(
        patientId: Int
    ) {

        viewModelScope.launch {

            _uiState.value =
                DoctorPatientDetailsUiState(
                    isLoading = true
                )


            try {

                val patient =
                    repository
                        .getPatient(
                            patientId
                        )


                _uiState.value =
                    DoctorPatientDetailsUiState(
                        patient =
                            patient
                    )


            } catch (e: Exception) {

                _uiState.value =
                    DoctorPatientDetailsUiState(
                        error =
                            e.message
                                ?: "Unable to load patient."
                    )
            }
        }
    }
}