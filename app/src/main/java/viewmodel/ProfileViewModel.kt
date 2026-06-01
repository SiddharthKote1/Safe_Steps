package viewmodel

import ProfilesDataclass.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.ProfileRepository

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    fun getProfile() {

        viewModelScope.launch {

            repository.getProfile()
        }
    }

    fun updateProfile(
        request: ProfileUpdateRequest
    ) {

        viewModelScope.launch {

            repository.updateProfile(request)
        }
    }

    fun updateLanguage(
        request: LanguageUpdateRequest
    ) {

        viewModelScope.launch {

            repository.updateLanguage(request)
        }
    }

    fun updateSim(
        request: SimUpdateRequest
    ) {

        viewModelScope.launch {

            repository.updateSim(request)
        }
    }

    fun getSettings() {

        viewModelScope.launch {

            repository.getSettings()
        }
    }

    fun updateSettings(
        request: SettingsUpdateRequest
    ) {

        viewModelScope.launch {

            repository.updateSettings(request)
        }
    }
}