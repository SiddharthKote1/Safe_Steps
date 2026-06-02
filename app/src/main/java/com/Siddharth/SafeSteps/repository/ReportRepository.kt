package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.data.ApiService

class ReportRepository(private val api: ApiService) {

    suspend fun generateReport(
        sessionId: String
    ) = api.generateReport(sessionId)

    suspend fun getReport(
        sessionId: String
    ) = api.getReport(sessionId)
}