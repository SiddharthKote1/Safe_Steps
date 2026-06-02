package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.ttsdataclass.TtsRequest
import com.Siddharth.SafeSteps.data.ApiService

class TtsRepository(private val api: ApiService) {

    suspend fun synthesizeSpeech(
        request: TtsRequest
    ) = api.synthesizeSpeech(request)
}