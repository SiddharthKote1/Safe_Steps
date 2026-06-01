package repository

import ProfilesDataclass.*
import data.RetrofitClient

class ProfileRepository {

    private val api = RetrofitClient.apiService

    suspend fun getProfile() =
        api.getProfile()

    suspend fun updateProfile(request: ProfileUpdateRequest) =
        api.updateProfile(request)

    suspend fun updateLanguage(request: LanguageUpdateRequest) =
        api.updateLanguage(request)

    suspend fun updateSim(request: SimUpdateRequest) =
        api.updateSim(request)

    suspend fun getSettings() =
        api.getSettings()

    suspend fun updateSettings(request: SettingsUpdateRequest) =
        api.updateSettings(request)
}