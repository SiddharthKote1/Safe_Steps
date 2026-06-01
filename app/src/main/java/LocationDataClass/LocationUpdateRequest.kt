package LocationDataClass

data class LocationUpdateRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double,
    val speed: Double,
    val heading: Double
)
