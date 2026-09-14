package com.mediflowai.data.repository

import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.remote.RetrofitClient


class DoctorRepository {

    suspend fun getMyDoctorProfile():
            DoctorItem {

        return RetrofitClient.apiService
            .getMyDoctorProfile()
    }


    suspend fun getDoctors():
            List<DoctorItem> {

        return RetrofitClient.apiService
            .getDoctors()
    }


    suspend fun getDepartments():
            List<DepartmentItem> {

        return RetrofitClient.apiService
            .getDepartments()
    }
}