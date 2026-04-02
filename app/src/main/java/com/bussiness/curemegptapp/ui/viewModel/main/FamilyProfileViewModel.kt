package com.bussiness.curemegptapp.ui.viewModel.main

// FamilyProfileViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
class FamilyProfileViewModel @Inject constructor(
    val repository: Repository
) : ViewModel() {

    private val _familyMember = MutableStateFlow<FamilyMember?>(null)
    val familyMember: StateFlow<FamilyMember?> = _familyMember.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()



    fun loadFamilyMember(memberId: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            getSampleFamilyMember(memberId)
        }
    }



    fun deleteFamilyMember() {
        viewModelScope.launch {
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

    private fun getSampleFamilyMember(id:Int) {

        viewModelScope.launch {
            LoaderManager.show()
            repository.getFamilyMemberProfileDetails(id).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        val data = it.data
                        _familyMember.value = data
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                    }
                    else ->{
                    }
                }
            }
        }
    }

    fun deleteProfile(id:Int,onSucess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteFamilyMember(id).collectLatest {
                when(it){
                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        onSucess()
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message ?: "Unknown error")
                    }
                    else ->{
                    }
                }
            }
        }

    }


}