package com.Siddharth.SafeSteps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Siddharth.SafeSteps.PreferencesHelper
import com.Siddharth.SafeSteps.data.*
import com.Siddharth.SafeSteps.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val isEmail = username.contains("@")
                val formattedPhone = if (isEmail) "" else {
                    if (username.startsWith("+")) username else "+91$username"
                }
                
                val request = if (isEmail) {
                    SupabaseSignInRequest(email = username, password = password)
                } else {
                    SupabaseSignInRequest(phone = formattedPhone, password = password)
                }

                val response = SupabaseClient.authService.signIn(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    request = request
                )

                val token = response.access_token
                if (!token.isNullOrBlank()) {
                    preferencesHelper.saveAccessToken(token)
                    
                    // Retrieve metadata to populate local user profile
                    val metadata = response.user?.user_metadata
                    val fullName = metadata?.get("full_name") as? String ?: "User"
                    val rawPhone = response.user?.phone ?: formattedPhone
                    
                    var countryCode = "+91"
                    var localPhone = rawPhone
                    if (rawPhone.startsWith("+")) {
                        if (rawPhone.startsWith("+91") && rawPhone.length > 3) {
                            countryCode = "+91"
                            localPhone = rawPhone.substring(3)
                        } else if (rawPhone.length > 3) {
                            countryCode = rawPhone.substring(0, 3)
                            localPhone = rawPhone.substring(3)
                        }
                    }
                    
                    // Save local profile data
                    preferencesHelper.saveUserData(
                        name = fullName,
                        age = "20",
                        phone1 = "",
                        phone2 = "",
                        countryCode1 = "",
                        countryCode2 = ""
                    )
                    preferencesHelper.saveOwnPhone(localPhone, countryCode)
                    
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Authentication failed: No access token returned.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.localizedMessage ?: "Failed to sign in.")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String,
        countryCode: String,
        password: String
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val fullPhone = countryCode + phone
                val userMetadata = mapOf(
                    "full_name" to name,
                    "phone" to fullPhone
                )
                
                val request = SupabaseSignUpRequest(
                    email = email,
                    password = password,
                    phone = fullPhone,
                    data = userMetadata
                )

                val response = SupabaseClient.authService.signUp(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    request = request
                )

                // Save access token if automatically returned (auto-confirm enabled)
                val token = response.access_token
                if (!token.isNullOrBlank()) {
                    preferencesHelper.saveAccessToken(token)
                }
                
                // Save user profile details locally
                preferencesHelper.saveUserData(
                    name = name,
                    age = "20",
                    phone1 = "",
                    phone2 = "",
                    countryCode1 = "",
                    countryCode2 = ""
                )
                preferencesHelper.saveOwnPhone(phone, countryCode)
                
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed.")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                preferencesHelper.saveAccessToken("")
                preferencesHelper.setAppSetupDone(false)
                preferencesHelper.clearActiveSession()
                _authState.value = AuthState.Idle
            }
        }
    }

    fun getMe() {
        viewModelScope.launch {
            try {
                repository.getMe()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}