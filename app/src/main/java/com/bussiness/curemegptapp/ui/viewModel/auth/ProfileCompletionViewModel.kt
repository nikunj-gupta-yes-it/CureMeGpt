package com.bussiness.curemegptapp.ui.viewModel.auth

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bussiness.curemegptapp.data.model.ProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor() : ViewModel() {


    val currentStep = mutableStateOf(0)


    val profileData = mutableStateOf(ProfileData())


    fun updatePersonalInfo(
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?
    ) {

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