package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // For mood selection
    private val _selectedMood = mutableStateOf("")
    val selectedMood: State<String> = _selectedMood

    // Initial hardcoded data (API aane ke baad ye remove/replace hoga)
    private val initialMedications = listOf("Lisinopril 10mg", "Vitamin D")
    private val initialAllergies = listOf("Penicillin", "Shellfish")
    private val initialSteps = listOf(
        "Set a reminder for your blood pressure medication",
        "Schedule your annual checkup",
        "Complete emergency contact information."
    )
    private val initialAlerts = listOf(
        "Blood pressure medication reminder",
        "Annual checkup due"
    )

    private   val attentionItems = listOf(
        Triple("Tooth Pain Symptoms Detected", "For: James Logan",true),
        Triple("Overdue Dental Cleaning", "For: Rosy Logan",true)


    )

    // Things needing attention data
    private val initialAttentionItems = listOf(
        AttentionItem(
            id = 1,
            title = "Tooth Pain Symptoms Detected",
            subtitle = "For: James Logan",
            isUrgent = true,
            forPerson = "James Logan"
        ),
        AttentionItem(
            id = 2,
            title = "Overdue Dental Cleaning",
            subtitle = "For: Rosy Logan",
            isUrgent = true,
            forPerson = "Rosy Logan"
        ),
        AttentionItem(
            id = 3,
            title = "Annual Physical Due",
            subtitle = "For: James Logan",
            isUrgent = false,
            forPerson = "James Logan"
        )
    )

    init {
        // Load initial data
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = "James",
            profileCompletion = 0.5f,
            medications = initialMedications,
            allergies = initialAllergies,
            recommendedSteps = initialSteps,
            alerts = initialAlerts,
           // attentionItems = attentionItems,
            attentionItems = initialAttentionItems
        )
    }

    fun updateMood(mood: String) {
        _selectedMood.value = mood
        // Yahan API call kar sakte hain mood update karne ke liye
    }

    fun scheduleAttentionItem(itemId: Int) {
        viewModelScope.launch {
            // Future API implementation for scheduling
            // repository.scheduleAttentionItem(itemId)

            // For now, just update UI state
            val currentItems = _uiState.value.attentionItems
            val updatedItems = currentItems.map { item ->
                if (item.id == itemId) {
                    item.copy(isScheduled = true)
                } else {
                    item
                }
            }

            _uiState.value = _uiState.value.copy(
                attentionItems = updatedItems
            )
        }
    }

    fun markAttentionItemAsResolved(itemId: Int) {
        viewModelScope.launch {
            // Future API implementation
            // repository.markAsResolved(itemId)

            // Update UI state
            val currentItems = _uiState.value.attentionItems
            val updatedItems = currentItems.filter { it.id != itemId }

            _uiState.value = _uiState.value.copy(
                attentionItems = updatedItems
            )
        }
    }

    fun addNewAttentionItem(title: String, subtitle: String, isUrgent: Boolean, forPerson: String) {
        val newItem = AttentionItem(
            id = (_uiState.value.attentionItems.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            subtitle = subtitle,
            isUrgent = isUrgent,
            forPerson = forPerson
        )

        val updatedItems = _uiState.value.attentionItems + newItem

        _uiState.value = _uiState.value.copy(
            attentionItems = updatedItems
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            // Future API implementation:
            // try {
            //     val homeData = repository.getHomeData()
            //     _uiState.value = homeData
            // } catch (e: Exception) {
            //     // Handle error
            // }
        }
    }
}

// Data class for Home Screen UI State
data class HomeUiState(
    val userGreating: String = "Hello",
    val userName: String = "James",
    val profileCompletion: Float = 0f,
    val medications: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val recommendedSteps: List<String> = emptyList(),
    val alerts: List<String> = emptyList(),
    val attentionItems: List<AttentionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Data class for attention items
data class AttentionItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isUrgent: Boolean,
    val forPerson: String,
    val isScheduled: Boolean = false,
    val createdAt: String = "", // For future API integration
    val dueDate: String = "" // For future API integration
)
//package com.bussiness.curemegptapp.ui.viewModel.main
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.State
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.bussiness.curemegptapp.data.model.AttentionItem
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class HomeViewModel @Inject constructor() : ViewModel() {
//
//    // UI State
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    // For mood selection
//    private val _selectedMood = mutableStateOf("")
//    val selectedMood: State<String> = _selectedMood
//
//    // Initial hardcoded data (API aane ke baad ye remove/replace hoga)
//    private val initialMedications = listOf("Lisinopril 10mg", "Vitamin D")
//    private val initialAllergies = listOf("Penicillin", "Shellfish")
//    private val initialSteps = listOf(
//        "Set a reminder for your blood pressure medication",
//        "Schedule your annual checkup",
//        "Complete emergency contact information"
//    )
//    private val initialAlerts = listOf(
//        "Blood pressure medication reminder",
//        "Annual checkup due"
//    )
//
//    init {
//        // Load initial data
//        loadHomeData()
//    }
//
//    private fun loadHomeData() {
//        // Yahaan aap hardcoded data set kar rahe hain
//        // Future mein yahaan API call hoga
//        _uiState.value = HomeUiState(
//            userName = "James",
//            profileCompletion = 0.5f,
//            medications = initialMedications,
//            allergies = initialAllergies,
//            recommendedSteps = initialSteps,
//            alerts = initialAlerts
//        )
//    }
//
//    fun updateMood(mood: String) {
//        _selectedMood.value = mood
//        // Yahan API call kar sakte hain mood update karne ke liye
//    }
//
//    fun refreshData() {
//        viewModelScope.launch {
//            // Future API implementation:
//            // try {
//            //     val homeData = repository.getHomeData()
//            //     _uiState.value = homeData
//            // } catch (e: Exception) {
//            //     // Handle error
//            // }
//        }
//    }
//
//    // Add more functions as needed for API calls
//}
//
//// Data class for Home Screen UI State
//data class HomeUiState(
//    val userName: String = "",
//    val profileCompletion: Float = 0f,
//    val medications: List<String> = emptyList(),
//    val allergies: List<String> = emptyList(),
//    val recommendedSteps: List<String> = emptyList(),
//    val attentionItems: List<AttentionItem> = emptyList(),
//    val alerts: List<String> = emptyList(),
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)

