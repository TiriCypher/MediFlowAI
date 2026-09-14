package com.mediflowai.data.repository

import com.mediflowai.data.model.DoctorAnalyticsResponse
import com.mediflowai.data.remote.RetrofitClient


class DoctorAnalyticsRepository {


    suspend fun getAnalytics():
            DoctorAnalyticsResponse {

        return RetrofitClient
            .apiService
            .getDoctorAnalytics()
    }
}