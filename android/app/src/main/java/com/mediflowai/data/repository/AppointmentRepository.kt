package com.mediflowai.data.repository

import com.mediflowai.data.model.AppointmentCreateRequest
import com.mediflowai.data.model.AppointmentItem
import com.mediflowai.data.model.DepartmentItem
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.remote.RetrofitClient

class AppointmentRepository {

    suspend fun getAppointments():
            List<AppointmentItem> {

        return RetrofitClient.apiService
            .getAppointments()
    }


    suspend fun getAppointment(
        appointmentId: Int
    ): AppointmentItem {

        return RetrofitClient.apiService
            .getAppointment(
                appointmentId
            )
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


    suspend fun createAppointment(
        request: AppointmentCreateRequest
    ): AppointmentItem {

        return RetrofitClient.apiService
            .createAppointment(
                request
            )
    }

    suspend fun cancelAppointment(
        appointmentId: Int
    ): AppointmentItem {

        return RetrofitClient.apiService
            .cancelAppointment(
                appointmentId
            )
    }

    suspend fun completeAppointment(
        appointmentId: Int
    ): AppointmentItem {

        return RetrofitClient.apiService
            .completeAppointment(
                appointmentId
            )
    }
}