package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.data.ApiService

class PermissionRepository(private val api: ApiService) {

    suspend fun getPermissions() =
        api.getPermissions()
}