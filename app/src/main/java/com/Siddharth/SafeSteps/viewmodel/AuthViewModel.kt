package com.Siddharth.SafeSteps.viewmodel

import com.Siddharth.SafeSteps.authdataclass.LoginRequest
import com.Siddharth.SafeSteps.authdataclass.RegisterRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.repository.AuthRepository

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    fun login(phone: String, password: String? = null) {

        viewModelScope.launch {

            try {

                val response =
                    repository.login(
                        LoginRequest(phone, password)
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    fun register(
        request: RegisterRequest
    ) {

        viewModelScope.launch {

            try {

                repository.register(request)

            } catch (e: Exception) {

                e.printStackTrace()
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