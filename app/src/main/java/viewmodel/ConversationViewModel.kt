package viewmodel

import ConversionDataClass.ConversationRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.ConversationRepository

class ConversationViewModel : ViewModel() {

    private val repository = ConversationRepository()

    fun sendMessage(
        request: ConversationRequest
    ) {

        viewModelScope.launch {

            repository.sendMessage(request)
        }
    }
}