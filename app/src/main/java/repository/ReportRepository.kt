package repository

import data.RetrofitClient

class ReportRepository {

    private val api = RetrofitClient.apiService

    suspend fun generateReport(
        sessionId: String
    ) = api.generateReport(sessionId)

    suspend fun getReport(
        sessionId: String
    ) = api.getReport(sessionId)
}