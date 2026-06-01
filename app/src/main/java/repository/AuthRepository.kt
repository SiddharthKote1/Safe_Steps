package repository

import AuthDataClass.LoginRequest
import AuthDataClass.RegisterRequest
import data.RetrofitClient

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun register(request: RegisterRequest) =
        api.register(request)

    suspend fun login(request: LoginRequest) =
        api.login(request)

    suspend fun logout() =
        api.logout()

    suspend fun getMe() =
        api.getMe()
}