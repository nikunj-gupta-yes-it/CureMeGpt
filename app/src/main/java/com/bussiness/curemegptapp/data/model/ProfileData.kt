package com.bussiness.curemegptapp.data.model

import android.net.Uri

data class ProfileData(
    var fullName: String = "",
    var contactNumber: String = "",
    var email: String = "",
    var dateOfBirth: String = "",
    var gender: String = "Select",
    var height: String = "",
    var weight: String = "",
    var profilePhotoUri: Uri? = null,
    var bloodGroup: String = "Select",
    var allergies: List<String> = emptyList(),
    var emergencyContactName: String = "",
    var emergencyContactPhone: String = "",
    var chronicConditions: List<String> = emptyList(),
    var surgicalHistory: String = "",
    var currentMedications: List<String> = emptyList(),
    var currentSupplements: List<String> = emptyList(),
    var uploadedFiles: List<Uri> = emptyList()
)