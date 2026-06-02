package com.Siddharth.SafeSteps.locationdataclass

data class LocationUpdateResponse(
    val success: Boolean,
    val maps_link: String?,
    val is_first_update: Boolean?
)
