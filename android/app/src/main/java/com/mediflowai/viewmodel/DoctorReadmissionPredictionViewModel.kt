package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.MLPredictionItem
import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.model.PatientReadmissionPredictionResponse
import com.mediflowai.data.model.ReadmissionPredictionRequest
import com.mediflowai.data.repository.PatientRepository
import com.mediflowai.data.repository.PredictionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorPredictionUiState(

    val isLoading: Boolean = false,

    val isPredicting: Boolean = false,

    val patient: PatientProfile? = null,

    val result:
    PatientReadmissionPredictionResponse? =
        null,

    val history:
    List<MLPredictionItem> =
        emptyList(),

    val error: String? = null
)


class DoctorReadmissionPredictionViewModel :
    ViewModel() {


    private val patientRepository =
        PatientRepository()

    private val predictionRepository =
        PredictionRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorPredictionUiState()
        )


    val uiState:
            StateFlow<DoctorPredictionUiState> =
        _uiState.asStateFlow()


    fun load(
        patientId: Int
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val patient =
                    patientRepository
                        .getPatient(
                            patientId
                        )


                val history =
                    predictionRepository
                        .getPatientPredictionHistory(
                            patientId
                        )


                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        patient = patient,
                        history = history,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load prediction data."
                    )
            }
        }
    }


    fun predict(
        patientId: Int,
        request: ReadmissionPredictionRequest
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isPredicting = true,
                    error = null
                )


            try {

                val result =
                    predictionRepository
                        .predictPatientReadmission(
                            patientId =
                                patientId,
                            request =
                                request
                        )


                val history =
                    predictionRepository
                        .getPatientPredictionHistory(
                            patientId
                        )


                _uiState.value =
                    _uiState.value.copy(
                        isPredicting = false,
                        result = result,
                        history = history,
                        error = null
                    )


            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isPredicting = false,
                        error =
                            e.message
                                ?: "Prediction failed."
                    )
            }
        }
    }
}