package com.mediflowai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediflowai.data.model.DoctorItem
import com.mediflowai.data.remote.BackendUser
import com.mediflowai.data.remote.RetrofitClient
import com.mediflowai.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class DoctorProfileUiState(

    val isLoading: Boolean = false,

    val doctor: DoctorItem? = null,

    val user: BackendUser? = null,

    val error: String? = null
)


class DoctorProfileViewModel :
    ViewModel() {


    private val doctorRepository =
        DoctorRepository()


    private val _uiState =
        MutableStateFlow(
            DoctorProfileUiState()
        )


    val uiState:
            StateFlow<DoctorProfileUiState> =
        _uiState.asStateFlow()


    init {

        loadProfile()
    }


    fun loadProfile() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                val doctor =
                    doctorRepository
                        .getMyDoctorProfile()


                val user =
                    RetrofitClient
                        .apiService
                        .getCurrentUser()


                _uiState.value =
                    DoctorProfileUiState(
                        doctor = doctor,
                        user = user,
                        isLoading = false
                    )


            } catch (e: Exception) {

                _uiState.value =
                    DoctorProfileUiState(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Unable to load profile."
                    )
            }
        }
    }
}