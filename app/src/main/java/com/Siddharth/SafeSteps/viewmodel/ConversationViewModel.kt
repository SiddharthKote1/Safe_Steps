package com.Siddharth.SafeSteps.viewmodel

import com.Siddharth.SafeSteps.conversationdataclass.ConversationResponse
import com.Siddharth.SafeSteps.conversiondataclass.ConversationRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.repository.ConversationRepository

class ConversationViewModel(private val repository: ConversationRepository) : ViewModel() {

    var response by mutableStateOf<ConversationResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun sendMessage(
        request: ConversationRequest
    ) {

        viewModelScope.launch {

            try {

                isLoading = true

                response =
                    repository.sendMessage(
                        request
                    )

            } catch (e: Exception) {

                error = e.message

            } finally {

                isLoading = false
            }
        }
    }
}