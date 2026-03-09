package com.bussiness.curemegptapp.repository

import android.util.Log
import com.bussiness.curemegptapp.di.ApiService
import com.bussiness.curemegptapp.util.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.util.AppConstant
import com.google.gson.Gson
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.get

class RepositoryImpl @Inject constructor(
    private val api: ApiService
) : Repository {


    override fun loginRequest(
        emailOrPhone: String,
        password: String,
        fcmToken: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.loginRequest(emailOrPhone, password,fcmToken) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun registerRequest(
        name: String,
        emailOrPhone: String,
        password: String,
        deviceType: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.registerRequest(name,emailOrPhone, password,deviceType) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun updatePasswordRequest(
        emailOrPhone: String,
        password: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.updatePasswordRequest(emailOrPhone, password) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun sendOtpRequest(
        emailOrPhone: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.sendOtpRequest(emailOrPhone) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun forgotOtpRequest(
        emailOrPhone: String
    ): Flow<Resource<LoginResponse>> = flow {

        emit(Resource.Loading)

        Log.d("TESTING", "Forgot OTP Request: emailOrPhone=$emailOrPhone")

        val result = safeApiCall { api.forgotOtpRequest(emailOrPhone) }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override fun otpVerifyRequest(
        emailOrPhone: String,
        otp: String,
        fcmToken: String,
        fromScreen: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = if (fromScreen.equals("create", true)) {
            safeApiCall { api.otpVerifySignUpRequest(emailOrPhone, otp, fcmToken) }
        } else {
            safeApiCall { api.otpVerifyRequest(emailOrPhone, otp) }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun profileRequest(): Flow<Resource<PersonalModel>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.profileRequest() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun updatePersonalRequest( name: String, phone: String, email: String, dob: String, gender: String,
                                        height: String, heightType: String, weight: String, weightType: String)
               : Flow<Resource<PersonalModel>> = flow {

                   emit(Resource.Loading)

        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightBody = "$height$heightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val weightBody = "$weight$weightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val result = safeApiCall { api.updatePersonalRequest(nameBody,phoneBody,emailBody,
            dobBody,genderBody,heightBody,weightBody)
        }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override fun generalProfileRequest(
        bloodGroup: String,
        allergies: String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ): Flow<Resource<ProfileResponse>> =flow{

        emit(Resource.Loading)
        val result = safeApiCall {
            api.completeGeneralRequest(bloodGroup, allergies, emergencyContactName, emergencyContactPhone)
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun verifyEmailPhoneRequest(emailOrPhone: String) :Flow<NetworkResult<String>> = flow{
        try {
            val response = api.verifyEmailPhoneRequest(emailOrPhone)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                          val otp = data.get("otp").asInt
                        emit(NetworkResult.Success(otp.toString()))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }


    private suspend fun <T : BaseResponse> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return try {
            val response = apiCall()
            when {
                !response.isSuccessful ->
                    Resource.Error("Server error ${response.code()}")
                response.body() == null ->
                    Resource.Error(Messages.RESPONSE_BODY_ERROR)
                response.body()?.success == false ->
                    Resource.Error(response.body()?.message ?: "Operation failed")
                else ->
                    Resource.Success(response.body()!!)
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Network call failed", e)
            Resource.Error(e.localizedMessage ?: "Something went wrong")
        }
    }





}