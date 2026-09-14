package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.SystemDashboardResponse
import com.mediflowai.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository =
        DashboardRepository()

    private val _backendStatus =
        MutableStateFlow("Checking backend...")

    val backendStatus: StateFlow<String> =
        _backendStatus.asStateFlow()

    private val _dashboardData =
        MutableStateFlow<SystemDashboardResponse?>(null)

    val dashboardData: StateFlow<SystemDashboardResponse?> =
        _dashboardData.asStateFlow()

    private val _dashboardError =
        MutableStateFlow<String?>(null)

    val dashboardError: StateFlow<String?> =
        _dashboardError.asStateFlow()

    init {
        checkBackend()
        loadDashboard()
    }

    fun checkBackend() {

        viewModelScope.launch {

            _backendStatus.value =
                "Checking backend..."

            try {

                val status =
                    repository.getBackendHealth()

                _backendStatus.value =
                    "Backend Connected: $status"

            } catch (e: Exception) {

                _backendStatus.value =
                    "Failed: ${e.javaClass.simpleName}: ${e.message}"
            }
        }
    }

    fun loadDashboard() {

        viewModelScope.launch {

            _dashboardError.value = null

            try {

                _dashboardData.value =
                    repository.getSystemDashboard()

            } catch (e: Exception) {

                _dashboardError.value =
                    "${e.javaClass.simpleName}: ${e.message}"
            }
        }
    }
}