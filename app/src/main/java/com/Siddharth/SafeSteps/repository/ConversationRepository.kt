package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.conversiondataclass.ConversationRequest
import com.Siddharth.SafeSteps.data.ApiService

class ConversationRepository(private val api: ApiService) {

    suspend fun sendMessage(
        request: ConversationRequest
    ) = api.sendMessage(request)
}