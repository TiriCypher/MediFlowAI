package com.mediflowai.data.repository

import com.mediflowai.data.model.PatientProfile
import com.mediflowai.data.remote.PatientUpdateRequest
import com.mediflowai.data.remote.RetrofitClient


class PatientRepository {


    suspend fun getMyProfile():
            PatientProfile {

        return RetrofitClient.apiService
            .getMyPatientProfile()
    }


    suspend fun getPatients():
            List<PatientProfile> {

        return RetrofitClient.apiService
            .getPatients()
    }


    suspend fun getPatient(
        patientId: Int
    ): PatientProfile {

        return RetrofitClient.apiService
            .getPatient(
                patientId
            )
    }


    suspend fun updateMyProfile(
        name: String,
        phone: String?,
        address: String?,
        medicalInformation: String?
    ): PatientProfile {

        return RetrofitClient.apiService
            .updateMyPatientProfile(
                PatientUpdateRequest(
                    name = name,
                    phone = phone,
                    address = address,
                    medical_information =
                        medicalInformation
                )
            )
    }
}