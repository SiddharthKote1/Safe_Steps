package repository

import TtsDataClass.TtsRequest
import data.RetrofitClient

class TtsRepository {

    private val api = RetrofitClient.apiService

    suspend fun synthesizeSpeech(
        request: TtsRequest
    ) = api.synthesizeSpeech(request)
}