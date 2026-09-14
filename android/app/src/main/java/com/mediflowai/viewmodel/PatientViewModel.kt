package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class PatientUiState(
    val isLoading: Boolean = false,
    val patient: PatientProfile? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)


class PatientViewModel : ViewModel() {

    private val repository =
        PatientRepository()

    private val _uiState =
        MutableStateFlow(
            PatientUiState()
        )

    val uiState: StateFlow<PatientUiState> =
        _uiState.asStateFlow()


    init {
        loadPatientProfile()
    }


    fun loadPatientProfile() {

        viewModelScope.launch {

            _uiState.value =
                PatientUiState(
                    isLoading = true
                )

            try {

                val patient =
                    repository.getMyProfile()

                _uiState.value =
                    PatientUiState(
                        patient = patient
                    )

            } catch (e: Exception) {

                _uiState.value =
                    PatientUiState(
                        error = (
                                e.message
                                    ?: "Unable to load patient profile."
                                )
                    )
            }
        }
    }

    fun updateProfile(
        name: String,
        phone: String?,
        address: String?,
        medicalInformation: String?
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSaving = true,
                    error = null,
                    saveSuccess = false
                )

            try {

                val updatedPatient =
                    repository.updateMyProfile(
                        name = name,
                        phone = phone,
                        address = address,
                        medicalInformation =
                            medicalInformation
                    )

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        patient = updatedPatient,
                        saveSuccess = true
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        error = e.message
                            ?: "Unable to update profile."
                    )
            }
        }
    }
}