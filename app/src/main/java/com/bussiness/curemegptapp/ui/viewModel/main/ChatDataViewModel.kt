package com.bussiness.curemegptapp.ui.viewModel.main

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri

import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.PdfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.ChatScreenArgs
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


data class ChatInputState1(
    val message: String = "",
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList(),
    val isRecording: Boolean = false,
    val showVoicePreview: Boolean = false,
    val transcribedText: String = "",
    val selectedProfile: Profile? = null,
    val showProfileDropdown: Boolean = false
)

@HiltViewModel
class ChatDataViewModel @Inject constructor(
    private val app: Application,
    val repository: Repository
) : ViewModel() {

    private lateinit var context: Context

    private val _chatArgs = MutableStateFlow(ChatScreenArgs())
    val chatArgs: StateFlow<ChatScreenArgs> = _chatArgs

    private val _uiState = MutableStateFlow(ChatInputState1())
    val uiState: StateFlow<ChatInputState1> = _uiState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages


    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    // For tracking message being edited
    private var editingMessageId: String? = null

    init {

        _profiles.value = getDefaultProfiles()

        if (_profiles.value.isNotEmpty()) {
            _uiState.update { it.copy(selectedProfile = _profiles.value.first()) }
        }

        //  getFamilyMembers()

    }


    fun setChatArgs(
        context: Context,
        chatId: Int,
        familyMemberId: Int,
        chatMessage: ChatMessage?,
        type: String,
        familyList: List<FamilyDetails>
    ) {
        this.context = context

        _chatArgs.value = ChatScreenArgs(
            chatId = chatId,
            familyMemberId = familyMemberId,
            chatMessage = chatMessage,
            type = type,
            familyList = familyList
        )
    }

    private fun getDefaultProfiles(): List<Profile> {
        return listOf(
            Profile(
                id = "general",
                name = "General Assistant",
                iconResId = R.drawable.ic_profile,
                description = "General purpose AI assistant"
            ),

            Profile(
                id = "creative",
                name = "Creative Writer",
                iconResId = R.drawable.ic_profile,
                description = "For creative writing and ideas"
            ),

            Profile(
                id = "technical",
                name = "Technical Expert",
                iconResId = R.drawable.ic_profile,
                description = "Technical and coding assistance"
            ),

            Profile(
                id = "academic",
                name = "Academic Helper",
                iconResId = R.drawable.ic_profile,
                description = "Academic writing and research"
            )

        )
    }

    fun toggleProfileDropdown() {
        _uiState.update { it.copy(showProfileDropdown = !it.showProfileDropdown) }
    }

    fun selectProfile(profile: Profile) {
        _uiState.update { it.copy(selectedProfile = profile, showProfileDropdown = false) }
    }

    fun onMessageChange(newText: String) {
        _uiState.update { it.copy(message = newText.take(1000)) }
    }

    fun addImage(uri: Uri) {
        _uiState.update { it.copy(images = it.images + uri) }
    }


    fun clearImages() {
        _uiState.update { it.copy(images = emptyList()) }
    }

    fun clearPdfs() {
        _uiState.update { it.copy(pdfs = emptyList()) }
    }

    fun removeImage(uri: Uri) {
        _uiState.update { it.copy(images = it.images - uri) }
    }

    fun addPdf(uri: Uri) {
        val name = getPdfName(uri)
        _uiState.update { it.copy(pdfs = it.pdfs + PdfData(uri, name)) }
    }

    fun removePdf(pdf: PdfData) {
        _uiState.update { it.copy(pdfs = it.pdfs - pdf) }
    }

    fun toggleRecording() {
        _uiState.update { it.copy(isRecording = !it.isRecording) }
    }


    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false) }
    }

    fun sendMessageFromInput() {
        val s = _uiState.value
        if (s.message.isBlank() && s.images.isEmpty() && s.pdfs.isEmpty()) return

        // If we're editing a message, update it
        editingMessageId?.let { messageId ->
            updateMessage(messageId, s.message)
            editingMessageId = null
            _uiState.update { ChatInputState1(selectedProfile = s.selectedProfile) }
            return
        }


        val msg = ChatMessage(
            text = s.message.takeIf { it.isNotBlank() },
            isUser = true,
            images = s.images,
            pdfs = s.pdfs
        )

        _messages.update { it + msg }


        _uiState.update { ChatInputState1(selectedProfile = s.selectedProfile) }


            sendInitialChatRequest(
                s.message.toString(),
                chatId = _chatArgs.value.chatId,
                familyMemberId = _chatArgs.value.familyMemberId,
                type = _chatArgs.value.type,
                images = s.images,
                pdfs = s.pdfs
            )


    }

    fun copyMessage(text: String) {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Chat message", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(app, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun editMessage(messageId: String) {
        val message = _messages.value.find { it.id == messageId }
        message?.let { msg ->
            editingMessageId = messageId
            _uiState.update {
                it.copy(
                    message = msg.text ?: "",
                    images = msg.images,
                    pdfs = msg.pdfs
                )
            }

            Toast.makeText(app, "Editing message...", Toast.LENGTH_SHORT).show()
        }
    }

    fun regenerateMessage(messageId: String) {
        viewModelScope.launch {
            val index = _messages.value.indexOfLast { it.id == messageId }
            if (index != -1) {
                // Show loading indicator
                _messages.update {
                    it.toMutableList().apply {
                        this[index] = this[index].copy(isGenerating = true)
                    }
                }

                delay(1500)

                _messages.update {
                    it.toMutableList().apply {
                        this[index] = this[index].copy(
                            text = "Here's a regenerated response based on your query.",
                            isGenerating = false
                        )
                    }
                }

                Toast.makeText(app, "Message regenerated", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun rateMessage(messageId: String, isPositive: Boolean) {
        viewModelScope.launch {
            val index = _messages.value.indexOfFirst { it.id == messageId }
            if (index != -1) {
                val current = _messages.value[index]
                val newRating =
                    if (current.rating == 1 && isPositive) null
                    else if (current.rating == -1 && !isPositive) null
                    else if (isPositive) 1 else -1

                _messages.update {
                    it.toMutableList().apply {
                        this[index] = current.copy(
                            rating = newRating ?: 0,
                            isRated = newRating != null
                        )
                    }
                }
            }
        }
    }


    private fun updateMessage(messageId: String, newText: String) {
        val index = _messages.value.indexOfFirst { it.id == messageId }
        if (index != -1) {
            _messages.update {
                it.toMutableList().apply {
                    val message = this[index]
                    this[index] = message.copy(text = newText)
                }
            }

            Toast.makeText(app, "Message updated", Toast.LENGTH_SHORT).show()

            if (_messages.value[index].isUser) {
                //simulateAIResponse()
            }
        }
    }


    private fun getPdfName(uri: Uri): String {
        return app.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (index >= 0) cursor.getString(index) else "document.pdf"
        } ?: "document.pdf"
    }

    fun onVoiceRecorded(text: String) {
        _uiState.update {
            _uiState.value.copy(
                transcribedText = text,
                showVoicePreview = false,
                message = text,
                isRecording = false
            )
        }
    }

    fun onPartialVoiceResult(partialText: String) {
        _uiState.update {
            it.copy(
                message = partialText,  // Update message with partial text
                transcribedText = partialText
            )
        }
    }


    //    fun showTranscribedText() {
//        _uiState.update { _uiState.value.copy(
//            message = _uiState.value.transcribedText,
//            showVoicePreview = false
//        ) }
//    }
    fun showTranscribedText() {
        _uiState.update {
            it.copy(
                message = it.transcribedText,
                showVoicePreview = false
            )
        }
    }

//    fun clearVoicePreview() {
//        _uiState.update { it.copy(showVoicePreview = false, transcribedText = "") }
//    }

    fun clearVoicePreview() {
        _uiState.update {
            it.copy(
                showVoicePreview = false,
                transcribedText = "",
                message = ""  // Also clear message
            )
        }
    }

    fun clearEdit() {
        editingMessageId = null
        val selectedProfile = _uiState.value.selectedProfile
        _uiState.update { ChatInputState1(selectedProfile = selectedProfile) }
    }


    fun onChatScreenOpened(
        chatId: Int,
        type: String,
        message: String?,
        familyMemberId: Int,
         images: List<Uri> = emptyList(),
         pdfs: List<PdfData> = emptyList()
    ) {
        if (chatId == 0 && type == "normal") {
            when {
                images.isNotEmpty() -> {
                    _messages.update { list ->
                        list + ChatMessage(
                            images = images,
                            isUser = true
                        )
                    }
                }

                pdfs.isNotEmpty() -> {
                    _messages.update { list ->
                        list + ChatMessage(
                            pdfs = pdfs,
                            isUser = true
                        )
                    }
                }
            }

            message?.let {
                _messages.update { list ->
                    list + ChatMessage(
                        text = it,
                        isUser = true
                    )
                }
            }

            sendInitialChatRequest(
                message = message,
                chatId = chatId,
                familyMemberId = familyMemberId,
                type = type,
                images = images,
                pdfs = pdfs
            )
        }
    }

    private fun sendInitialChatRequest(
        message: String?,
        chatId: Int,
        familyMemberId: Int,
        type: String,
        images: List<Uri> = emptyList(),
        pdfs: List<PdfData> = emptyList()
    ) {
        viewModelScope.launch {

            if (message.isNullOrBlank() && images.isEmpty() && pdfs.isEmpty()) {
                return@launch
            }

            val messageBody = message?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: return@launch

            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())

            val chatIdBody = if (chatId == 0) null
            else chatId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            Log.d("TESTING_CHAT_ID","CHAT ID VALUE IS"+ chatId)
            val familyBody = if (familyMemberId == 0) null
            else familyMemberId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            var  profileImage : MultipartBody.Part? = null

            if(images.size > 0){

                UriToRequestBody.uriToMultipart(context, images.first(), "file")?.let {
                    profileImage = it
                }

            }else{
                pdfs.firstOrNull()?.uri?.let { uri ->
                    UriToRequestBody.uriToMultipart(
                        context = context,
                        uri = uri,
                          "file"
                    )?.let {
                        profileImage = it
                    }
                }

            }

            LoaderManager.show()
            repository.getChatResponse(
                familyMemberId = familyBody,
                message = messageBody,
                type = typeBody,
                chatId = chatIdBody,
                profile_image = profileImage
            ).collect { result ->

                when (result) {

                    is NetworkResult.Success -> {
                        _chatArgs.value = _chatArgs.value.copy(
                            chatId = result.data?.chatId?.toInt() ?: 0
                        )
                        _messages.update { (it + result.data) as List<ChatMessage> }
                        LoaderManager.hide()
                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        _messages.update {
                            it + ChatMessage(
                                text = result.message,
                                isUser = false
                            )
                        }
                    }

                    is NetworkResult.Loading -> {

                        // optional typing UI
                    }
                }
            }
        }
    }


}


//@HiltViewModel
//class ChatViewModel @Inject constructor(
//    private val app: Application
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(ChatInputState())
//    val uiState: StateFlow<ChatInputState> = _uiState
//
//    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val messages: StateFlow<List<ChatMessage>> = _messages
//
//    fun onMessageChange(newText: String) {
//        _uiState.update { it.copy(message = newText.take(1000)) }
//    }
//
//    fun addImage(uri: Uri) {
//        _uiState.update { it.copy(images = it.images + uri) }
//    }
//
//    fun removeImage(uri: Uri) {
//        _uiState.update { it.copy(images = it.images - uri) }
//    }
//
//    fun addPdf(uri: Uri) {
//        val name = getPdfName(uri)
//        _uiState.update { it.copy(pdfs = it.pdfs + PdfData(uri, name)) }
//    }
//
//    fun removePdf(pdf: PdfData) {
//        _uiState.update { it.copy(pdfs = it.pdfs - pdf) }
//    }
//
//    fun toggleRecording() {
//        _uiState.update { it.copy(isRecording = !it.isRecording) }
//    }
//
//    fun stopRecording() {
//        _uiState.update { it.copy(isRecording = false) }
//    }
//
//    fun sendMessageFromInput() {
//        val s = _uiState.value
//        if (s.message.isBlank() && s.images.isEmpty() && s.pdfs.isEmpty()) return
//
//        val msg = ChatMessage(
//            text = s.message.takeIf { it.isNotBlank() },
//            isUser = true,
//            images = s.images,
//            pdfs = s.pdfs
//        )
//        _messages.update { it + msg }
//
//        // reset input
//        _uiState.update { ChatInputState() }
//
//        // Example: simulate AI response
//        viewModelScope.launch {
//            delay(800)
//            _messages.update {
//                it + ChatMessage(text = "Thanks — I got that. Here's a sample reply.", isUser = false)
//            }
//        }
//    }
//
//    // Helper to read filename from content resolver
//    private fun getPdfName(uri: Uri): String {
//        return app.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            cursor.moveToFirst()
//            if (index >= 0) cursor.getString(index) else "document.pdf"
//        } ?: "document.pdf"
//    }
//    fun onVoiceRecorded(text: String) {
//        _uiState.value = _uiState.value.copy(
//            transcribedText = text,
//            showVoicePreview = true,
//            message = ""   // input box empty rahe
//        )
//    }
//    fun showTranscribedText() {
//        _uiState.value = _uiState.value.copy(
//            message = _uiState.value.transcribedText,
//            showVoicePreview = false
//        )
//    }
//
//    fun clearVoicePreview() {
//        _uiState.update { it.copy(showVoicePreview = false, transcribedText = "") }
//    }
//
//}

// Create Profile.kt in your data/model folder
//data class Profile(
//    val id: String,
//    val name: String,
//    val iconResId: Int,
//    val description: String
//)

