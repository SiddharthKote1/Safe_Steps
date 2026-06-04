package com.Siddharth.SafeSteps.viewmodel

import com.Siddharth.SafeSteps.authdataclass.LoginRequest
import com.Siddharth.SafeSteps.authdataclass.RegisterRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.PreferencesHelper
import com.Siddharth.SafeSteps.repository.AuthRepository

class AuthViewModel(
    private val repository: AuthRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    fun login(
        phone: String,
        password: String? = null,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.login(
                        LoginRequest(phone, password)
                    )

                // Persist the JWT so AuthInterceptor can attach it to every
                // authenticated request. Without this, calls like emergency/start
                // return HTTP 401 and SOS cannot be triggered.
                if (response.token.isNotBlank()) {
                    preferencesHelper.saveAccessToken(response.token)
                    onResult(true, null)
                } else {
                    onResult(false, "Login failed")
                }

            } catch (e: Exception) {

                e.printStackTrace()
                onResult(false, e.message)
            }
        }
    }

    fun register(
        request: RegisterRequest,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {

        viewModelScope.launch {

            try {

                val response = repository.register(request)

                if (response.token.isNotBlank()) {
                    preferencesHelper.saveAccessToken(response.token)
                    onResult(true, null)
                } else {
                    onResult(false, "Registration failed")
                }

            } catch (e: Exception) {

                e.printStackTrace()
                onResult(false, e.message)
            }
        }
    }

    fun logout() {

        viewModelScope.launch {

            repository.logout()
        }
    }

    fun getMe() {

        viewModelScope.launch {

            repository.getMe()
        }
    }
}