package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.data.ApiService

class AnalyticsRepository(private val api: ApiService) {

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