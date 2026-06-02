package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.data.ApiService

class EmergencyRepository(private val api: ApiService) {

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