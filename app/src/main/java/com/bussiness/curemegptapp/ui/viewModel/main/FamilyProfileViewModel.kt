package com.bussiness.curemegptapp.ui.viewModel.main

// FamilyProfileViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// FamilyMember.kt
data class FamilyMember(
    val id: String = "",
    val name: String = "",
    val profileImage: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val relation: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val bloodGroup: String = "",
    val allergies: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val chronicConditions: String = "",
    val surgicalHistory: String = "",
    val currentMedications: List<String> = emptyList(),
    val currentSupplements: List<String> = emptyList(),
    val documents: List<Document> = emptyList()
)

data class Document(
    val id: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val fileType: String = ""
)

@HiltViewModel
class FamilyProfileViewModel @Inject constructor() : ViewModel() {

    private val _familyMember = MutableStateFlow<FamilyMember?>(null)
    val familyMember: StateFlow<FamilyMember?> = _familyMember.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Initialize with sample data or fetch from repository
    init {
        loadFamilyMember()
    }

    fun loadFamilyMember(memberId: String = "1") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // यहाँ आप API call या database से data fetch कर सकते हैं
                val member = getSampleFamilyMember()
                _familyMember.value = member
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateFamilyMember(updatedMember: FamilyMember) {
        viewModelScope.launch {
            _familyMember.value = updatedMember
            // यहाँ आप data को repository में save कर सकते हैं
        }
    }

    fun deleteFamilyMember() {
        viewModelScope.launch {
            // Delete logic here
            _familyMember.value = null
        }
    }

    fun addDocument(document: Document) {
        val currentMember = _familyMember.value
        if (currentMember != null) {
            val updatedDocuments = currentMember.documents + document
            val updatedMember = currentMember.copy(documents = updatedDocuments)
            _familyMember.value = updatedMember
        }
    }

    fun removeDocument(documentId: String) {
        val currentMember = _familyMember.value
        if (currentMember != null) {
            val updatedDocuments = currentMember.documents.filter { it.id != documentId }
            val updatedMember = currentMember.copy(documents = updatedDocuments)
            _familyMember.value = updatedMember
        }
    }

    private fun getSampleFamilyMember(): FamilyMember {
        return FamilyMember(
            id = "1",
            name = "Rose Logan",
            profileImage = "profile_image_url",
            contactNumber = "+1 555 987 654",
            email = "rosy@gmail.com",
            relation = "Spouse",
            dateOfBirth = "05/08/1995",
            gender = "Female",
            height = "150 Cm",
            weight = "55 Kg",
            bloodGroup = "O+",
            allergies = "Nuts",
            emergencyContact = "--",
            emergencyPhone = "--",
            chronicConditions = "Hypertension",
            surgicalHistory = "--",
            currentMedications = listOf("--", "--", "--", "--"),
            currentSupplements = listOf("--", "--", "--"),
            documents = listOf(
                Document(
                    id = "doc1",
                    fileName = "Demo_1.Pdf",
                    fileUrl = "",
                    fileType = "pdf"
                )
            )
        )
    }
}