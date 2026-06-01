package repository

import data.RetrofitClient

class AnalyticsRepository {

    private val api = RetrofitClient.apiService

    suspend fun getAnalyticsOverview() =
        api.getAnalyticsOverview()

    suspend fun getAnalytics() =
        api.getAnalytics()

    suspend fun getIncidentTypes() =
        api.getIncidentTypes()

    suspend fun getSeverity() =
        api.getSeverity()

    suspend fun getMonthly() =
        api.getMonthly()

    suspend fun getTrends() =
        api.getTrends()
}