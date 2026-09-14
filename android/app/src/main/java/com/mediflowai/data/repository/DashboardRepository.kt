package com.mediflowai.data.repository

import com.mediflowai.data.model.SystemDashboardResponse
import com.mediflowai.data.remote.RetrofitClient

class DashboardRepository {

    suspend fun getBackendHealth(): String {
        return RetrofitClient.apiService
            .getHealth()
            .status
    }

    suspend fun getSystemDashboard(): SystemDashboardResponse {
        return RetrofitClient.apiService
            .getSystemDashboard()
    }
}