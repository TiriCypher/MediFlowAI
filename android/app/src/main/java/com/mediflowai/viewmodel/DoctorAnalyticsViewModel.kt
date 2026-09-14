package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.DoctorAnalyticsResponse
import com.mediflowai.data.repository.DoctorAnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorAnalyticsUiState(

    val isLoading: Boolean =
        false,

    val analytics:
    DoctorAnalyticsResponse? =
        null,

    val error: String? =
        null
)


class DoctorAnalyticsViewModel :
    ViewModel() {


    private val repository =
        DoctorAnalyticsRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorAnalyticsUiState()
        )


    val uiState:
            StateFlow<DoctorAnalyticsUiState> =
        _uiState.asStateFlow()


    fun loadAnalytics() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading =
                        true,
                    error =
                        null
                )


            try {

                val analytics =
                    repository
                        .getAnalytics()


                _uiState.value =
                    DoctorAnalyticsUiState(
                        analytics =
                            analytics
                    )


            } catch (e: Exception) {

                _uiState.value =
                    DoctorAnalyticsUiState(
                        error =
                            e.message
                                ?: "Unable to load analytics."
                    )
            }
        }
    }
}