package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.data.ApiService

class AdminRepository(private val api: ApiService) {

    suspend fun getHealth() =
        api.getHealth()

    suspend fun getMetrics() =
        api.getMetrics()

    suspend fun getVersion() =
        api.getVersion()

    suspend fun getRoot() =
        api.getRoot()
}