package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.locationdataclass.LocationUpdateRequest
import com.Siddharth.SafeSteps.data.ApiService

class LocationRepository(private val api: ApiService) {

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