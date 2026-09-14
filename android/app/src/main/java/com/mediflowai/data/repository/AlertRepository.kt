package com.mediflowai.data.repository

import com.mediflowai.data.model.AlertItem
import com.mediflowai.data.remote.RetrofitClient

class AlertRepository {

    suspend fun getAlerts(): List<AlertItem> {
        return RetrofitClient.apiService
            .getAlerts()
    }

    suspend fun markAlertAsRead(
        alertId: Int
    ): AlertItem {

        return RetrofitClient.apiService
            .markAlertAsRead(
                alertId
            )
    }
}