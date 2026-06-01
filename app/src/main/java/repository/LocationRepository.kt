package repository

import LocationDataClass.LocationUpdateRequest
import data.RetrofitClient

class LocationRepository {

    private val api = RetrofitClient.apiService

    suspend fun updateLocation(
        request: LocationUpdateRequest
    ) = api.updateLocation(request)

    suspend fun getLatestLocation(
        sessionId: String
    ) = api.getLatestLocation(sessionId)

    suspend fun getLocationHistory(
        sessionId: String
    ) = api.getLocationHistory(sessionId)
}