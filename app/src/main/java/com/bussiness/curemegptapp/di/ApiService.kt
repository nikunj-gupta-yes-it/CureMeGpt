package com.bussiness.curemegptapp.di

import com.bussiness.curemegptapp.apiendpoint.ApiEndPoint
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import kotlinx.serialization.json.JsonObject
import com.google.gson.JsonObject as GsonJsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST(ApiEndPoint.LOGIN_URL)
    suspend fun loginRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String,
        @Field("fcmToken") fcmToken: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.SIGNUP_URL)
    suspend fun registerRequest(
        @Field("name") name: String,
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String,
        @Field("deviceType") deviceType: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.UPDATE_PASSWORD_URL)
    suspend fun updatePasswordRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("password") password: String
    ): Response<LoginResponse>


    @FormUrlEncoded
    @POST(ApiEndPoint.RESEND_OTP_URL)
    suspend fun sendOtpRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.FORGOT_OTP_URL)
    suspend fun forgotOtpRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.VERIFY_ACCOUNT_URL)
    suspend fun otpVerifySignUpRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("otp") otp: String,
        @Field("fcmToken") fcmToken: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiEndPoint.VERIFY_OTP_URL)
    suspend fun otpVerifyRequest(
        @Field("emailPhone") emailOrPhone: String,
        @Field("otp") otp: String
    ): Response<LoginResponse>



    @POST(ApiEndPoint.USER_DETAILS_URL)
    suspend fun profileRequest(): Response<PersonalModel>

    @Multipart
    @POST(ApiEndPoint.COMPLETE_PERSONAL_URL)
    suspend fun updatePersonalRequest(
        @Part("full_name") nameBody: RequestBody,
        @Part("contact_number") phoneBody: RequestBody,
        @Part("email") emailBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ): Response<PersonalModel>

    @FormUrlEncoded
    @POST(ApiEndPoint.COMPLETE_GENERAL_URL)
    suspend fun  completeGeneralRequest(
        @Field("blood_group") bloodGroup: String,
        @Field("allergies[]") allergies: String,
        @Field("emergency_contact_name") contactName: String,
        @Field("emergency_contact_number") contactNumber :String
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("verify_email_phone")
    suspend fun verifyEmailPhoneRequest(
        @Field("emailPhone") emailOrPhone: String
    ): Response<GsonJsonObject>


    @FormUrlEncoded
    @POST("complete_general_profile_history")
    suspend fun completeGeneralProfileHistoryRequest(
        @Field("chronic_condition[]") chronicConditions: String,
        @Field("surgical_history") surgicalHistory: String,
        @Field("current_medications[]") currentMedication: String,
        @Field("current_supplements[]") currentSupplement : String
    ): Response<ProfileResponse>


    @Multipart
    @POST("complete_profile_documents")
    suspend fun completeProfileDocumentsRequest(
        @Part files: List<MultipartBody.Part>
    ): Response<ProfileResponse>



    @POST("onboarding_data")
    suspend fun onBoardingData() :Response<OnboardingResponse>

    @POST("get_user_details")
    suspend fun getUserDetails() : Response<GsonJsonObject>


    @Multipart
    @POST("update_profile_picture")
    suspend fun updateProfilePicture(
        @Part profile_image: MultipartBody.Part
    ) : Response<GsonJsonObject>


    @POST("faqs")
    suspend fun getFAQs() : Response<GsonJsonObject>

    @POST("privacy_policy")
    suspend fun getPrivacyPolicy() : Response<GsonJsonObject>

     @POST("terms_condition")
    suspend fun getTermsConditions() : Response<GsonJsonObject>


    @POST("account_privacy")
    suspend fun getAccountPrivacy() : Response<GsonJsonObject>

    @POST("account_privacy")
    suspend fun getAccountPrivacyPolicy() : Response<GsonJsonObject>

    @POST("help_support")
    suspend fun helpSupport() : Response<GsonJsonObject>
    @POST("about_us")
    suspend fun  aboutUs() : Response<GsonJsonObject>
}