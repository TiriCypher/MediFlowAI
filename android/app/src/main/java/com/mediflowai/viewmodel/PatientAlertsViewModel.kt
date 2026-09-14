package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.AlertItem
import com.mediflowai.data.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class PatientAlertsUiState(
    val isLoading: Boolean = false,
    val alerts: List<AlertItem> = emptyList(),
    val markingReadId: Int? = null,
    val error: String? = null
)


class PatientAlertsViewModel : ViewModel() {

    private val repository =
        AlertRepository()

    private val _uiState =
        MutableStateFlow(
            PatientAlertsUiState()
        )

    val uiState:
            StateFlow<PatientAlertsUiState> =
        _uiState.asStateFlow()


    init {
        loadAlerts()
    }


    fun loadAlerts() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val alerts =
                    repository.getAlerts()

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        alerts = alerts,
                        error = null
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load alerts."
                    )
            }
        }
    }


    fun markAsRead(
        alertId: Int
    ) {

        val currentAlert =
            _uiState.value.alerts
                .firstOrNull {
                    it.id == alertId
                }

        if (
            currentAlert == null ||
            currentAlert.status.equals(
                "READ",
                ignoreCase = true
            )
        ) {
            return
        }


        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    markingReadId = alertId,
                    error = null
                )

            try {

                val updatedAlert =
                    repository.markAlertAsRead(
                        alertId
                    )

                val updatedAlerts =
                    _uiState.value.alerts.map {

                        if (
                            it.id ==
                            updatedAlert.id
                        ) {
                            updatedAlert
                        } else {
                            it
                        }
                    }

                _uiState.value =
                    _uiState.value.copy(
                        alerts =
                            updatedAlerts,
                        markingReadId =
                            null
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        markingReadId =
                            null,
                        error =
                            e.message
                                ?: "Unable to mark alert as read."
                    )
            }
        }
    }
}