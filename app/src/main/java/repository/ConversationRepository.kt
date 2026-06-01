package repository

import ConversionDataClass.ConversationRequest
import data.RetrofitClient

class ConversationRepository {

    private val api = RetrofitClient.apiService

    suspend fun sendMessage(
        request: ConversationRequest
    ) = api.sendMessage(request)
}