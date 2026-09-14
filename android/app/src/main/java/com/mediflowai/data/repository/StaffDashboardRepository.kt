package com.mediflowai.data.repository

import com.mediflowai.data.model.StaffDashboardResponse
import com.mediflowai.data.remote.RetrofitClient


class StaffDashboardRepository {


    suspend fun getDashboard():
            StaffDashboardResponse {

        return RetrofitClient
            .apiService
            .getStaffDashboard()
    }
}