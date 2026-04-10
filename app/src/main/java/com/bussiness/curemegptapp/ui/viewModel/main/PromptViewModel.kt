package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.chatModel.PromptQuestion
import com.bussiness.curemegptapp.apimodel.chatModel.PromptQuestionResponse
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _promptQuestions = MutableStateFlow<PromptQuestionResponse>(PromptQuestionResponse())
    val promptQuestions: StateFlow<PromptQuestionResponse> = _promptQuestions

    private val _selectedUser = MutableStateFlow<FamilyDetails?>(null)
    val selectedUser: StateFlow<FamilyDetails?> = _selectedUser

    fun selectUser(user: FamilyDetails) {
        _selectedUser.value = user
    }

    // STATE
    private val _currentMessage = MutableStateFlow<ChatMessage?>(null)
    val currentMessage: StateFlow<ChatMessage?> = _currentMessage

    init {
        getPromptQuestions()
    }

    fun setMessage(message: ChatMessage) {
        _currentMessage.value = message
    }
    fun getMessage(): ChatMessage? {
        return _currentMessage.value
    }

    fun clearMessage() {
        _currentMessage.value = null
    }

    fun getPromptQuestions() {
        viewModelScope.launch {
             LoaderManager.show()
            repository.getPromptQuestions().collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        it.data?.let {
                            _promptQuestions.value = it
                        }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {
                        LoaderManager.hide()
                    }
                }

            }
        }
    }


}