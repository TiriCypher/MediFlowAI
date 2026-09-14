package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.StaffDashboardResponse
import com.mediflowai.data.repository.StaffDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class StaffHomeUiState(

    val isLoading: Boolean =
        false,

    val dashboard:
    StaffDashboardResponse? =
        null,

    val error: String? =
        null
)


class StaffHomeViewModel :
    ViewModel() {


    private val repository =
        StaffDashboardRepository()


    private val _uiState =
        MutableStateFlow(
            StaffHomeUiState()
        )


    val uiState:
            StateFlow<StaffHomeUiState> =
        _uiState.asStateFlow()


    fun loadDashboard() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading =
                        true,
                    error =
                        null
                )


            try {

                val dashboard =
                    repository
                        .getDashboard()


                _uiState.value =
                    StaffHomeUiState(
                        dashboard =
                            dashboard
                    )


            } catch (e: Exception) {

                _uiState.value =
                    StaffHomeUiState(
                        error =
                            e.message
                                ?: "Unable to load staff dashboard."
                    )
            }
        }
    }
}