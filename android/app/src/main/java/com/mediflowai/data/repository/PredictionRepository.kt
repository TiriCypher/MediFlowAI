package com.mediflowai.data.repository

import com.mediflowai.data.model.MLPredictionItem
import com.mediflowai.data.model.PatientReadmissionPredictionResponse
import com.mediflowai.data.model.ReadmissionPredictionRequest
import com.mediflowai.data.remote.RetrofitClient


class PredictionRepository {


    suspend fun predictPatientReadmission(
        patientId: Int,
        request: ReadmissionPredictionRequest
    ): PatientReadmissionPredictionResponse {

        return RetrofitClient.apiService
            .predictPatientReadmission(
                patientId = patientId,
                request = request
            )
    }


    suspend fun getPatientPredictionHistory(
        patientId: Int
    ): List<MLPredictionItem> {

        return RetrofitClient.apiService
            .getPatientPredictionHistory(
                patientId
            )
    }
}