package viewmodel

import AuthDataClass.LoginRequest
import AuthDataClass.RegisterRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun login(phone: String) {

        viewModelScope.launch {

            try {

                val response =
                    repository.login(
                        LoginRequest(phone)
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