package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.profilesdataclass.*
import com.Siddharth.SafeSteps.data.ApiService

class ProfileRepository(private val api: ApiService) {

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