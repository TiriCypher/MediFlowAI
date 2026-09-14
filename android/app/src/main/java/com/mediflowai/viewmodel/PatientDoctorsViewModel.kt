package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class PatientDoctorsUiState(
    val isLoading: Boolean = false,
    val doctors: List<DoctorItem> = emptyList(),
    val departments: List<DepartmentItem> = emptyList(),
    val error: String? = null
)


class PatientDoctorsViewModel : ViewModel() {

    private val repository =
        DoctorRepository()

    private val _uiState =
        MutableStateFlow(
            PatientDoctorsUiState()
        )

    val uiState:
            StateFlow<PatientDoctorsUiState> =
        _uiState.asStateFlow()


    init {
        loadDoctors()
    }


    fun loadDoctors() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val doctors =
                    repository.getDoctors()

                val departments =
                    repository.getDepartments()

                _uiState.value =
                    PatientDoctorsUiState(
                        doctors = doctors,
                        departments = departments
                    )

            } catch (e: Exception) {

                _uiState.value =
                    PatientDoctorsUiState(
                        error =
                            e.message
                                ?: "Unable to load doctors."
                    )
            }
        }
    }
}