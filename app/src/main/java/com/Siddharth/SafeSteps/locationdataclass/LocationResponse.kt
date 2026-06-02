package com.Siddharth.SafeSteps.locationdataclass

data class LocationResponse(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val accuracy: Double,
    val speed: Double,
    val heading: Double,
    val maps_link: String
)