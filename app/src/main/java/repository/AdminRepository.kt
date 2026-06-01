package repository

import data.RetrofitClient

class AdminRepository {

    private val api = RetrofitClient.apiService

    suspend fun getHealth() =
        api.getHealth()

    suspend fun getMetrics() =
        api.getMetrics()

    suspend fun getVersion() =
        api.getVersion()

    suspend fun getRoot() =
        api.getRoot()
}