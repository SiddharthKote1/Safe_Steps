package AnalyticsDataClass

data class AnalyticsResponse(
    val totalEncounters: Int,
    val avgResponseLimit: String,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int
)
