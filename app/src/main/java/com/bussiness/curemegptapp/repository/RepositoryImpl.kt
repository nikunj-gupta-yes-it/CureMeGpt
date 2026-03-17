package com.bussiness.curemegptapp.repository

import android.util.Log
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
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
import com.bussiness.curemegptapp.apimodel.personalmodel.User1
import com.bussiness.curemegptapp.apimodel.profilemodel.Data.UserProfile
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Part
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
        val result = safeApiCall { api.loginRequest(emailOrPhone, password, fcmToken) }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun registerRequest(
        name: String,
        emailOrPhone: String,
        password: String,
        deviceType: String
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.registerRequest(name, emailOrPhone, password, deviceType) }
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

    override fun updatePersonalRequest(
        name: String, phone: String, email: String, dob: String, gender: String,
        height: String, heightType: String, weight: String, weightType: String,
        profileImage: MultipartBody.Part?
    )
            : Flow<Resource<PersonalModel>> = flow {

        emit(Resource.Loading)

        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightBody = "$height$heightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val weightBody = "$weight$weightType".toRequestBody("text/plain".toMediaTypeOrNull())
        val result = safeApiCall {
            api.updatePersonalRequest(
                nameBody, phoneBody, emailBody,
                dobBody, genderBody, heightBody, weightBody, profileImage
            )
        }

        emit(result)

    }.flowOn(Dispatchers.IO)

    override fun generalProfileRequest(
        bloodGroup: String,
        allergies: String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ): Flow<Resource<ProfileResponse>> = flow {

        emit(Resource.Loading)
        val result = safeApiCall {
            api.completeGeneralRequest(
                bloodGroup,
                allergies,
                emergencyContactName,
                emergencyContactPhone
            )
        }
        emit(result)
    }.flowOn(Dispatchers.IO)


    override fun verifyEmailPhoneRequest(emailOrPhone: String): Flow<NetworkResult<String>> = flow {
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

    override suspend fun completeGeneralProfileHistoryRequest(
        chronicConditions: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplement: String
    ): Flow<Resource<ProfileResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            api.completeGeneralProfileHistoryRequest(
                chronicConditions, surgicalHistory, currentMedication,
                currentSupplement
            )
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun completeProfileDocumentsRequest(files: List<MultipartBody.Part>): Flow<Resource<ProfileResponse>> =
        flow {

            emit(Resource.Loading)
            val result = safeApiCall { api.completeProfileDocumentsRequest(files) }
            emit(result)
        }.flowOn(Dispatchers.IO)

    override suspend fun onBoardingData(): Flow<Resource<OnboardingResponse>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { api.onBoardingData() }
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserDetails(): Flow<NetworkResult<UserProfile>> = flow {
        try {
            val response = api.getUserDetails()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val userObj = data.get("user").asJsonObject
                        val userProfile = Gson().fromJson(userObj, UserProfile::class.java)
                        emit(NetworkResult.Success(userProfile))
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

    override fun updateProfilePicture(profile_image: MultipartBody.Part): Flow<NetworkResult<String>> =
        flow {
            try {
                val response = api.updateProfilePicture(profile_image)
                if (response.isSuccessful) {
                    val respBody = response.body()
                    if (respBody != null) {
                        if (respBody.has("success") && respBody.get("success").asBoolean) {

                            emit(NetworkResult.Success("Profile picture updated successfully"))
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

    override fun getFAQ(): Flow<NetworkResult<List<QuestionAnswer>>> = flow {

        try {
            val response = api.getFAQs()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("data")?.asJsonArray ?: return@flow
                        val faqList = dataArray.map { element ->
                            Gson().fromJson(element, QuestionAnswer::class.java)
                        }
                        emit(NetworkResult.Success(faqList))
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

    override fun getPrivacyPolicy(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getPrivacyPolicy()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
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

    override fun getTermsAndConditions(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getTermsConditions()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
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

    override fun getAccountPrivacyPolicy(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.getAccountPrivacyPolicy()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
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

    override fun helpSupport(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.helpSupport()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
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

    override fun aboutUs(): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.aboutUs()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("data")?.asJsonObject
                        val secondData = dataObject?.get("data")?.asJsonObject
                        val content = secondData?.get("content")?.asString ?: ""
                        emit(NetworkResult.Success(content))
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


    override fun getAppointmentType(): Flow<NetworkResult<List<AppointmentTypeModel>>> =
        flow {

            val response = api.getAppointmentType()

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("data")?.asJsonArray ?: return@flow

                        val appointmentTypeList = dataArray.map { element ->
                            Gson().fromJson(element, AppointmentTypeModel::class.java)
                        }

                        emit(NetworkResult.Success(appointmentTypeList))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        }.catch { e ->

            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))

        }

    override fun getFamilyMembersList(): Flow<NetworkResult<List<FamilyModel>>> =
        flow {

            val response = api.getFamilyMembersList()

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("people")?.asJsonArray ?: return@flow

                        val familyList = dataArray.map { element ->
                            Gson().fromJson(element, FamilyModel::class.java)
                        }

                        emit(NetworkResult.Success(familyList))

                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }

                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }

            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }

        }.catch { e ->

            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))

        }

    override fun addScheduleAppointment(
        forWhomeId: String?,
        appointmentTypeId: String,
        description: String,
        date: String,
        time: String,
        preferredDoctor: String,
        preferredClinic: String,
        reminder: String
    ): Flow<NetworkResult<String>> =  flow {
        try {
            val response = api.addScheduleAppointment(
            forWhomeId,appointmentTypeId,description,date,time,preferredDoctor,preferredClinic,reminder)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObject = respBody.get("message").asString
                        emit(NetworkResult.Success(dataObject))
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

    override fun getPersonalProfile(): Flow<NetworkResult<User1>> = flow {
        try {

            val response = api.getPersonalProfile()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val obj = respBody.get("data").asJsonObject

                        val profileResponse = User1(
                            id = obj.get("id")?.asInt ?: 0,
                            name = obj.get("full_name")?.asString ?: "",
                            phone = obj.get("contact_number")?.asString ?: "",
                            email = obj.get("email_address")?.asString ?: "",
                            dob = obj.get("dob")?.asString ?: "",
                            gender = obj.get("gender")?.asString ?: "",
                            height = obj.get("height")?.asString ?: "",
                            weight = obj.get("weight")?.asString ?: "",
                            profile_photo = obj.get("profile_photo")?.asString ?: ""
                        )
                         emit(NetworkResult.Success(profileResponse))

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

    override fun updatePersonalProfile(
        name: String,
        phone: String,
        email: String,
        dob: String,
        gender: String,
        height: String,
        weight: String,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<String>> = flow {
        try {

            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val dobBody = dob.toRequestBody("text/plain".toMediaTypeOrNull())
            val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
            val heightBody = height.toRequestBody("text/plain".toMediaTypeOrNull())
            val weightBody = weight.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.updatePersonalProfile(
                nameBody,phoneBody,emailBody,dobBody,genderBody,heightBody,weightBody,
                profileImage
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        emit(NetworkResult.Success("Profile updated successfully"))

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



