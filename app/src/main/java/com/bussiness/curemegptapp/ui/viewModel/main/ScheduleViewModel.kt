package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentItem
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var uiState by mutableStateOf(ScheduleUiState())
        private set

    init {
        getAppointments()
    }

    fun getAppointments() {
        viewModelScope.launch {
            repository.getAppointmentList().collect { result ->
                when (result) {

                    is NetworkResult.Loading -> {
                        uiState = uiState.copy(isLoading = true)
                    }

                    is NetworkResult.Success -> {
                        val list = result.data?.map { it.toUIModel() } ?: emptyList()

                        uiState = uiState.copy(
                            isLoading = false,
                            appointmentList = list
                        )
                    }

                    is NetworkResult.Error -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
data class AppointmentUIModel(
    val title: String,
    val doctor: String,
    val patientName: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val icon: Int,
    val isVisibleItem: Boolean = true
)

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val appointmentList: List<AppointmentUIModel> = emptyList(),
    val error: String? = null
)

fun AppointmentItem.toUIModel(): AppointmentUIModel {
    return AppointmentUIModel(
        title = appointment_type?.name ?: "Appointment",
        doctor = preferred_doctor,
        patientName = if (appointment_for_whom == "self") {
            user?.name ?: ""
        } else {
            family_member?.full_name ?: ""
        },
        date = date,
        time = time,
        location = preferred_clinic,
        description = description,
        icon = R.drawable.ic_medical_bag_icon,
        isVisibleItem = true
    )
}