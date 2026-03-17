package com.bussiness.curemegptapp.ui.viewModel.main

//EditProfileViewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {


    val currentStep = mutableStateOf(0)

    // All profile data
    val profileData = mutableStateOf(ProfileData())
//
//    fun getProfileData(
//        onSuccess: () -> Unit,
//        onError: (msg: String) -> Unit
//    ){
//        viewModelScope.launch {
//            LoaderManager.show()
//            repository.getPersonalProfile().collectLatest {
//                when(it){
//                    is Resource.Success -> {
//                        Log.e("ProfileCompletionVM", "Inside Sucess Here")
//
//                        LoaderManager.hide()
//                        val userData =  it.data.data?.user
//                        val baseUrl = AppConstant.IMAGE_BASE_URL // Replace with your actual base URL
//
//                        val profileImage = userData?.profile_photo
//                            ?.takeIf { it.isNotBlank() }
//                            ?.let { "$baseUrl$it" } ?: ""
//                        profileData.value = profileData.value.copy(
//                            id = userData?.id ?: 0,
//                            fullName = userData?.name ?: "",
//                            contactNumber = userData?.phone ?: "",
//                            email = userData?.email ?: "",
//                            dateOfBirth = userData?.dob ?: "",
//                            gender = userData?.gender ?: "",
//                            height = userData?.height ?: "",
//                            weight = userData?.weight ?: "",
//                            profileImage = profileImage ?: ""
//                        )
//                        onSuccess()
//                    }
//                    is Resource.Error -> {
//                        LoaderManager.hide()
//                        Log.e("ProfileCompletionVM", "Error updating medical history: ${it.message}")
//                        onError(it.message ?: "An error occurred")
//                    }
//                    else -> {
//
//                    }
//                }
//
//            }
//        }
//    }

    var profileFormState by mutableStateOf(ProfileData())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun getProfileData(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            LoaderManager.show()
            repository.getPersonalProfile().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val user = result.data
                        val baseUrl = AppConstant.IMAGE_BASE_URL
                        val image = user?.profile_photo
                            ?.takeIf { it.isNotBlank() }
                            ?.let { "$baseUrl$it" } ?: ""
                        profileFormState = profileFormState.copy(
                            id = user?.id ?: 0,
                            fullName = user?.name ?: "",
                            contactNumber = user?.phone ?: "",
                            email = user?.email ?: "",
                            dateOfBirth = user?.dob ?: "",
                            gender = user?.gender ?: "",
                            height = user?.height ?: "",
                            weight = user?.weight ?: "",
                            profileImage = image
                        )
                        isLoading = false
                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        isLoading = false
                        onError(result.message ?: "Something went wrong")
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateField(update: ProfileData.() -> ProfileData) {
        profileFormState = profileFormState.update()
    }


    // Functions to update profile data
    fun updatePersonalInfo(
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?,
        profileMultipart: MultipartBody.Part?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {


        viewModelScope.launch {
            repository.updatePersonalProfile(
                name = fullName,
                phone = contactNumber,
                email = email,
                dob = dateOfBirth,
                gender = gender,
                height = height,
                weight = weight,
                profileImage = profileMultipart
            ).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        Log.e("EditProfileVM", "Personal info updated successfully")
                        val current = profileData.value.copy(
                            fullName = fullName,
                            contactNumber = contactNumber,
                            email = email,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            height = height,
                            weight = weight,
                            profilePhotoUri = profilePhotoUri
                        )
                        profileData.value = current
                        onSuccess()
                    }

                    is NetworkResult.Error -> {
                        Log.e("EditProfileVM", "Error updating personal info: ${it.message}")
                        onError(it.message ?: "An error occurred")
                    }

                    else -> {}


                }
            }
        }
    }


    fun updateGeneralInfo(
        bloodGroup: String,
        allergies: List<String>,
        emergencyName: String,
        emergencyPhone: String
    ) {
        val current = profileData.value.copy(
            bloodGroup = bloodGroup,
            allergies = allergies,
            emergencyContactName = emergencyName,
            emergencyContactPhone = emergencyPhone
        )
        profileData.value = current
    }

    fun updateMedicalHistory(
        chronicConditions: List<String>,
        surgicalHistory: String,
        currentMedications: List<String>,
        currentSupplements: List<String>
    ) {
        val current = profileData.value.copy(
            chronicConditions = chronicConditions,
            surgicalHistory = surgicalHistory,
            currentMedications = currentMedications,
            currentSupplements = currentSupplements
        )
        profileData.value = current
    }

    fun addUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.add(uri)
        profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
    }

    fun removeUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.remove(uri)
        profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
    }

    fun clearUploadedFiles() {
        profileData.value = profileData.value.copy(uploadedFiles = emptyList())
    }

    fun goToNextStep() {
        currentStep.value++
    }

    fun goToPreviousStep() {
        if (currentStep.value > 0) {
            currentStep.value--
        }
    }

    fun submitProfile() {
        // Sab data log karo
        Log.d("PROFILE_DATA", "========== PROFILE COMPLETION DATA ==========")

        // Personal Info
        Log.d("PROFILE_DATA", "PERSONAL INFORMATION:")
        Log.d("PROFILE_DATA", "Full Name: ${profileData.value.fullName}")
        Log.d("PROFILE_DATA", "Contact Number: ${profileData.value.contactNumber}")
        Log.d("PROFILE_DATA", "Email: ${profileData.value.email}")
        Log.d("PROFILE_DATA", "Date of Birth: ${profileData.value.dateOfBirth}")
        Log.d("PROFILE_DATA", "Gender: ${profileData.value.gender}")
        Log.d("PROFILE_DATA", "Height: ${profileData.value.height}")
        Log.d("PROFILE_DATA", "Weight: ${profileData.value.weight}")
        Log.d("PROFILE_DATA", "Profile Photo URI: ${profileData.value.profilePhotoUri}")

        // General Info
        Log.d("PROFILE_DATA", "\nGENERAL INFORMATION:")
        Log.d("PROFILE_DATA", "Blood Group: ${profileData.value.bloodGroup}")
        Log.d("PROFILE_DATA", "Allergies: ${profileData.value.allergies}")
        Log.d("PROFILE_DATA", "Emergency Contact Name: ${profileData.value.emergencyContactName}")
        Log.d("PROFILE_DATA", "Emergency Contact Phone: ${profileData.value.emergencyContactPhone}")

        // Medical History
        Log.d("PROFILE_DATA", "\nMEDICAL HISTORY:")
        Log.d("PROFILE_DATA", "Chronic Conditions: ${profileData.value.chronicConditions}")
        Log.d("PROFILE_DATA", "Surgical History: ${profileData.value.surgicalHistory}")
        Log.d("PROFILE_DATA", "Current Medications: ${profileData.value.currentMedications}")
        Log.d("PROFILE_DATA", "Current Supplements: ${profileData.value.currentSupplements}")

        // Documents
        Log.d("PROFILE_DATA", "\nDOCUMENTS:")
        Log.d("PROFILE_DATA", "Uploaded Files Count: ${profileData.value.uploadedFiles.size}")
        profileData.value.uploadedFiles.forEachIndexed { index, uri ->
            Log.d("PROFILE_DATA", "File ${index + 1}: ${uri.lastPathSegment}")
        }

        Log.d("PROFILE_DATA", "========== END OF PROFILE DATA ==========\n")

        // Yahan aap local storage ya navigation kar sakte hain
        // For example, navigate to home screen
        // navController.navigate("home")
    }
}