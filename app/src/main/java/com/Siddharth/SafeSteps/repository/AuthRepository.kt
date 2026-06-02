package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.authdataclass.LoginRequest
import com.Siddharth.SafeSteps.authdataclass.RegisterRequest
import com.Siddharth.SafeSteps.data.ApiService

class AuthRepository(private val api: ApiService) {

    suspend fun register(request: RegisterRequest) =
        api.register(request)

    suspend fun login(request: LoginRequest) =
        api.login(request)

    suspend fun logout() =
        api.logout()

    suspend fun getMe() =
        api.getMe()
}