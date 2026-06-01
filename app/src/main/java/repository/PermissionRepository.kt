package repository

import data.RetrofitClient

class PermissionRepository {

    private val api = RetrofitClient.apiService

    suspend fun getPermissions() =
        api.getPermissions()
}