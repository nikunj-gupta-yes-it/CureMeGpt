package com.bussiness.curemegptapp.repository

import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.profilemodel.Data.UserProfile
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Part

interface Repository {

    fun loginRequest(emailOrPhone: String, password: String, fcmToken: String): Flow<Resource<LoginResponse>>
    fun registerRequest(name: String, emailOrPhone: String, password: String, deviceType: String): Flow<Resource<LoginResponse>>
    fun updatePasswordRequest(emailOrPhone: String, password: String): Flow<Resource<LoginResponse>>
    fun forgotOtpRequest(emailOrPhone: String): Flow<Resource<LoginResponse>>
    fun otpVerifyRequest(emailOrPhone: String,otp: String,fcmToken: String,fromScreen: String): Flow<Resource<LoginResponse>>
    fun sendOtpRequest(emailOrPhone: String): Flow<Resource<LoginResponse>>
    fun profileRequest(): Flow<Resource<PersonalModel>>

    fun updatePersonalRequest(
        name: String, phone: String, email: String, dob: String,
        gender: String, height: String, heightType: String,
        weight: String, weightType: String,imageProfile: MultipartBody.Part?
    ): Flow<Resource<PersonalModel>>

    fun generalProfileRequest(
        bloodGroup: String,
        allergies:String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ): Flow<Resource<ProfileResponse>>

     fun verifyEmailPhoneRequest(
        emailOrPhone: String
    ) : Flow<NetworkResult<String>>

    suspend fun completeGeneralProfileHistoryRequest(
        @Field("chronic_condition[]") chronicConditions: String,
        @Field("surgical_history") surgicalHistory: String,
        @Field("current_medications[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplement : String
    ) : Flow<Resource<ProfileResponse>>


    suspend fun completeProfileDocumentsRequest(
        @Part files: List<MultipartBody.Part>
    ): Flow<Resource<ProfileResponse>>


    suspend fun onBoardingData() : Flow<Resource<OnboardingResponse>>

    suspend fun getUserDetails() : Flow<NetworkResult<UserProfile>>

    fun updateProfilePicture(
        @Part profile_image: MultipartBody.Part
    ) : Flow<NetworkResult<String>>

    fun getFAQ(): Flow<NetworkResult<List<QuestionAnswer>>>

    fun getPrivacyPolicy() : Flow<NetworkResult<String>>

    fun getTermsAndConditions() : Flow<NetworkResult<String>>
    fun getAccountPrivacyPolicy() : Flow<NetworkResult<String>>

    fun helpSupport() : Flow<NetworkResult<String>>

    fun aboutUs() : Flow<NetworkResult<String>>


}