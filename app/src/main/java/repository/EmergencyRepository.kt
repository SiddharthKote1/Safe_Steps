package repository

import data.RetrofitClient

class EmergencyRepository {

    private val api = RetrofitClient.apiService

    suspend fun startSession() =
        api.startSession()

    suspend fun endSession() =
        api.endSession()

    suspend fun getCurrentSession() =
        api.getCurrentSession()

    suspend fun getIncidents() =
        api.getIncidents()

    suspend fun getHistory() =
        api.getHistory()

    suspend fun triggerSos() =
        api.triggerSos()

    suspend fun getSessionById(sessionId: String) =
        api.getSessionById(sessionId)
}