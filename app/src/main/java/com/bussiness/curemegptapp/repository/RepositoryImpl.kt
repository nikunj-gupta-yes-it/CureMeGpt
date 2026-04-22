package com.bussiness.curemegptapp.repository

import android.net.Uri
import android.util.Log
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingItem
import com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingResponse
import com.bussiness.curemegptapp.apimodel.QuestionAnswer
import com.bussiness.curemegptapp.apimodel.chatModel.ChatHistoryItem
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.chatModel.PromptQuestionResponse
import com.bussiness.curemegptapp.apimodel.familyProfile.FamilyMemberResponse
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentData
import com.bussiness.curemegptapp.apimodel.getAppointmentList.AppointmentItem
import com.bussiness.curemegptapp.di.ApiService
import com.bussiness.curemegptapp.util.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginResponse
import com.bussiness.curemegptapp.apimodel.medication.MedicationItem
import com.bussiness.curemegptapp.apimodel.medication.MedicationItemDetail
import com.bussiness.curemegptapp.apimodel.personalmodel.PersonalModel
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.apimodel.personalmodel.User1
import com.bussiness.curemegptapp.apimodel.profilemodel.Data.UserProfile
import com.bussiness.curemegptapp.apimodel.profilemodel.DeleteMedicalDocRequest
import com.bussiness.curemegptapp.apimodel.profilemodel.UserProfileResponse
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.AppointmentTypeModel
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.ui.viewModel.main.Document
import com.bussiness.curemegptapp.ui.viewModel.main.FamilyMember
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.UriToRequestBody
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.flow.catch
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject
import kotlin.Int
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
        reminder: String,
        recommendedChatId:String?
    ): Flow<NetworkResult<String>> =  flow {
        try {
            val response = api.addScheduleAppointment(
            forWhomeId,appointmentTypeId,description,date,time,preferredDoctor,preferredClinic,reminder,recommendedChatId)

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
                            name = obj.getStringSafe("full_name"),
                            phone = obj.getStringSafe("contact_number"),
                            email = obj.getStringSafe("email_address"),
                            dob = obj.getStringSafe("dob"),
                            gender = obj.getStringSafe("gender"),
                            height = obj.getStringSafe("height"),
                            weight = obj.getStringSafe("weight"),
                            profile_photo = obj.getStringSafe("profile_photo")
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

    override fun getAppointmentList(): Flow<NetworkResult<List<AppointmentItem>>> = flow {
        try {
            val response = api.getAppointmentList()
            if(response.isSuccessful) {
                val respBody = response.body()

                if (respBody != null) {
                    if(respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        val listArray = dataObj.getAsJsonArray("data")

                        val type = object : TypeToken<List<AppointmentItem>>() {}.type
                        val appointmentList: List<AppointmentItem> =
                            Gson().fromJson(listArray, type)

                        emit(NetworkResult.Success(appointmentList))

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


    override fun getProfileDocuments(): Flow<NetworkResult<List<String>>> =flow{
        try {
            val response = api.getProfileDocuments()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        var result = listOf<String>()
                        val dataObject = respBody.get("data")?.asJsonObject
                        val dataArray = dataObject?.get("medical_documents")?.asJsonArray ?: return@flow
                        result = dataArray.map { element ->
                            element.asString
                        }
                        emit(NetworkResult.Success(result))

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
    override suspend fun updateGeneralProfileHistory(
        chronicConditions: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplement: String
    ): Flow<NetworkResult<String>>  = flow{
        try {
            val response = api.updateGeneralProfileHistory(
                chronicConditions, surgicalHistory, currentMedication, currentSupplement
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        emit(NetworkResult.Success("Profile history updated successfully"))

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


    override fun getGeneralProfileHistory(): Flow<NetworkResult<User1>> = flow {
        try {

            val response = api.getGeneralProfileHistory()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        fun JsonObject.getSafeString(key: String): String {
                            return if (has(key) && !get(key).isJsonNull) {
                                get(key).asString.trim()
                            } else {
                                ""
                            }
                        }

                        fun JsonObject.getSafeStringList(key: String): String {
                            return if (has(key) && get(key).isJsonArray) {
                                getAsJsonArray(key)
                                    .mapNotNull { element ->
                                        if (!element.isJsonNull) element.asString.trim() else null
                                    }
                                    .joinToString(", ")
                            } else {
                                ""
                            }
                        }

                        val profileResponse = User1(
                            blood_group = dataObj.getSafeString("blood_group"),
                            allergies = dataObj.getSafeStringList("known_allergies"),
                            emergency_contact_name = dataObj.getSafeString("emergency_contact_name"),
                            emergency_contact_number = dataObj.getSafeString("emergency_phone_number"),
                            chronic_condition = dataObj.getSafeStringList("chronic_condition"),
                            surgical_history = dataObj.getSafeString("surgical_history"),
                            current_medications = dataObj.getSafeStringList("current_medications"),
                            current_supplements = dataObj.getSafeStringList("current_supplements")
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

    override fun updateGeneralProfile(
        bloodGroup: String,
        allergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.completeGeneralProfile(
                bloodGroup,allergies,contactName,contactNumber
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        emit(NetworkResult.Success("General profile completed successfully"))

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

    override fun getGeneralProfile(): Flow<NetworkResult<User1>>  = flow{
        try {

            val response = api.getGeneralProfile()

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObj = respBody.getAsJsonObject("data")

                        val allergiesList = if (dataObj.has("known_allergies") &&
                            dataObj.get("known_allergies").isJsonArray) {

                            dataObj.getAsJsonArray("known_allergies")
                                .mapNotNull { element ->
                                    if (!element.isJsonNull) element.asString.trim() else null
                                }

                        } else {
                            emptyList()
                        }

                        val allergiesString = allergiesList.joinToString(", ")

                        fun getSafeString(obj: JsonObject, key: String): String {
                            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                                try {
                                    obj.get(key).asString.trim()
                                } catch (e: Exception) {
                                    ""
                                }
                            } else {
                                ""
                            }
                        }

                        val profileResponse = User1(
                            blood_group = getSafeString(dataObj, "blood_group"),
                            allergies = allergiesString,
                            emergency_contact_name = getSafeString(dataObj, "emergency_contact_name"),
                            emergency_contact_number = getSafeString(dataObj, "emergency_phone_number")
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

    override fun completeProfileDocuments(files: List<MultipartBody.Part>): Flow<NetworkResult<String>>
            = flow {
        try {
            val response = api.completeProfileDocuments(files)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
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

    override fun updateProfileDocuments(files: List<MultipartBody.Part>): Flow<NetworkResult<String>> = flow{
        try {
            val response = api.updateProfileDocuments(files)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
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

    override fun getScheduleAppointmentDetails(appointmentId: String): Flow<NetworkResult<AppointmentData>> = flow {
        try {
            val response = api.getScheduleAppointmentDetails(appointmentId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                      //  emit(NetworkResult.Success("Profile Updated successfully"))
                        val dataObj = respBody.getAsJsonObject("data")
                        val innerDataObj = dataObj.getAsJsonObject("data")
                        val appointmentData = Gson().fromJson(innerDataObj, AppointmentData::class.java)
                        emit(NetworkResult.Success(appointmentData))
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

    override fun rescheduleAppointment(
        appointmentId: Int,
        date: String,
        time: String,
        reminder: String
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.rescheduleAppointment(appointmentId,date,time,reminder)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
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

    override fun markAppointmentComplete(id: Int): Flow<NetworkResult<String>> = flow{
        try {
            val response = api.markAppointmentComplete(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Profile Updated successfully"))
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

    override fun deleteAppointment(id: Int): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.deleteAppointment(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Deleted successfully"))
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

    override fun addMedication(
        forWhomId: RequestBody?,
        medicationType: RequestBody,
        medicationName: RequestBody,
        dosage: RequestBody,
        frequency: RequestBody,
        days: RequestBody?,
        startDate: RequestBody,
        endDate: RequestBody,
        notes: RequestBody,
        reminderStatus: RequestBody,
        reminderTimes: List<MultipartBody.Part>,
        prescriptionDocs: MultipartBody.Part?
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.addMedication(
                forWhomId,medicationType,medicationName,dosage,frequency,days,startDate,endDate,notes,reminderStatus,
                reminderTimes,prescriptionDocs)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

    override fun getMedicationList(): Flow<NetworkResult<MutableList<MedicationItem>>> = flow {

        emit(NetworkResult.Loading())

        try {

            val response = api.getMedicationList()

            if (response.isSuccessful) {
               Log.d("TESTING_MEDICATION","Here inside the medicationlist")
                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        Log.d("TESTING_MEDICATION","Success Here inside the medicationlist")

                        if (respBody.has("data") && !respBody.get("data").isJsonNull) {

                            val dataObject = respBody.getAsJsonObject("data")

                            if (dataObject.has("data") && !dataObject.get("data").isJsonNull) {

                                val dataArray = dataObject.getAsJsonArray("data")

                                val type =
                                    object : TypeToken<MutableList<MedicationItem>>() {}.type

                                val medicationList: MutableList<MedicationItem> =
                                    Gson().fromJson(dataArray, type)

                                emit(NetworkResult.Success(medicationList))

                            } else {
                                emit(NetworkResult.Success(mutableListOf()))
                            }

                        } else {
                            emit(NetworkResult.Success(mutableListOf()))
                        }

                    }
                    else {
                        Log.d("TESTING_MEDICATION","Error Here inside the medicationlist")

                        val message = if (respBody.has("message"))
                            respBody.get("message").asString
                        else
                            AppConstant.serverError

                        emit(NetworkResult.Error(message))
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

    override fun getMedicationDetails(medicationId: Int): Flow<NetworkResult<MedicationItemDetail>> = flow {
        emit(NetworkResult.Loading())

        try {
            val response = api.getMedicationDetails(medicationId)
            if (response.isSuccessful) {
                Log.d("TESTING_MEDICATION","Here inside the medicationlist")

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        Log.d("TESTING_MEDICATION","Success Here inside the medicationlist")

                        if (respBody.has("data") && !respBody.get("data").isJsonNull) {

                            val dataObject = respBody.getAsJsonObject("data")

                            if (dataObject.has("data") && !dataObject.get("data").isJsonNull) {
                                val response = dataObject.get("data").asJsonObject
                                val medicationItem = Gson().fromJson(response, MedicationItemDetail::class.java)
                                emit(NetworkResult.Success(medicationItem))
                            } else {
                                emit(NetworkResult.Error("Details Not Found"))
                            }
                        } else {
                            emit(NetworkResult.Error("Details Not Found"))
                        }

                    }
                    else {
                        Log.d("TESTING_MEDICATION","Error Here inside the medicationlist")

                        val message = if (respBody.has("message"))
                            respBody.get("message").asString
                        else
                            AppConstant.serverError

                        emit(NetworkResult.Error(message))
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

    override fun deleteMedication(medicationId: Int): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.deleteMedication(medicationId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success("Deleted successfully"))
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

    override fun updateMedication(
        medicationId: RequestBody?,
        forWhomId: RequestBody?,
        medicationType: RequestBody,
        medicationName: RequestBody,
        dosage: RequestBody,
        frequency: RequestBody,
        days: RequestBody?,
        startDate: RequestBody,
        endDate: RequestBody,
        notes: RequestBody,
        reminderStatus: RequestBody,
        reminderTimes: List<MultipartBody.Part>,
        prescriptionDocs: MultipartBody.Part?
    ) : Flow<NetworkResult<String>> = flow{
        try {
            val response = api.updateMedication(medicationId,
                forWhomId,medicationType,medicationName,
                dosage,frequency,days, startDate,endDate,notes,
                reminderStatus, reminderTimes,prescriptionDocs)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

     override  fun addFamilyMemberPersonal(
        relation: RequestBody,
        nameBody: RequestBody,
        phoneBody: RequestBody,
        emailBody: RequestBody,
        dobBody: RequestBody,
        genderBody: RequestBody,
        heightBody: RequestBody,
        weightBody: RequestBody,
        profile_image: MultipartBody.Part?
    ): Flow<NetworkResult<Int>> =flow{
        try {
            val response = api.addFamilyMemberPersonal(relation, nameBody,phoneBody,emailBody,dobBody,
                genderBody,heightBody,weightBody,profile_image)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val dataInner = data.get("data").asJsonObject
                        val id =   dataInner.get("id").asInt

                        emit(NetworkResult.Success(id))
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

    override fun addFamilyMemberGeneral(
        familyMemberId: Int,
        bloodGroup: String,
        knownAllergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> = flow{
        try {

            val response = api.addFamilyMemberGeneral(
                familyMemberId, bloodGroup, knownAllergies,
                contactName, contactNumber
                )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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



    override fun addFamilyMemberHistory(
        familyMemeberId: Int,
        chronicCondition: String,
        surgicalHistory: String,
        currentMedication: String,
        currentSupplements: String
    ): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.addFamilyMemberHistory(
                familyMemeberId,chronicCondition,surgicalHistory,currentMedication,currentSupplements
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

    override fun addFamilyMemberMedicalDocuments(
        familyMemeberId: RequestBody,
        profile_image: MutableList<MultipartBody.Part?>
    ): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.addFamilyMemberMedicalDocuments(familyMemeberId,profile_image)

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

    override fun getFamilyMemberProfile(id: Int): Flow<NetworkResult<ProfileData>> =flow{
        try {
            val response = api.getFamilyMemberProfile(id)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data1 = respBody.getAsJsonObject("data")
                        val data = data1.get("data").asJsonObject
                        val uriList: List<Uri> = data.getAsJsonArray("medical_documents")
                            ?.mapNotNull { element ->
                                element.takeIf { !it.isJsonNull }?.asString
                            }
                            ?.mapNotNull { runCatching { Uri.parse(it) }.getOrNull() }
                            ?: emptyList()
                        val profile = ProfileData(
                            id = data.get("id")?.takeIf { !it.isJsonNull }?.asInt ?: 0,

                            fullName = data.get("full_name")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            contactNumber = data.get("contact_number")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            email = data.get("email")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            dateOfBirth = data.get("dob")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            relation = data.get("relation")?.takeIf { !it.isJsonNull }?.asString ?: "father",

                            gender = data.get("gender")?.takeIf { !it.isJsonNull }?.asString ?: "Select",

                            height = data.get("height")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            weight = data.get("weight")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            profileImage = data.get("profile_image")?.takeIf { !it.isJsonNull }?.asString ?: "",

                            bloodGroup = data.get("blood_group")?.takeIf { !it.isJsonNull }?.asString ?: "Select",

                            allergies = data.getAsJsonArray("allergies")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            emergencyContactName = data.get("emergency_contact_name")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            emergencyContactPhone = data.get("emergency_contact_number")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            chronicConditions = data.getAsJsonArray("chronic_condition")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            surgicalHistory = data.get("surgical_history")
                                ?.takeIf { !it.isJsonNull }?.asString ?: "",

                            currentMedications = data.getAsJsonArray("current_medications")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            currentSupplements = data.getAsJsonArray("current_supplements")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString?.trim() }
                                ?: emptyList(),

                            uploadedDocument = data.getAsJsonArray("medical_documents")
                                ?.mapNotNull { it.takeIf { !it.isJsonNull }?.asString }
                                ?: emptyList(),
                            uploadedFiles = uriList
                        )

                        emit(NetworkResult.Success(profile))
                    }
                    else {
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

   override fun updateFamilyPersonalProfile(
        @Part("family_member_id") familyMemberBody : RequestBody,
        @Part("fullName") nameBody: RequestBody,
        @Part("contactNumber") phoneBody: RequestBody,
        @Part("emailAddress") emailBody: RequestBody,
        @Part("dateOfBirth") dobBody: RequestBody,
        @Part("gender") genderBody: RequestBody,
        @Part("height") heightBody: RequestBody,
        @Part("weight") weightBody: RequestBody,
        @Part profile_image:  MultipartBody.Part?
    ) : Flow<NetworkResult<String>> =flow{
        try {
            val response = api.updateFamilyPersonalProfile(
            familyMemberBody,nameBody,phoneBody,emailBody,dobBody,genderBody,heightBody,weightBody,profile_image
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

    override fun updateFamilyGeneralProfile(
        familyMemberId: Int,
        bloodGroup: String,
        knownAllergies: String,
        contactName: String,
        contactNumber: String
    ): Flow<NetworkResult<String>> =flow{
        try {

            val response = api.updateFamilyGeneralProfile(
                familyMemberId, bloodGroup, knownAllergies,
                contactName, contactNumber
            )

            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

   override fun updateFamilyHistoryProfile(
        familyMemberId :Int,
        chronicCondition :String,
        surgicalHistory :String,
        currentMediation :String,
        supplement :String
    ): Flow<NetworkResult<String>> = flow{
       try {
           val response = api.updateFamilyHistoryProfile(
               familyMemberId, chronicCondition, surgicalHistory,
               currentMediation, supplement
           )
           if (response.isSuccessful) {
               val respBody = response.body()
               if (respBody != null) {
                   if (respBody.has("success") && respBody.get("success").asBoolean) {
                       emit(NetworkResult.Success(respBody.get("message").asString))
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

    override fun updateFamilyMedicalDocuments(
        id: RequestBody,
        profile_image: MutableList<MultipartBody.Part?>
    ) : Flow<NetworkResult<String>> = flow {
      try {
          val response = api.updateFamilyMedicalDocuments(id,profile_image)
          if (response.isSuccessful) {
              val respBody = response.body()
              if (respBody != null) {
                  if (respBody.has("success") && respBody.get("success").asBoolean) {
                      emit(NetworkResult.Success(respBody.get("message").asString))
                  } else {
                      emit(NetworkResult.Error(respBody.get("message").asString))
                  }
              } else {
                  emit(NetworkResult.Error(AppConstant.serverError))
              }
          } else {
              emit(NetworkResult.Error(AppConstant.serverError))
          }
      }
      catch (e: Exception){
          e.printStackTrace()
          emit(NetworkResult.Error(AppConstant.serverError))
      }

    }

    override suspend fun familyMemberList(): Flow<NetworkResult<FamilyMemberResponse>> = flow{
        try {
            val response = api.familyMemberList()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                           val data = respBody.get("data").asJsonObject
                        val familyProfile = Gson().fromJson(data, FamilyMemberResponse::class.java)

                        emit(NetworkResult.Success(familyProfile))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getFamilyMemberProfileDetails(familyMemberId: Int): Flow<NetworkResult<FamilyMember>> = flow {
        try{
            val response = api.getFamilyMemberProfileDetails(familyMemberId)
            if(response.isSuccessful){
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data1 = respBody.get("data").asJsonObject
                        val json = data1.get("data").asJsonObject
                        val allergies = json.getArraySafe("allergies")
                        val chronic = json.getArraySafe("chronic_condition")
                        val meds = json.getArraySafe("current_medications")
                        val supplements = json.getArraySafe("current_supplements")
                        val docs = json.getArraySafe("medical_documents")
                        val member = FamilyMember(
                            id = json.getStringSafe("id"),
                            name = json.getStringSafe("full_name"),
                            profileImage = json.getStringSafe("profile_image").let {
                                if (!it.isNullOrEmpty()) AppConstant.IMAGE_BASE_URL+it else ""
                            },
                            contactNumber = json.getStringSafe("contact_number", "--"),
                            email = json.getStringSafe("email", "--"),
                            relation = json.getStringSafe("relationship", "--")
                                .replaceFirstChar { it.uppercase() },
                            dateOfBirth = json.getStringSafe("dob"),
                            gender = json.getStringSafe("gender", "--"),
                            height = json.getStringSafe("height"),
                            weight = json.getStringSafe("weight"),
                            bloodGroup = json.getStringSafe("blood_group", "--"),
                            allergies = allergies.toSafeString(),
                            emergencyContact = json.getStringSafe("emergency_contact_name", "--"),
                            emergencyPhone = json.getStringSafe("emergency_contact_number", "--"),
                            chronicConditions = chronic.toSafeString(),
                            surgicalHistory = json.getStringSafe("surgical_history", "--"),
                            currentMedications = meds.toSafeList(),
                            currentSupplements = supplements.toSafeList(),
                            documents = docs.toSafeDocuments()
                        )
                        emit(NetworkResult.Success(member))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }else{
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun deleteFamilyMember(familyMemberId: Int): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.deleteFamilyMember(familyMemberId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
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

    override suspend fun getUserWithFamilyDetails(): Flow<NetworkResult<MutableList<FamilyModel>>>  = flow{
        try {
            val response = api.getUserWithFamilyDetails()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val list = mutableListOf<FamilyModel>()

                        val data = respBody.getAsJsonObject("data")

                        // 🔹 USER (Myself)
                        val userObj = if (data.has("userDetails") && data.get("userDetails").isJsonObject) {
                            data.getAsJsonObject("userDetails")
                        } else null

                        userObj?.let {
                            list.add(
                                FamilyModel(
                                    id = it.get("id")?.asInt ?: 0,
                                    name = it.getStringSafe("name"),
                                    relationship = "Myself",
                                    profilePhoto = ""
                                )
                            )
                        }

                        // 🔹 FAMILY LIST
                        val familyArray = data?.getArraySafe("familyDetails")

                        familyArray?.forEach { element ->
                            if (element.isJsonNull) return@forEach
                            val obj = element.asJsonObject
                            list.add(
                                FamilyModel(
                                    id = obj.get("id")?.asInt ?: 0,
                                    name = obj.getStringSafe("name"),
                                    relationship = obj.getStringSafe("relationship"),
                                    profilePhoto = obj.getStringSafe("profile_photo")
                                )
                            )
                        }

                        emit(NetworkResult.Success(list))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getChatResponse(
        familyMemberId: RequestBody?,
        message: RequestBody,
        type: RequestBody,
        chatId: RequestBody?,
        profile_image: MultipartBody.Part?
    ): Flow<NetworkResult<ChatMessage>> = flow {

        try {

            val response = api.getChatResponse(
                familyMemberId,
                message,
                type,
                chatId,
                profile_image
            )

            if (response.isSuccessful) {

                val respBody = response.body()

                if (respBody != null) {

                    if (respBody.has("success") && respBody.get("success").asBoolean) {

                        val dataObj = respBody.getAsJsonObject("data")

                        if (dataObj != null) {

                            val messageId = dataObj.get("message_id")?.asString
                                ?: java.util.UUID.randomUUID().toString()

                            val chatIdValue = dataObj.get("chat_id")?.asInt ?: 0

                            val messageText = dataObj.get("message")?.asString

                            val actionRequired = dataObj.getAsJsonObject("meta")
                                ?.get("action_required")
                                ?.asBoolean ?: false

                            val chatMessage = ChatMessage(
                                id = messageId,
                                chatId = chatIdValue,
                                text = messageText,
                                severity = actionRequired,
                                isUser = false
                            )

                            emit(NetworkResult.Success(chatMessage))

                        } else {
                            emit(NetworkResult.Error("Data object not found"))
                        }
                    } else {
                        val errorMsg = respBody.get("message")?.asString
                            ?: AppConstant.serverError
                        emit(NetworkResult.Error(errorMsg))
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

    override suspend fun getPromptQuestions(): Flow<NetworkResult<PromptQuestionResponse>> = flow {
        try {
            val response = api.getPromptQuestions()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        if (dataObj != null ) {
                            val promptQuestion = Gson().fromJson(dataObj, PromptQuestionResponse::class.java)
                            val userDetails = dataObj.get("userDetails").asJsonObject
                            val family = FamilyDetails(
                                id = userDetails.get("id").asInt,
                                name = userDetails.get("name").asString,
                                relationship = "Myself",
                                profile_photo = null
                            )
                            val updatedFamilyList = listOf(family) + promptQuestion.family_details
                            val updatedResponse = promptQuestion.copy(
                                family_details = updatedFamilyList
                            )
                            emit(NetworkResult.Success(updatedResponse))
                        }
                        else {
                              emit(NetworkResult.Error("Message not found in data"))
                        }

                        //  emit(NetworkResult.Success(respBody.get("message").asString))
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

    override suspend fun getUserChatHistoryList(): Flow<NetworkResult<MutableList<ChatHistoryItem>>> = flow {
        try {
            val response = api.getUserChatList()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val dataObj = respBody.getAsJsonObject("data")
                        if (dataObj != null ) {
                            val chatHistoryList = dataObj.getAsJsonArray("data")
                             val historyList = mutableListOf<ChatHistoryItem>()

                            chatHistoryList.forEach { element ->
                                try {
                                    val obj = element.asJsonObject
                                    val item = Gson().fromJson(obj, ChatHistoryItem::class.java)

                                    if (item != null) {
                                        historyList.add(item)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        historyList.reverse()

                            emit(NetworkResult.Success(historyList))
                        }
                        else {
                            emit(NetworkResult.Error("Message not found in data"))
                        }

                        //  emit(NetworkResult.Success(respBody.get("message").asString))
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

    override suspend fun renameChat(
        chatId: Int,
        title: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            val response = api.renameChat(chatId,title)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun deleteChat(chatId: Int): Flow<NetworkResult<String>> = flow{
        try {
            val response = api.deleteChat(chatId)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.get("message").asString))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }


    fun JsonObject.getStringSafe(key: String, default: String = ""): String {
        return if (has(key) && !get(key).isJsonNull) {
            get(key).asString
        } else default
    }

    fun JsonObject.getArraySafe(key: String): JsonArray? {
        return if (has(key) && get(key).isJsonArray) {
            getAsJsonArray(key)
        } else null
    }

    fun JsonArray?.toSafeString(): String {
        if (this == null || size() == 0) return "--"

        return mapNotNull {
            if (!it.isJsonNull) it.asString else null
        }.takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?: "--"
    }

    fun JsonArray?.toSafeList(): List<String> {
        if (this == null || size() == 0) return listOf("--")

        val list = mapNotNull {
            if (!it.isJsonNull) it.asString else null
        }

        return if (list.isEmpty()) listOf("--") else list
    }

    fun JsonArray?.toSafeDocuments(): List<Document> {
        if (this == null) return emptyList()

        return mapIndexedNotNull { index, element ->
            if (element.isJsonNull) return@mapIndexedNotNull null

            val url = element.asString

            Document(
                id = "doc$index",
                fileName = url.substringAfterLast("/", "file_$index"),
                fileUrl = "https://your-base-url.com/$url",
                fileType = url.substringAfterLast(".", "jpg")
            )
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



